package yuyang.hyy.game.carcassonne.core;

import java.util.ArrayList;
import java.util.List;

/**
 * Monastery feature class. It directly implements the BasicFeature interface because it applies different logic.
 */
public class Monastery implements BasicFeature{

    private final Tile center;
    private final List<Location> unfinishedLocList;
    private List<Meeple> meeples;
    private static final int NINE = 9;
    private final Segment type = Segment.MONASTERY;

    /**
     * Constructor.
     * @param centerTile center tile containing monastery segment in its center
     */
    Monastery(Tile centerTile) {
        center = centerTile;
        Location centerLoc = centerTile.getLoc();
        unfinishedLocList = centerLoc.getAllNeighbors();
        meeples = new ArrayList<>();
    }

    /**
     * Center tile getter (containing monastery segment in its center).
     * @return center tile
     */
    public Tile getCenter() {
        return center;
    }

    /**
     * Get the location of the center monastery tile.
     * @return location
     */
    public Location getCenterLoc() {
        return center.getLoc();
    }

    /**
     * Get the list of tile locations which are vacant.
     * @return unfinished location list
     */
    public List<Location> getUnfinishedLocList() {
        return unfinishedLocList;
    }

    @Override
    public Segment getType() {
        return type;
    }

    @Override
    public boolean isComplete() {
        return unfinishedLocList.isEmpty();
    }

    @Override
    public boolean hasMeeple() {
        return !meeples.isEmpty();
    }

    @Override
    public boolean addTile(Tile newTile) {
        Location tmpLoc = newTile.getLoc();
        return unfinishedLocList.remove(tmpLoc);
    }

    /**
     * Remove one vacancy location because one tile is added.
     * @param newLoc the location to be removed
     * @return true if removed successfully, false if list doesn't contain newLoc.
     */
    public boolean removeUnfinishedLoc(Location newLoc) {
        return unfinishedLocList.remove(newLoc);
    }

    @Override
    public boolean addMeeple(Meeple newMeeple) {
        if (meeples.isEmpty()) {
            return meeples.add(newMeeple);
        }
        return false;
    }

    @Override
    public List<Meeple> getMeeples() {
        return meeples;
    }

    @Override
    public List<Player> getScoreOwner() {
        return List.of(meeples.get(0).getOwner());
    }

    @Override
    public List<Meeple> returnMeeples() {
        List<Meeple> tmpMeeples = new ArrayList<>(meeples);
        meeples.clear();
        return tmpMeeples;
    }

    @Override
    public boolean removeMeeple(Meeple newMeeple) {
        return meeples.remove(newMeeple);
    }

    @Override
    public int getScore() {
        return NINE;
    }

    @Override
    public int getEndScore() {
        return NINE - unfinishedLocList.size();
    }

    @Override
    public String toString() {
        return "Monastery center at" + center.getLoc() + "vacancies: " + unfinishedLocList.toString();
    }
}
