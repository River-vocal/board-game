package yuyang.hyy.game.carcassonne.core;

import java.util.List;

/**
 * The interface for Continuous Features (road & city) and Monastery.
 */
public interface BasicFeature {
    /**
     * Feature type getter.
     * @return type of this feature
     */
    Segment getType();

    /**
     * Check whether a feature is completed
     * @return boolean value
     */
    boolean isComplete();

    /**
     * Check whether a feature contains a player's meeple.
     * @return boolean value
     */
    boolean hasMeeple();

    /**
     * Directly add a new tile to the tileList (Continuous Features) or finish a vacancy of Monastery feature.
     * @param newTile new tile added
     * @return successfully added or not
     */
    boolean addTile(Tile newTile);

    /**
     * Add one meeple to the feature.
     * @param newMeeple one meeple from one player.
     * @return true if this feature has no meeple thus meeple successfully added, false otherwise
     */
    boolean addMeeple(Meeple newMeeple);

    /**
     * Empty the meeple list of the feature.
     * @return the list of meeples.
     */
    List<Meeple> returnMeeples();

    /**
     * Get all the meeples from this feature.
     * @return list of meeples
     */
    List<Meeple> getMeeples();

    /**
     * Get the player(s) who own(s) the most no. of meeples in this feature.
     * @return score owners
     */
    List<Player> getScoreOwner();

    /**
     * Remove one specific meeple.
     * @param newMeeple the meeple to be removed
     * @return successfully removed or not
     */
    boolean removeMeeple(Meeple newMeeple);

    /**
     * Get the score when this feature is completed during the game.
     * @return score
     */
    int getScore();

    /**
     * Get the socre if this feature isn't finished at the end of game.
     * @return end game score
     */
    int getEndScore();
}
