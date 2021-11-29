package yuyang.hyy.game.carcassonne.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The game board, containing the game map, and lists of features.
 * The monastery and road/city are treated seperately because they apply totally different logic.
 */
public class Board {
    private Tile currPlacedTile;
    private Map<Location, Tile> gameMap;
    private List<ContinuousFeature> completedFea = new ArrayList<>(), incompleteFea = new ArrayList<>(),
            currTileFea = new ArrayList<>();
    private List<Monastery> completedMons = new ArrayList<>(), incompleteMons = new ArrayList<>();
    // Since the Orientation enum also contains CENTER, another array is necessary here.
    private static final Orientation[] DIRECTIONS = new Orientation[] {
            Orientation.TOP, Orientation.RIGHT, Orientation.DOWN, Orientation.LEFT};

    /**
     * Constructor for a new board.
     */
    Board() {
        gameMap = new HashMap<>();
        currPlacedTile = new Tile(4, Segment.ROAD, Segment.CITY_END, Segment.ROAD, Segment.FIELD, Segment.FIELD, false);

    }

    /**
     * Get all the neighboring locations of the existing tiles in the game map, which can narrow down the
     * range of possible locations for the next tile placement.
     * @return a set of all neighboring locations.
     */
    Set<Location> getAllNeighboringLoc() {
        Set<Location> possibleLoc = new HashSet<>();
        for (Map.Entry<Location, Tile> eachEntry : gameMap.entrySet()) {
            possibleLoc.addAll(eachEntry.getKey().getDirectNeighbors());
        }
        for (Map.Entry<Location, Tile> eachEntry : gameMap.entrySet()) {
            possibleLoc.remove(eachEntry.getKey());
        }
        return possibleLoc;
    }

    /**
     * Get the segment in the map with a given Location Orientation pair.
     * Should be private, but make it package-private for test use.
     * @param point the location of the segment.
     * @return segment
     */
    Segment getSeg(LocOriPair point) {
        assert gameMap.containsKey(point.getLoc()): "No tile on this location!";
        Tile thisTile = gameMap.get(point.getLoc());
        return thisTile.getEdgeSeg(point.getOri());
    }

