package yuyang.hyy.game.carcassonne.core;

import java.util.List;

/**
 * Game change listener interface for the changes in game, including game map, tile and meeple placement.
 */
public interface GameBoardChangeListener {
    /**
     * Show the placement of the current tile (after rotations) in the game map.
     * @param t tile placed
     * @param loc location of placement
     */
    void handleTilePlacement(Tile t, Location loc);

    /**
     * Show the meeple placement.
     * @param m meeple placed
     * @param ori orientation of meeple placement
     */
    void handleMeeplePlacement(Meeple m, Orientation ori);

    /**
     * Update the info for next turn.
     */
    void updateNextTurn();

    /**
     * Clear the meeple after a feature has been completed.
     * @param m meeple to be cleared
     */
    void clearMeeple(Meeple m);

    /**
     * Update the image of "next image" after rotations.
     */
    void updateNextTileImage();

    /**
     * Show error info in the error bar.
     * @param str message string
     */
    void showError(String str);

    /**
     * Notify the game over info.
     * @param winners winner(s) of the game
     */
    void gameOver(List<Player> winners);
}
