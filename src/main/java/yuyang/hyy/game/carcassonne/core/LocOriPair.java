package yuyang.hyy.game.carcassonne.core;

import java.util.Objects;

/**
 * A pair of location and orientation to represent the "location" of each segment in the game map.
 */
public class LocOriPair {
    private Location loc;
    private Orientation ori;

    /**
     * Constructor.
     * @param newLoc location
     * @param newOri orientation on this location
     */
    LocOriPair(Location newLoc, Orientation newOri) {
        loc = newLoc;
        ori = newOri;
    }

    /**
     * Location getter
     * @return location of this pair
     */
    public Location getLoc() {
        return loc;
    }

    /**
     * Orientation getter
     * @return orientation of this pair
     */
    public Orientation getOri() {
        return ori;
    }

    @Override
    public int hashCode() {
        return Objects.hash(loc, ori);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof LocOriPair)) {
            return false;
        } else {
            LocOriPair newPair = (LocOriPair) obj;
            return loc.equals(newPair.loc) && ori.equals(newPair.ori);
        }
    }

    @Override
    public String toString() {
        return String.format("(%s, %s, %s)", loc.getX(), loc.getY(), ori);
    }

    /**
     * Get the direct neighbor of this pair (from the neighboring location).
     * @return the neighbor
     */
    public LocOriPair getNeighbor() {
        if (ori.equals(Orientation.CENTER)) {
            throw new IllegalArgumentException("Center segment doesn't has a neighbor!");
        }
        Location newLoc = loc.getNeighbor(ori);
        Orientation newOri = ori.getOpposite();
        return new LocOriPair(newLoc, newOri);
    }

    /**
     * Check whether two pair are neighbors on the game map.
     * @param newPoint another segment location/orientation
     * @return whether these two are neighbors or not
     */
    public boolean isNeighbor(LocOriPair newPoint) {
        return getNeighbor().equals(newPoint);
    }

    /**
     * Another static method to check two loc/ori pair are neighbors on the game map.
     * @param point1 one segment location
     * @param point2 another segment location
     * @return whether these two are neighbors or not
     */
    public static boolean areNeighbors(LocOriPair point1, LocOriPair point2) {
        return point1.getNeighbor().equals(point2);
    }
}
