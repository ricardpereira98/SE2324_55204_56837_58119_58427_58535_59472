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

    // Difficulties
    private final String VERY_EASY = "model.difficulty.veryEasy";
    private final String EASY = "model.difficulty.easy";
    private final String MEDIUM = "model.difficulty.medium";
    private final String HARD = "model.difficulty.hard";
    private final String VERY_HARD = "model.difficulty.veryHard";

    // Effect on season (Percentage)
    private int WINTER_EFFECT;
    private int AUTUMN_EFFECT;
    private int SPRING_EFFECT;
    private int SUMMER_EFFECT;

    // Effect on season per difficulty (Percentage)
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


    /**
     * Creates a season effect given a game and turn.
     *
     * @param game The current Game being played.
     */
    public SeasonEffect(Game game){
        this.turn = game.getTurn();
        // Default difficulty
        applyDifficulty(MEDIUM);
    }

    /**
     * Creates a season effect given a game and turn.
     *
     * @param turn The current game turn.
     * @param difficultyLevel Games difficulty.
     */
    public SeasonEffect(Turn turn, String difficultyLevel) {
        this.turn = turn;
        applyDifficulty(difficultyLevel);
    }

    /**
     * Gets the season effect value for the current turn.
     *
     * @return The current season effect value.
     */
    public int getSeasonEffectValue(){
        switch (turn.getSeason()){
            case WINTER:
                return WINTER_EFFECT;
            case SPRING:
                return SPRING_EFFECT;
            case SUMMER:
                return SUMMER_EFFECT;
            case AUTUMN:
                return AUTUMN_EFFECT;
            default: return 0;
        }
    }


    /**
     * Given a difficulty, applies it to the season effect values.
     *
     * @param difficultyLevel The difficulty to be applied.
     */
    public void applyDifficulty(String difficultyLevel){
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

    /**
     * Sets the season effect value.
     * Auxiliary function of applyDifficulty.
     *
     * @param winter The effect value for winter season.
     * @param spring The effect value for spring season.
     * @param summer The effect value for summer season.
     * @param autumn The effect value for autumn season.
     */
    private void setSeasonsEffect(int winter, int spring, int summer, int autumn){
        WINTER_EFFECT = winter;
        SPRING_EFFECT = spring;
        SUMMER_EFFECT = summer;
        AUTUMN_EFFECT = autumn;
    }


    /**
     * Gets a Modifier for the tile production of the season.
     *
     * @return Stream with the Modifier.
     */
    public Stream<Modifier> getSeasonModifierStream() {
        Modifier mod;
        switch (turn.getSeason()){
            case WINTER: mod = getSeasonMod(WINTER_EFFECT); break;
            case SPRING: mod = getSeasonMod(SPRING_EFFECT); break;
            case SUMMER: mod = getSeasonMod(SUMMER_EFFECT); break;
            case AUTUMN: mod = getSeasonMod(AUTUMN_EFFECT); break;
            default: mod = null;
        }
        return (mod == null)? null : Stream.of(mod);
    }


    /**
     * Auxiliary function to create the season Modifier.
     *
     * @param effect The value to be applied in the season modifier.
     * @return The Modifier object.
     */
    private Modifier getSeasonMod(int effect){
        return effect == 0 ? null : new Modifier(Modifier.TILE_SEASON_EFFECT, effect, Modifier.ModifierType.PERCENTAGE, Specification.TILE_SEASON_EFFECT);
    }

}
