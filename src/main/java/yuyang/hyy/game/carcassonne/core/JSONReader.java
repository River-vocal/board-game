package yuyang.hyy.game.carcassonne.core;

import com.google.gson.Gson;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Stack;

/**
 * Class to read a json file.
 */
public class JSONReader {
    /**
     * Constructor to create a json tile.
     */
    //CHECKSTYLE:OFF
    static class TileInJson {
        private int index;
        private int quantity;
        private Segment top, right, down, left, center;
        private boolean isShield;
    }

    /**
     * Constructor to create a json tile list.
     */
    static class JSONTileList {
        private String name;
        private TileInJson[] tileList;
    }
    //CHECKSTYLE:ON
    /**
     * Method to parse from a json file and return a stack.
     * @param configFile the file name which contains tile settings.
     * @return a stack of tiles
     */
    public static Stack<Tile> parse(String configFile) {
        Gson gson = new Gson();
        Stack<Tile> initialStack = new Stack<>();
        try (Reader reader = new FileReader(new File(configFile), StandardCharsets.UTF_8)) {
            JSONTileList result = gson.fromJson(reader, JSONTileList.class);
            for (TileInJson tile : result.tileList) {
                for (int i = 0; i < tile.quantity; i++) {
                    Tile newTile = new Tile(tile.index, tile.top, tile.right, tile.down, tile.left,
                            tile.center, tile.isShield);
                    initialStack.push(newTile);
                }
            }
            return initialStack;
        } catch (IOException e) {
            throw new IllegalArgumentException("Error when reading file: " + configFile, e);
        }
    }
}
