package net.sf.freecol.common.model.production;

import net.sf.freecol.common.model.Game;
import net.sf.freecol.common.model.Modifier;
import net.sf.freecol.common.model.Specification;
import net.sf.freecol.common.model.Turn;

import java.util.stream.Stream;

public class SeasonEffect {

    private final Turn turn;

    //seasons of the year and the arbitrary constants that define them
    private static final int WINTER = 0;
    private static final int SPRING = 1;
    private static final int SUMMER = 2;
    private static final int AUTUMN = 3;

    // Effect on season (Percentage)
    private int WINTER_EFFECT;
    private int AUTUMN_EFFECT;
    private int SPRING_EFFECT;
    private int SUMMER_EFFECT;

    private final int WINTER_VERY_EASY = 0;
    private final int WINTER_EASY = -10;
    private final int WINTER_MEDIUM = -25;
    private final int WINTER_HARD = -50;
    private final int WINTER_VERY_HARD = -75;

    private final int SPRING_VERY_EASY = 75;
    private final int SPRING_EASY = 50;
    private final int SPRING_MEDIUM = 25;
    private final int SPRING_HARD = 10;
    private final int SPRING_VERY_HARD = 0;

    private final int SUMMER_VERY_EASY = 50;
    private final int SUMMER_EASY = 30;
    private final int SUMMER_MEDIUM = 20;
    private final int SUMMER_HARD = 10;
    private final int SUMMER_VERY_HARD = 0;

    private final int AUTUMN_VERY_EASY = 0;
    private final int AUTUMN_EASY = -10;
    private final int AUTUMN_MEDIUM = -20;
    private final int AUTUMN_HARD = -30;
    private final int AUTUMN_VERY_HARD = -50;

    private final String VERY_EASY = "model.difficulty.veryEasy";
    private final String EASY = "model.difficulty.easy";
    private final String MEDIUM = "model.difficulty.medium";
    private final String HARD = "model.difficulty.hard";
    private final String VERY_HARD = "model.difficulty.veryHard";

    public SeasonEffect(Turn turn, String difficultyLevel) {
        this.turn = turn;
        applyDifficulty(difficultyLevel);
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

    private void applyDifficulty(String difficultyLevel){
        switch (difficultyLevel){
            case VERY_EASY:
                setSeasonsEffect(WINTER_VERY_EASY, SPRING_VERY_EASY, SUMMER_VERY_EASY, AUTUMN_VERY_EASY);
                break;
            case EASY:
                setSeasonsEffect(WINTER_EASY, SPRING_EASY, SUMMER_EASY, AUTUMN_EASY);
                break;
            case HARD:
                setSeasonsEffect(WINTER_HARD, SPRING_HARD, SUMMER_HARD, AUTUMN_HARD);
                break;
            case VERY_HARD:
                setSeasonsEffect(WINTER_VERY_HARD, SPRING_VERY_HARD, SUMMER_VERY_HARD, AUTUMN_VERY_HARD);
                break;
            case MEDIUM:
            default:
                setSeasonsEffect(WINTER_MEDIUM, SPRING_MEDIUM, SUMMER_MEDIUM, AUTUMN_MEDIUM);
        }
    }

    private void setSeasonsEffect(int winter, int spring, int summer, int autumn){
        WINTER_EFFECT = winter;
        SPRING_EFFECT = spring;
        SUMMER_EFFECT = summer;
        AUTUMN_EFFECT = autumn;
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
