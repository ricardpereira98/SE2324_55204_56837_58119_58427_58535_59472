package net.sf.freecol.common.model.production;

import net.sf.freecol.common.model.Game;
import net.sf.freecol.common.model.Turn;

public class SeasonEffect {

    Turn turn;

    //seasons of the year and the arbitrary constants that define them
    private static final int WINTER = 0;
    private static final int SPRING = 1;
    private static final int SUMMER = 2;
    private static final int AUTUMN = 3;
    private static final double WINTER_NERF = 0.8 ;
    private static final double AUTUMN_NERF = 0.85;
    private static final double SPRING_BUFF = 1.15;
    private static final double SUMMER_BUFF = 1.2;



    public SeasonEffect(Game game) {
        this.turn = game.getTurn();
    }

    public SeasonEffect(Turn turn) {
        this.turn = turn;
    }

    public int getSeasonProduction(){
        int season = turn.getSeason();

        return switch (season) {
            case WINTER -> 1;
            case SPRING -> 2;
            case SUMMER -> 3;
            case AUTUMN -> 5;
            default -> 1;
        };
    }

    public double getCenterTileProductionEffect(){

    }

}
