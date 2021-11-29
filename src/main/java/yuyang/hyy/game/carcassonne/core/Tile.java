package yuyang.hyy.game.carcassonne.core;

/**
 * The tile class.
 */
public class Tile {
    private Segment centerSeg;
    private Segment[] edgeSegmentsOri, edgeSegmentsAftRot;
    private int antiClockwiseRotationTimes;
    private static final int FOUR = 4;
    private Location loc;
    private final boolean isShield;
    // Image index
    private int index;

    /**
     * Constructor without index.
     * @param top segment on the top
     * @param right segment on the right
     * @param down segment on the bottom
     * @param left  segment on the left
     * @param center  segment in the center
     * @param isShield1 whether the tile contains a coat-of-arm
     */
    Tile(Segment top, Segment right, Segment down, Segment left, Segment center, boolean isShield1) {
        this.isShield = isShield1;
        edgeSegmentsOri = new Segment[] {top, right, down, left};
        edgeSegmentsAftRot = edgeSegmentsOri.clone();
        centerSeg = center;
    }

    /**
     * Constructor with index to get the tile image.
     * @param index index of the tile.
     * @param top segment on the top
     * @param right segment on the right
     * @param down segment on the bottom
     * @param left  segment on the left
     * @param center  segment in the center
     * @param isShield1 whether the tile contains a coat-of-arm
     */
    Tile(int index, Segment top, Segment right, Segment down, Segment left, Segment center, boolean isShield1) {
        this.isShield = isShield1;
        edgeSegmentsOri = new Segment[] {top, right, down, left};
        edgeSegmentsAftRot = edgeSegmentsOri.clone();
        centerSeg = center;
        this.index = index;
    }

    /**
     * Get the segment in the edge according to the orientation.
     * @param o orientation
     * @return segment
     */
    public Segment getEdgeSeg(Orientation o) {
        assert !o.equals(Orientation.CENTER) : "Center is not the edge segment!";
        return edgeSegmentsAftRot[o.ordinal()];
    }

    /**
     * Center segment getter.
     * @return center segment
     */
    public Segment getCenterSeg() {
        return centerSeg;
    }

    /**
     * Index getter. "-1" to transfer to 0-based indexing.
     * @return the index of this tile.
     */
    public int getIndex() {
        return index - 1;
    }

    /**
     * Rotate this tile clockwise.
     */
    public void rotateClockwise() {
        antiClockwiseRotationTimes = (antiClockwiseRotationTimes - 1 + FOUR) % FOUR;
        updateRotation();
    }

    /**
     * Rotate this tile anticlockwise.
     */
    public void rotateAntiClockwise() {
        antiClockwiseRotationTimes = (antiClockwiseRotationTimes + 1) % FOUR;
        updateRotation();
    }

    private void updateRotation() {
        edgeSegmentsAftRot = new Segment[FOUR];
        for (int i = 0; i < 4; i++) {
            edgeSegmentsAftRot[i] = edgeSegmentsOri[(i + antiClockwiseRotationTimes) % FOUR];
        }
    }

    /**
     * Get the no. of rotation times (clockwise).
     * @return rotation times.
     */
    public int getRotationNo() {
        return (FOUR - antiClockwiseRotationTimes + FOUR) % FOUR;
    }

    /**
     * Set the location of this tile.
     * @param newLoc location in the game map.
     */
    public void setLoc(Location newLoc) {
        loc = newLoc;
    }

    /**
     * Location getter.
     * @return location in the game map.
     */
    public Location getLoc() {
        return loc;
    }

    /**
     * Boolean getter.
     * @return whether this tile contains a coat-of-arm.
     */
    public boolean isShield() {
        return isShield;
    }

    /**
     * Check whether two tiles have the same segment, only used for testcases.
     * @param t another tile
     * @return is same or not
     */
    boolean isSameTypeOfTile(Tile t) {
        return centerSeg.equals(t.centerSeg) && getEdgeSeg(Orientation.TOP).equals(t.getEdgeSeg(Orientation.TOP)) &&
                getEdgeSeg(Orientation.RIGHT).equals(t.getEdgeSeg(Orientation.RIGHT)) &&
                getEdgeSeg(Orientation.DOWN).equals(t.getEdgeSeg(Orientation.DOWN)) &&
                getEdgeSeg(Orientation.LEFT).equals(t.getEdgeSeg(Orientation.LEFT)) && isShield == t.isShield;

    }

    @Override
    public String toString() {
        String coat;
        if (isShield) {
            coat = "has coat-of-arm";
        } else {
            coat = "no coat-of-arm";
        }

        return String.format("Top: %s, Right: %s, Down: %s, Left: %s, Center: %s, %s",
                edgeSegmentsAftRot[0], edgeSegmentsAftRot[1],
                edgeSegmentsAftRot[2], edgeSegmentsAftRot[3], centerSeg, coat);
    }

}
