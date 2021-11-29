package yuyang.hyy.game.carcassonne.core;

/**
 * The listener interface for the changes in player info/status.
 */
public interface PlayerStatusChangeListener {
    /**
     * Update the player score, meeple no. in the info board.
     * @param player the player whose information to be updated
     */
    void scoreMeepleUpdated(Player player);

    /**
     * Update the name of the current player JLabel.
     */
    void updateCurrPlayer();
}
