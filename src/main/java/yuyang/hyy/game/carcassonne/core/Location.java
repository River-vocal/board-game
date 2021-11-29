package yuyang.hyy.game.carcassonne.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The location class to represent the location of a tile in the game map.
 */
public class Location {
    private final int x;
    private final int y;

    /**
     * Constructor ot create a location pair.
     * @param one x index
     * @param two y index
     */
    public Location(int one, int two) {
        x = one;
        y = two;
    }

    /**
     * Get x index.
     * @return x index
     */
    public int getX() {
        return x;
    }

    /**
     * Get y index.
     * @return y index
     */
    public int getY() {
        return y;
    }

    @Override
    public String toString() {
        return String.format("(%s, %s)", x, y);
    }

    /**
     * Get the direction of another location.
     * For example, if this loc is (0, 0), another one is (1, 0), then we get Orientation.RIGHT.
     * @param anotherLoc another location pair
     * @return the relative orientation between two locations.
     */
    public Orientation getDirectNeighboringOri(Location anotherLoc) {
        int deltaX = anotherLoc.x - this.x;
        int deltaY = anotherLoc.y - this.y;
        if (deltaX > 1 || deltaX < -1 || deltaY > 1 || deltaY < -1 || deltaX * deltaY != 0) {
            throw new IllegalArgumentException("They aren't direct neighbors!");
        }
        if (deltaX == 0 && deltaY == 0) {
            throw new IllegalArgumentException("They are the same!");
        }
        if (deltaX == 0) {
            return deltaY == 1 ? Orientation.TOP : Orientation.DOWN;
        } else {
            return deltaX == 1 ? Orientation.RIGHT : Orientation.LEFT;
        }
    }

    /**
     * Get neighbors according to the relative orientation.
     * @param o relative direction
     * @return the corresponding neighbor
     */
    public Location getNeighbor(Orientation o) {
        switch (o) {
            case TOP:
                return new Location(x, y + 1);
            case DOWN:
                return new Location(x, y - 1);
            case LEFT:
                return new Location(x - 1, y);
            case RIGHT:
                return new Location(x + 1, y);
            case CENTER:
                return this;
            default:
                throw new IllegalArgumentException("No such Diagonal Orientation!");
        }
    }

    /**
     * Get diagonal neighbors according to the relative orientation.
     * @param d relative direction
     * @return the corresponding neighbor
     */
    public Location getDiagNeighbor(DiagonalOrientation d) {
        switch (d) {
            case TOP_LEFT:
                return new Location(x - 1, y + 1);
            case TOP_RIGHT:
                return new Location(x + 1, y + 1);
            case DOWN_LEFT:
                return new Location(x - 1, y - 1);
            case DOWN_RIGHT:
                return new Location(x + 1, y - 1);
            default:
                throw new IllegalArgumentException("No such Diagonal Orientation!");
        }
    }

    /**
     * Get all neighbor locations in four directions.
     * @return list of 4 neighbor locations
     */
    public List<Location> getDirectNeighbors() {
        List<Location> directionList = new ArrayList<>();
        for (Orientation ori : Orientation.values()) {
            directionList.add(getNeighbor(ori));
        }
        directionList.remove(4);
        return directionList;
    }

    /**
     * Get all neighbor locations in 8 directions.
     * @return list of 8 neighbor locations
     */
    public List<Location> getAllNeighbors() {
        List<Location> directionList = new ArrayList<>(getDirectNeighbors());
        for (DiagonalOrientation ori : DiagonalOrientation.values()) {
            directionList.add(getDiagNeighbor(ori));
        }
        return directionList;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Location)) {
            return false;
        } else {
            Location loc = (Location) obj;
            return x == loc.x && y == loc.y;
        }
    }
}
