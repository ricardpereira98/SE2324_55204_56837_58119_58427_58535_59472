package net.sf.freecol.common.model.production;

import net.sf.freecol.common.model.Game;
import net.sf.freecol.common.model.Modifier;
import net.sf.freecol.common.model.Specification;
import net.sf.freecol.common.model.Turn;

import java.util.stream.Stream;

public class SeasonEffect {

    Turn turn;

    //seasons of the year and the arbitrary constants that define them
    private static final int WINTER = 0;
    private static final int SPRING = 1;
    private static final int SUMMER = 2;
    private static final int AUTUMN = 3;

    // Effect on season (Percentage)
    private final int WINTER_EFFECT = -20 ;
    private final int AUTUMN_EFFECT = -15;
    private final int SPRING_EFFECT = 15;
    private final int SUMMER_EFFECT = 20;


    public SeasonEffect(Turn turn) {
        this.turn = turn;
    }

    public int getWinterEffect() {
        return WINTER_EFFECT;
    }

    public int getAutumnEffect() {
        return AUTUMN_EFFECT;
    }

    public int getSpringEffect() {
        return SPRING_EFFECT;
    }

    public int getSummerEffect() {
        return SUMMER_EFFECT;
    }

    public Stream<Modifier> getSeasonModifierStream() {
        switch (turn.getSeason()){
            case WINTER:
                return Stream.of(getWinterMod());
            case SPRING:
                return Stream.of(getSpringMod());
            case SUMMER:
                return Stream.of(getSummerMod());
            case AUTUMN:
                return Stream.of(getAutumnMod());
            default: return null;
        }
    }

    private Modifier getWinterMod() {
        return new Modifier(Modifier.TILE_SEASON_EFFECT, WINTER_EFFECT, Modifier.ModifierType.PERCENTAGE, Specification.TILE_SEASON_EFFECT);
    }

    private Modifier getSpringMod() {
        return new Modifier(Modifier.TILE_SEASON_EFFECT, SPRING_EFFECT, Modifier.ModifierType.PERCENTAGE, Specification.TILE_SEASON_EFFECT);
    }

    private Modifier getSummerMod() {
        return new Modifier(Modifier.TILE_SEASON_EFFECT, SUMMER_EFFECT, Modifier.ModifierType.PERCENTAGE, Specification.TILE_SEASON_EFFECT);
    }

    private Modifier getAutumnMod() {
        return new Modifier(Modifier.TILE_SEASON_EFFECT, AUTUMN_EFFECT, Modifier.ModifierType.PERCENTAGE, Specification.TILE_SEASON_EFFECT);
    }
}
