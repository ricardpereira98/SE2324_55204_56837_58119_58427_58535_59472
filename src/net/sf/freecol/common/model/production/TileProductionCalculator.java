/**
 *  Copyright (C) 2002-2022   The FreeCol Team
 *
 *  This file is part of FreeCol.
 *
 *  FreeCol is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  FreeCol is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with FreeCol.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.sf.freecol.common.model.production;

import static net.sf.freecol.common.util.CollectionUtils.concat;
import static net.sf.freecol.common.util.CollectionUtils.forEach;
import static net.sf.freecol.common.util.CollectionUtils.map;

import java.util.stream.Stream;

import net.sf.freecol.common.model.AbstractGoods;
import net.sf.freecol.common.model.FeatureContainer;
import net.sf.freecol.common.model.GoodsType;
import net.sf.freecol.common.model.Modifier;
import net.sf.freecol.common.model.Player;
import net.sf.freecol.common.model.ProductionCache;
import net.sf.freecol.common.model.ProductionInfo;
import net.sf.freecol.common.model.ProductionType;
import net.sf.freecol.common.model.Tile;
import net.sf.freecol.common.model.Turn;
import net.sf.freecol.common.model.UnitType;

/**
 * Calculates the production for a tile.
 */
public class TileProductionCalculator {

    private Player owner;
    private int colonyProductionBonus;

    //seasons of the year and the arbitrary constants that define them
    private static final int WINTER = 0;
    private static final int SPRING = 1;
    private static final int SUMMER = 2;
    private static final int AUTUMN = 3;
    private static final double WINTER_NERF = 0.8 ;
    private static final double AUTUMN_NERF = 0.85;
    private static final double SPRING_BUFF = 1.15;
    private static final double SUMMER_BUFF = 1.2;

    /**
     * Creates a calculator for the given owner and colony data.
     * 
     * @param owner The {@code Player} owning the building.
     * @param colonyProductionBonus The production bonus for the colony where the building
     *      is located.
     */
    public TileProductionCalculator(Player owner, int colonyProductionBonus) {
        this.owner = owner;
        this.colonyProductionBonus = colonyProductionBonus;
    }
    
    
    /**
     * Gets the basic production information for the colony tile,
     * ignoring any colony limits (which for now, should be
     * irrelevant).
     *
     * In the original game, the following special rules apply to
     * colony center tiles:
     * - All tile improvements contribute to the production of food
     * - Only natural tile improvements, such as rivers, contribute
     *   to the production of other types of goods.
     * - Artificial tile improvements, such as plowing, are ignored.
     *
     * @param tile The {@code Tile} where the production is happening.
     * @param turn The current game turn.
     * @param workerAssignment If any, the worker assign to this tile.
     * @param colonyCenterTile If true, then the tile will autoproduce.
     * @return The raw production of this colony tile.
     * @see ProductionCache#update
     */
    public ProductionInfo getBasicProductionInfo(Tile tile, Turn turn, WorkerAssignment workerAssignment, boolean colonyCenterTile) {

        ProductionInfo pi = new ProductionInfo();
        
        if (workerAssignment.getProductionType() == null) {
            /*
             *  XXX: It's silly that the production is calculated
             *       before the productionType is set.
             */
             
            return pi;
        }

        if (colonyCenterTile) {
            forEach(workerAssignment.getProductionType().getOutputs(), output -> {
                    int n = getCenterTileProduction(turn, tile, output.getType());
                    if (n > 0) {
                        pi.addProduction(new AbstractGoods(output.getType(), n));
                    }
                });
        } else {
            forEach(map(workerAssignment.getProductionType().getOutputs(), AbstractGoods::getType),
                gt -> {
                    int n = getUnitProduction(turn, tile, workerAssignment, gt);
                    if (n > 0) {
                        pi.addProduction(new AbstractGoods(gt, n));
                    }
                });
        }
        return pi;
    }
    
