package yuyang.hyy.game.carcassonne.core;

/**
 * Enums to represent segments.
 */
public enum Segment {
    CITY, CITY_END, ROAD, ROAD_END, MONASTERY, FIELD;

    /**
     * There are two kinds of road and city segment, but they still share the same type in the game rule.
     * @param seg another segment
     * @return true if they are the same according to the rule
     */
    public boolean isSameTypeSeg(Segment seg) {
        if (this.equals(CITY) || this.equals(CITY_END)) {
            return seg.equals(CITY) || seg.equals(CITY_END);
        }
        if (this.equals(ROAD) || this.equals(ROAD_END)) {
            return seg.equals(ROAD) || seg.equals(ROAD_END);
        }
        return equals(seg);
    }
}