    /**
     * Check whether a tiles placement violates the game rule.
     * @param thisTile the tile to be placed
     * @param loc the location to be placed
     * @return legal placement or not
     */
    boolean placementIsLegal(Tile thisTile, Location loc) {
        if (gameMap.containsKey(loc)) {
            return false;
        }
        for (Orientation ori : DIRECTIONS) {
            LocOriPair thisPoint = new LocOriPair(loc, ori);
            LocOriPair neighborPoint = thisPoint.getNeighbor();
            if (gameMap.containsKey(neighborPoint.getLoc())) {
                if (!thisTile.getEdgeSeg(ori).isSameTypeSeg(getSeg(neighborPoint))) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Check whether there is at least one available location for this tile to place.
     * @param thisTile the tile to be placed.
     * @return whether this tile should be discarded or not
     */
    boolean tileIsLegal(Tile thisTile) {
        Tile tmpTile = new Tile(thisTile.getEdgeSeg(Orientation.TOP), thisTile.getEdgeSeg(Orientation.RIGHT),
                thisTile.getEdgeSeg(Orientation.DOWN), thisTile.getEdgeSeg(Orientation.LEFT),
                thisTile.getCenterSeg(), thisTile.isShield());
        for (int i = 0; i < 4; i++) {
            tmpTile.rotateClockwise();
            for (Location loc : getAllNeighboringLoc()) {
                if (placementIsLegal(tmpTile, loc)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Place the 1st tile (tile D) to start the game.
     * @param firstT always tile D
     */
    void placeFirstTile(Tile firstT) {
        assert gameMap.isEmpty();
        firstT.setLoc(new Location(0, 0));
        gameMap.put(new Location(0, 0), firstT);
        currPlacedTile = firstT;
        generateFeatureFromNewTile();
        updateFeatures();
    }

    /**
     * Method called by Game class to try to place a tile. If this location is valid for this tile (after rotation),
     * place it. Otherwise return false.
     * @param t the tile to be placed
     * @param loc the location to be placed
     * @return true if successfully placed. false otherwise.
     */
    boolean placeTile(Tile t, Location loc) {
        assert !gameMap.isEmpty();
        if (!placementIsLegal(t, loc) || !getAllNeighboringLoc().contains(loc)) {
            return false;
        }
        t.setLoc(loc);
        currPlacedTile = t;
        gameMap.put(loc, t);
        generateFeatureFromNewTile();
        updateFeatures();
        return true;
    }

    /**
     * Generate the features from the newly placed tile.
     */
    void generateFeatureFromNewTile() {
        if (currPlacedTile.getCenterSeg().isSameTypeSeg(Segment.MONASTERY)) {
            Monastery mon = new Monastery(currPlacedTile);
            incompleteMons.add(mon);
        }
        boolean cityHasBeenCreated = false;
        boolean roadHasBeenCreated = false;
        for (Orientation ori : DIRECTIONS) {
            Segment thisSeg = currPlacedTile.getEdgeSeg(ori);
            LocOriPair thisPoint = new LocOriPair(currPlacedTile.getLoc(), ori);
            switch (thisSeg) {
                case CITY_END:
                    City city = new City(currPlacedTile, List.of(thisPoint));
                    currTileFea.add(city);
                    break;
                case CITY:
                    if (!cityHasBeenCreated) {
                        Set<LocOriPair> tmpCitySeg = new HashSet<>();
                        for (Orientation o : DIRECTIONS) {
                            LocOriPair tmpPoint = new LocOriPair(currPlacedTile.getLoc(), o);
                            if (getSeg(tmpPoint).equals(Segment.CITY)) {
                                tmpCitySeg.add(tmpPoint);
                            }
                        }
                        City city1 = new City(currPlacedTile, new ArrayList<>(tmpCitySeg));
                        cityHasBeenCreated = true;
                        currTileFea.add(city1);
                    }
                    break;
                case ROAD_END:
                    Road road = new Road(currPlacedTile, List.of(thisPoint));
                    currTileFea.add(road);
                    break;
                case ROAD:
                    if (!roadHasBeenCreated) {
                        Set<LocOriPair> tmpRoadSeg = new HashSet<>();
                        for (Orientation o : DIRECTIONS) {
                            LocOriPair tmpPoint = new LocOriPair(currPlacedTile.getLoc(), o);
                            if (getSeg(tmpPoint).equals(Segment.ROAD)) {
                                tmpRoadSeg.add(tmpPoint);
                            }
                        }
                        Road road1 = new Road(currPlacedTile, new ArrayList<>(tmpRoadSeg));
                        roadHasBeenCreated = true;
                        currTileFea.add(road1);
                    }
                    break;
                default:
                    break;
            }
        }

    }

    /**
     * Update the features.
     * Check whether the newly generated features can combine the existing incomplete ones.
     */
    void updateFeatures() {
        // Check Monastery first
        for (Monastery mon : incompleteMons) {
            if (mon.getCenterLoc().equals(currPlacedTile.getLoc())) {
                List<Location> tmpNeighborList = currPlacedTile.getLoc().getAllNeighbors();
                for (Location tmpLoc : tmpNeighborList) {
                    if (gameMap.containsKey(tmpLoc)) {
                        mon.removeUnfinishedLoc(tmpLoc);
                    }
                }
            } else {
                if (mon.getUnfinishedLocList().contains(currPlacedTile.getLoc())) {
                    mon.removeUnfinishedLoc(currPlacedTile.getLoc());
                }
            }
            if (mon.isComplete()) {
                completedMons.add(mon);
            }
        }
        incompleteMons.removeAll(completedMons);

        // Then road and city
        for (ContinuousFeature newFea : currTileFea) {
            List<ContinuousFeature> featuresHasBeenCombined = new ArrayList<>();
            for (ContinuousFeature oldFea : incompleteFea) {
                if (newFea.combineFeature(oldFea)) {
                    featuresHasBeenCombined.add(oldFea);
                }
            }
            incompleteFea.removeAll(featuresHasBeenCombined);
            if (newFea.isComplete()) {
                completedFea.add(newFea);
            }
        }
        currTileFea.removeAll(completedFea);
        incompleteFea.removeAll(completedFea);
        incompleteFea.addAll(currTileFea);
        currTileFea.clear();
    }

    /**
     * Method called by Game class to place the meeple.
     * @param meeple the meeple to be placed
     * @param o the orientation on the current tile to place the meeple
     * @return true if successfully placed the meeple. False otherwise.
     */
    boolean placeMeeple(Meeple meeple, Orientation o) {
        Location currLoc = currPlacedTile.getLoc();
        if (o.equals(Orientation.CENTER)) {
            if (currPlacedTile.getCenterSeg().equals(Segment.MONASTERY)) {
                for (Monastery mon : incompleteMons) {
                    if (currLoc.equals(mon.getCenterLoc())) {
                        return mon.addMeeple(meeple);
                    }
                }
                for (Monastery mon : completedMons) {
                    if (currLoc.equals(mon.getCenterLoc())) {
                        return mon.addMeeple(meeple);
                    }
                }
            }
            return false;
        }
        LocOriPair addPoint = new LocOriPair(currLoc, o);
        for (ContinuousFeature fea : incompleteFea) {
            if (fea.containsPoint(addPoint)) {
                return fea.addMeeple(meeple);
            }
        }
        for (ContinuousFeature fea : completedFea) {
            if (fea.containsPoint(addPoint)) {
                return fea.addMeeple(meeple);
            }
        }
        return false;
    }

    /**
     * A getter for test use.
     * @return the tile placed in the current turn
     */
    Tile getCurrPlacedTile() {
        return currPlacedTile;
    }

    /**
     * Get all the completed feature on board.
     * @return list of completed features
     */
    List<ContinuousFeature> getCompletedFea() {
        return completedFea;
    }

    /**
     * Get all the completed monastery on board.
     * @return list of completed monasteries
     */
    List<Monastery> getCompletedMons() {
        return completedMons;
    }

    /**
     * Get all the incomplete feature on board.
     * @return list of incomplete features
     */
    List<ContinuousFeature> getIncompleteFea() {
        return incompleteFea;
    }

    /**
     * Get all the incomplete monastery on board.
     * @return list of incomplete monasteries
     */
    List<Monastery> getIncompleteMons() {
        return incompleteMons;
    }

    /**
     * Game map getter for testcases.
     * @return game map
     */
    Map<Location, Tile> getGameMap() {
        return gameMap;
    }

    /**
     * Clear all the completed features.
     * Used at end turn
     */
    void clearCompletedFeatures() {
        completedMons.clear();
        completedFea.clear();
    }

    /**
     * Clear all the incomplete features.
     * Used at end game
     */
    void clearIncompleteFeatures() {
        incompleteFea.clear();
        incompleteMons.clear();
    }
}
