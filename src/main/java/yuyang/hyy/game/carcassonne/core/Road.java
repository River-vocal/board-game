package yuyang.hyy.game.carcassonne.core;

import java.util.ArrayList;
import java.util.List;

/**
 * The Road feature.
 */
public class Road extends ContinuousFeature {
    /**
     * Constructor.
     * @param firstTile the starting tile of this feature (always = currently placed tile in one turn)
     * @param pointList the open end location/orientation pairs.
     */
    public Road(Tile firstTile, List<LocOriPair> pointList) {
        List<Tile> tmpTileList = new ArrayList<>();
        setUnfinishedPoints(new ArrayList<>(pointList));
        setAllPoints(new ArrayList<>(pointList));
        setType(Segment.ROAD);
        tmpTileList.add(firstTile);
        setTileList(tmpTileList);
    }

    @Override
    public int getScore() {
        return getTileList().size();
    }

    @Override
    public int getEndScore() {
        return getTileList().size();
    }
}
