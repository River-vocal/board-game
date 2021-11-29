package yuyang.hyy.game.carcassonne.core;

import java.util.ArrayList;
import java.util.List;

/**
 * Player class, which represents a virtual player in the game, not the observer.
 */
public class Player {
    private int score;
    private final List<Meeple> meeples = new ArrayList<>();
    private final String playerName;

    /**
     * Player constructor.
     * @param playerName name string of the player
     */
    public Player(String playerName) {
        this.playerName = playerName;
        while (meeples.size() < 7) {
            Meeple tmpMeeple = new Meeple(this);
            meeples.add(tmpMeeple);
        }
    }

    /**
     * Score getter.
     * @return score
     */
    public int getScore() {
        return score;
    }

    /**
     * Add score to the existing score.
     * @param newScore score from a feature
     */
    public void addScore(int newScore) {
        score = score + newScore;
    }

    /**
     * Meeple list is empty or not.
     * @return meeple list is empty or not
     */
    public boolean hasMeeple() {
        return !meeples.isEmpty();
    }

    /**
     * Meeple getter.
     * @return get all meeples
     */
    public List<Meeple> getMeeples() {
        return meeples;
    }

    /**
     * Play a meeple if the player still has >= 1.
     * @return true if player has one.
     */
    public Meeple playMeeple() {
        try {
            Meeple tmpMeeple = meeples.get(meeples.size() - 1);
            meeples.remove(meeples.size() - 1);
            return tmpMeeple;
        } catch (IndexOutOfBoundsException e) {
            throw new IndexOutOfBoundsException("This player has no meeple to play!" + e);
        }
    }

    /**
     * Peek the last meeple of the list.
     * @return the last meeple
     */
    public Meeple getOneMeeple() {
        try {
            return meeples.get(meeples.size() - 1);
        } catch (IndexOutOfBoundsException e) {
            throw new IndexOutOfBoundsException("This player has no meeple to get!" + e);
        }
    }

    /**
     * Give the meeple back to the player.
     * @param backMeeple meeple back
     */
    public void returnMeeple(Meeple backMeeple) {
        if (meeples.size() > 6) {
            throw new IllegalArgumentException("This player will have > 7 meeples!");
        }
        if (!backMeeple.getOwner().equals(this)) {
            throw new IllegalArgumentException("Wrong meeple return to the wrong owner!");
        }
        meeples.add(backMeeple);

    }

    @Override
    public String toString() {
        return playerName;
    }
}