    /**
     * Gets the productivity of a unit working in this work location,
     * considering *only* the contribution of the unit, exclusive of
     * that of the work location.
     *
     * Used below, only public for the test suite.
     *
     * @param turn The current game turn.
     * @param tile The tile where the production is happening.
     * @param workerAssignment If any, the worker assigned to the {@code Tile}.
     * @param goodsType The {@code GoodsType} to check the production of.
     * @return The maximum return from this unit.
     */
    public int getUnitProduction(Turn turn, Tile tile, WorkerAssignment workerAssignment, GoodsType goodsType) {

        if (workerAssignment == null
                || workerAssignment.getProductionType().getOutputs().noneMatch(g -> goodsType.equals(g.getType()))
                || workerAssignment.getUnitType() == null) {
            return 0;
        }

        //0->Winter, 1->Spring, 2->Summer, 3-> Autumn
        int seasonOfTheYear = turn.getSeason();

        switch (seasonOfTheYear){
            case WINTER -> {
                int production = getBaseProduction(tile, workerAssignment.getProductionType(), goodsType, workerAssignment.getUnitType());
                //20% nerf on production
                double nerfedProduction = production * WINTER_NERF;
                production = (int) Math.floor(nerfedProduction);

                return Math.max(0, (int) FeatureContainer.applyModifiers(production, turn, getProductionModifiers(turn, tile, goodsType, workerAssignment.getUnitType())));
            }
            case SPRING -> {
                int production = getBaseProduction(tile, workerAssignment.getProductionType(), goodsType, workerAssignment.getUnitType());
                //15% buff on production
                double buffedProduction = production * SPRING_BUFF;
                production = (int) Math.ceil(buffedProduction);

                return Math.max(0, (int) FeatureContainer.applyModifiers(production, turn, getProductionModifiers(turn, tile, goodsType, workerAssignment.getUnitType())));
            }
            case SUMMER -> {
                int production = getBaseProduction(tile, workerAssignment.getProductionType(), goodsType, workerAssignment.getUnitType());
                //20% buff on production
                double buffedProduction = production * SUMMER_BUFF;
                production = (int) Math.ceil(buffedProduction);

                return Math.max(0, (int) FeatureContainer.applyModifiers(production, turn, getProductionModifiers(turn, tile, goodsType, workerAssignment.getUnitType())));
            }
            case AUTUMN -> {
                int production = getBaseProduction(tile, workerAssignment.getProductionType(), goodsType, workerAssignment.getUnitType());
                //15% nerf on production
                double nerfedProduction = production * AUTUMN_NERF;
                production = (int) Math.floor(nerfedProduction);

                return Math.max(0, (int) FeatureContainer.applyModifiers(production, turn, getProductionModifiers(turn, tile, goodsType, workerAssignment.getUnitType())));
            }
        }
        return Math.max(0, (int) FeatureContainer.applyModifiers(getBaseProduction(tile, workerAssignment.getProductionType(), goodsType, workerAssignment.getUnitType()), turn, getProductionModifiers(turn, tile, goodsType, workerAssignment.getUnitType())));
    }
    
    private int getCenterTileProduction(Turn turn, Tile tile, GoodsType goodsType) {
        final int production = tile.getBaseProduction(null, goodsType, null);
        return Math.max(0, (int) FeatureContainer.applyModifiers(
                production,
                turn,
                getCenterTileProductionModifiers(turn, tile, goodsType)));
    }
    
    /**
     * Get the base production exclusive of any bonuses.
     *
     * @param tile The tile where the production is happening.
     * @param productionType An optional {@code ProductionType} to use,
     *     if null the best available one is used.
     * @param goodsType The {@code GoodsType} to produce.
     * @param unitType An optional {@code UnitType} to use.
     * @return The base production due to tile type and resources.
     */
    private int getBaseProduction(Tile tile, ProductionType productionType,
                                 GoodsType goodsType, UnitType unitType) {
        if (tile == null || goodsType == null || !goodsType.isFarmed()) {
            return 0;
        }
        final int amount = tile.getBaseProduction(productionType, goodsType, unitType);
        return (amount < 0) ? 0 : amount;
    }
    
    /**
     * Gets the production modifiers for the given type of goods and
     * unit type.
     *
     * @param goodsType The {@code GoodsType} to produce.
     * @param unitType The optional {@code UnitType} to produce them.
     * @return A stream of the applicable modifiers.
     */
    public Stream<Modifier> getProductionModifiers(Turn turn, Tile tile, GoodsType goodsType, UnitType unitType) {
        if (unitType == null || !tile.canProduce(goodsType, unitType)) {
            return Stream.<Modifier>empty();
        }
        
        return concat(tile.getProductionModifiers(goodsType, unitType),
                unitType.getModifiers(goodsType.getId(), tile.getType(), turn),
                ((owner == null) ? null
                    : owner.getModifiers(goodsType.getId(), unitType, turn)),
                ProductionUtils.getRebelProductionModifiersForTile(tile, colonyProductionBonus, goodsType, unitType));
    }

    /**
     * Gets the production modifiers for the given type of goods on
     * the colony center tile.
     *
     * @param goodsType The {@code GoodsType} to produce.
     * @param unitType The optional {@code UnitType} to produce them.
     * @return A stream of the applicable modifiers.
     */
    public Stream<Modifier> getCenterTileProductionModifiers(Turn turn, Tile tile, GoodsType goodsType) {
        if (!tile.canProduce(goodsType, null)) {
            return Stream.<Modifier>empty();
        }
        return concat(tile.getProductionModifiers(goodsType, null),
                ProductionUtils.getRebelProductionModifiersForTile(tile, colonyProductionBonus, goodsType, null)
                // This does not seem to influence center tile production, but was present in the old code.
                //colony.getModifiers(id, null, turn),
                //((owner == null) ? null : owner.getModifiers(goodsType.getId(), tile.getType(), turn))
                );
    }

}
