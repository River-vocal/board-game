package yuyang.hyy.game.carcassonne.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class includes Road and City feature, which are quite differently from Monastery.
 * So, their logic of creation, combination, and completion should be treated separately.
 */
public abstract class ContinuousFeature implements BasicFeature {

    private List<Tile> tileList;
    private List<Meeple> meepleList = new ArrayList<>();
    private Segment type;
    private List<LocOriPair> unfinishedPoints, allPoints;

    List<LocOriPair> getUnfinishedPoints() {
        return unfinishedPoints;
    }

    void setUnfinishedPoints(List<LocOriPair> unfinishedPoints) {
        this.unfinishedPoints = unfinishedPoints;
    }

    List<LocOriPair> getAllPoints() {
        return allPoints;
    }

    void setAllPoints(List<LocOriPair> allPoints) {
        this.allPoints = allPoints;
    }

    List<Tile> getTileList() {
        return tileList;
    }

    void setTileList(List<Tile> tileList) {
        this.tileList = tileList;
    }

    void setMeepleList(List<Meeple> meepleList) {
        this.meepleList = meepleList;
    }

    @Override
    public Segment getType() {
        return type;
    }

    void setType(Segment type) {
        this.type = type;
    }

    @Override
    public boolean isComplete() {
        return unfinishedPoints.isEmpty();
    }

    /**
     *
     * @return whether the meeple list is empty
     */
    @Override
    public boolean hasMeeple() {
        return !meepleList.isEmpty();
    }

    @Override
    public boolean addTile(Tile newTile){
        return tileList.add(newTile);

    }

    /**
     * Check whether this feature contains an open end at the specific location and orientation.
     * @param point (location, orientation)
     * @return contains or not
     */
    public boolean containsPoint(LocOriPair point) {
        return allPoints.contains(point);
    }

    @Override
    public List<Meeple> getMeeples() {
        return meepleList;
    }

    @Override
    public boolean addMeeple(Meeple newMeeple) {
        if (meepleList.isEmpty()) {
            return meepleList.add(newMeeple);
        }
        return false;
    }

    @Override
    public List<Meeple> returnMeeples() {
        List<Meeple> tmpMeepleList = new ArrayList<>(meepleList);
        meepleList.clear();
        return tmpMeepleList;
    }

    @Override
    public boolean removeMeeple(Meeple newMeeple) {
        return meepleList.remove(newMeeple);
    }

    private boolean completePoint(LocOriPair pair) {
        return unfinishedPoints.remove(pair);
    }

    /**
     * Combine this feature with another one. Compare each open ending points and merge.
     * @param anotherFea another feature
     * @return true if successfully merged, vice versa.
     */
    public boolean combineFeature(ContinuousFeature anotherFea) {
        if (!this.type.isSameTypeSeg(anotherFea.type) || isComplete()) {
            return false;
        }
        boolean canCombine = false;
        List<LocOriPair> combinedPoints = new ArrayList<>();

        List<LocOriPair> anoUnfinishedPoints = anotherFea.unfinishedPoints;
        for (LocOriPair point : unfinishedPoints) {
            if (anoUnfinishedPoints.contains(point.getNeighbor())) {
                canCombine = true;
                combinedPoints.add(point);
            }
        }
        if (!canCombine) {
            return false;
        }
        for (LocOriPair point : combinedPoints) {
            this.completePoint(point);
            anotherFea.completePoint(point.getNeighbor());
        }
        unfinishedPoints.addAll(anotherFea.unfinishedPoints);
        meepleList.addAll(anotherFea.meepleList);
        tileList.addAll(anotherFea.tileList);
        allPoints.addAll(anotherFea.allPoints);
        return canCombine;
    }

    @Override
    public List<Player> getScoreOwner() {
        int maxCount = 0;
        List<Player> owners = new ArrayList<>();
        Map<Player, Integer> countMap = new HashMap<>();
        for (Meeple m : meepleList) {
            countMap.merge(m.getOwner(), 1, Integer::sum);
        }
        for (Map.Entry<Player, Integer> each : countMap.entrySet()) {
            if (each.getValue() > maxCount) {
                owners = new ArrayList<>();
                owners.add(each.getKey());
                maxCount = each.getValue();
            } else if (each.getValue() == maxCount && each.getValue() > 0) {
                owners.add(each.getKey());
            }
        }
        return owners;
    }

    @Override
    public String toString() {
        return type.toString() + "at: " + unfinishedPoints.toString();
    }
}
