package yuyang.hyy.game.carcassonne.core;

/**
 * Meeple class.
 */
public class Meeple {
    private final Player owner;

    /**
     * Meeple constructor.
     * @param player meeple owner
     */
    Meeple(Player player) {
        owner = player;
    }

    /**
     * Owner getter.
     * @return owner
     */
    public Player getOwner() {
        return owner;
    }

    @Override
    public String toString() {
        return owner.toString() + "'s meeple";
    }
}
