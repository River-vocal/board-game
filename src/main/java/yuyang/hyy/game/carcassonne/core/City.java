package yuyang.hyy.game.carcassonne.core;

import java.util.ArrayList;
import java.util.List;

/**
 * The City feature.
 */
public class City extends ContinuousFeature {

    private static final int TWO = 2;
    private int noOfShields = 0;

    /**
     * No. of coat-of-arms getter.
     * @return No. of tiles in this feature which has a coat of arm.
     */
    public int getNoOfShields() {
        return noOfShields;
    }

    /**
     * Constructor.
     * @param firstTile the starting tile of this feature (always = currently placed tile in one turn)
     * @param pointList the open end location/orientation pairs.
     */
    public City(Tile firstTile, List<LocOriPair> pointList) {
        List<Tile> tmpTileList = new ArrayList<>();
        setUnfinishedPoints(new ArrayList<>(pointList));
        setAllPoints(new ArrayList<>(pointList));
        setType(Segment.CITY);
        tmpTileList.add(firstTile);
        setTileList(tmpTileList);
        if (firstTile.isShield()) {
            noOfShields = 1;
        }
    }

    @Override
    public boolean combineFeature(ContinuousFeature anotherFea) {
        if(super.combineFeature(anotherFea)) {
            for (Tile tmpTile : anotherFea.getTileList()) {
                if (tmpTile.isShield()) {
                    noOfShields++;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean addTile(Tile newTile) {
        if (newTile.isShield()) {
            noOfShields++;
        }
        return getTileList().add(newTile);
    }

    @Override
    public int getScore() {
        return (getTileList().size() + noOfShields) * TWO;
    }

    @Override
    public int getEndScore() {
        return (getTileList().size() + noOfShields);
    }
}
