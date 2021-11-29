package yuyang.hyy.game.carcassonne.core;

import java.util.Collections;
import java.util.Stack;

/**
 * The deck to store the stack of tiles
 */
public class Deck {
    private final Stack<Tile> tileStack;

    /**
     * Create a tile deck from a json file.
     * @param jsonFileName name of json file.
     */
    Deck(String jsonFileName) {
        tileStack = JSONReader.parse(jsonFileName);
        // For test use, we don't shuffle in the constructor.
    }

    /**
     * Add a tile to the stack. Only for test use. Never used during real game.
     * @param newTile new tile to add
     */
    void add(Tile newTile) {
        tileStack.push(newTile);
    }

    /**
     * Peek the next tile.
     * @return the next tile from the stack
     */
    Tile checkNextTile() {
        return tileStack.peek();
    }

    /**
     * Pop the next tile.
     * @return poped tile
     */
    public Tile nextTile() {
        return tileStack.pop();
    }

    /**
     * Check if the game has finished.
     * @return stack is empty or not
     */
    public boolean isEmpty() {
        return tileStack.isEmpty();
    }

    /**
     * Shuffle the tiles.
     */
    public void shuffle() {
        Collections.shuffle(tileStack);
    }


}
