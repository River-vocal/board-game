package yuyang.hyy.game.carcassonne.core;

import java.util.ArrayList;
import java.util.List;

/**
 * The game system class
 */
public class Game {

    private final Board board;
    private final List<Player> playerList;
    private List<Player> winner;
    private final Deck deck;
    private boolean isRunning;
    private Player currPlayer;
    private Tile currTile;
    private int turnCount;
    private boolean tilePlaced;
    /** The listeners who will be notified of changes in the game board */
    private final List<GameBoardChangeListener> gameBoardChangeListeners = new ArrayList<>();
    /** The listeners who will be notified of changes in the game state */
    private final List<PlayerStatusChangeListener> playerStatusChangeListeners = new ArrayList<>();

    /**
     * The constructor used for real game.
     * @param playerList a list of participants.
     */
    public Game(List<Player> playerList) {
        this.playerList = playerList;
        board = new Board();
        currTile = new Tile(4, Segment.ROAD, Segment.CITY_END, Segment.ROAD, Segment.FIELD, Segment.FIELD, false);
        board.placeFirstTile(currTile);
        deck = new Deck("src/main/resources/tiles.json");
        deck.shuffle();
    }

    /**
     * This constructor is only used for testcases (so that a different (smaller) stack can be used, and it won't be shuffled)
     * @param p1 player 1
     * @param p2 player 2
     * @param p3 player 3
     */
    public Game(Player p1, Player p2, Player p3) {
        playerList = new ArrayList<>();
        playerList.add(p1);
        playerList.add(p2);
        playerList.add(p3);
        board = new Board();
        currTile = new Tile(4, Segment.ROAD, Segment.CITY_END, Segment.ROAD, Segment.FIELD, Segment.FIELD, false);
        board.placeFirstTile(currTile);
        deck = new Deck("src/main/resources/testTiles.json");
    }
    /**
     * This constructor is only used for testcases (so that a different (smaller) stack can be used, and it won't be shuffled)
     * @param p1 player 1
     * @param p2 player 2
     */
    public Game(Player p1, Player p2) {
        playerList = new ArrayList<>();
        playerList.add(p1);
        playerList.add(p2);
        board = new Board();
        currTile = new Tile(4, Segment.ROAD, Segment.CITY_END, Segment.ROAD, Segment.FIELD, Segment.FIELD, false);
        board.placeFirstTile(currTile);
        deck = new Deck("src/main/resources/testCityTiles.json");
    }

    /**
     * Register a game change listener to be notified of game change events.
     *
     * @param listener The listener to be notified of game change events.
     */
    public void addGameBoardChangeListener(GameBoardChangeListener listener) {
        gameBoardChangeListeners.add(listener);
    }

    /**
     * Register a game change listener to be notified of game change events.
     *
     * @param listener The listener to be notified of game change events.
     */
    public void addPlayerStatusChangeListener(PlayerStatusChangeListener listener) {
        playerStatusChangeListeners.add(listener);
    }

    /**
     * Clockwise rotation times getter. Used for rotation of tile images.
     * @return no. of clockwise rotation times.
     */
    public int getRotationTimes() {
        return currTile.getRotationNo();
    }

    /**
     * Start the game.
     */
    public void gameStart() {
        isRunning = true;
        turnCount = 0;
        currPlayer = getNextPlayer();
        currTile = deck.nextTile();
        tilePlaced = false;
    }

    /**
     * Switch to next turn.
     * If tiles are over, end game.
     */
    public void nextTurn() {
        assert tilePlaced;
        if (deck.isEmpty()) {
            endGame();
            return;
        }
        endTurnUpdate();
        turnCount++;
        currPlayer = getNextPlayer();
        currTile = deck.nextTile();
        while (!board.tileIsLegal(currTile) && !deck.isEmpty()) {
            currTile = deck.nextTile();
        }
        tilePlaced = false;
        notifyNextTurn();
    }

    /**
     * End game. Check the score of all incomplete features. Figure out the winner.
     */
    public void endGame() {
        assert deck.isEmpty();
        endGameUpdate();
        int maxScore = playerList.get(0).getScore();
        winner = new ArrayList<>();
        for (Player player : playerList) {
            if (player.getScore() > maxScore) {
                winner.clear();
                winner.add(player);
            } else if (player.getScore() == maxScore) {
                winner.add(player);
            }
        }
        notifyGameOver(winner);
    }

    /**
     * Place the tile if legal.
     * @param tile the tile to be placed
     * @param loc the location to be placed
     * @return whether this placement is valid or not. If not, the placement will be cancelled.
     */
    public boolean placeTile(Tile tile, Location loc) {
        assert isRunning && !tilePlaced;
        if (board.placeTile(tile, loc)) {
            tilePlaced = true;
            notifyTilePlacement(tile, loc);
            return true;
        } else {
            showError("You cannot place a tile here!");
            return false;
        }
    }

    /**
     * Place the meeple if legal.
     * @param o the orientation on the current tile to place the meeple
     * @return whether this placement is valid or not. If not, the placement will be cancelled.
     */
    public boolean placeMeeple(Orientation o) {
        assert isRunning && currPlayer.hasMeeple() && tilePlaced;
        Meeple tryMeeple = currPlayer.getOneMeeple();
        if (board.placeMeeple(tryMeeple, o)) {
            notifyMeeplePlacement(currPlayer.playMeeple(), o);
            return true;
        } else {
            showError("You cannot place a meeple here!");
            return false;
        }
    }

    /**
     * At the end of each turn, score all the completed features and delete them.
     */
    public void endTurnUpdate() {
        // Because monastery feature can only have one meeple, we can just check the 0th of the meeple list and the
        // scorer list.
        assert tilePlaced;
        for (Monastery mon : board.getCompletedMons()) {
            if (mon.hasMeeple()) {
                int monScore = mon.getScore();
                Player scorer = mon.getScoreOwner().get(0);
                scorer.addScore(monScore);
                Meeple returningMeeples = mon.returnMeeples().get(0);
                scorer.returnMeeple(returningMeeples);
                updatePlayerStatus(scorer);
                notifyReturnMeeple(returningMeeples);
            }
        }
        for (ContinuousFeature fea : board.getCompletedFea()) {
            if (fea.hasMeeple()) {
                int feaScore = fea.getScore();
                completeFeaScoreHelper(fea, feaScore);
            }
        }
        board.clearCompletedFeatures();
    }

    private void completeFeaScoreHelper(ContinuousFeature fea, int feaScore) {
        List<Player> scorers = new ArrayList<>(fea.getScoreOwner());
        List<Meeple> returningMeeples = new ArrayList<>(fea.returnMeeples());
        for (Player scoreOwner : scorers) {
            scoreOwner.addScore(feaScore);
            updatePlayerStatus(scoreOwner);
        }
        for (Meeple reMeeple : returningMeeples) {
            Player owner = reMeeple.getOwner();
            owner.returnMeeple(reMeeple);
            notifyReturnMeeple(reMeeple);
        }
    }

    /**
     * At the end of each game, score all the incomplete features.
     */
    public void endGameUpdate() {
        endTurnUpdate();
        for (Monastery mon : board.getIncompleteMons()) {
            if (mon.hasMeeple()) {
                int monScore = mon.getEndScore();
                Player monScorer = mon.getScoreOwner().get(0);
                monScorer.addScore(monScore);
                Meeple returningMeeple = mon.returnMeeples().get(0);
                monScorer.returnMeeple(returningMeeple);
                updatePlayerStatus(monScorer);
                notifyReturnMeeple(returningMeeple);
            }
        }
        for (ContinuousFeature fe : board.getIncompleteFea()) {
            if (fe.hasMeeple()) {
                int feaScore = fe.getEndScore();
                completeFeaScoreHelper(fe, feaScore);
            }
        }
        board.clearIncompleteFeatures();
        isRunning = false;
    }

    private Player getNextPlayer() {
        return playerList.get(turnCount % playerList.size());
    }

    /**
     * Rotate the current tile clockwise.
     */
    public void rotateCurrTileClockwise() {
        currTile.rotateClockwise();
        updateCurrTile();
    }

    /**
     * Rotate the current tile anticlockwise.
     */
    public void rotateCurrTileAntiClockwise() {
        currTile.rotateAntiClockwise();
        updateCurrTile();
    }

    /**
     * Player list getter
     * @return play list
     */
    public List<Player> getPlayerList() {
        return playerList;
    }

    /**
     * Get the current player.
     * @return current player
     */
    public Player getCurrPlayer() {
        return currPlayer;
    }

    /**
     * Get the winner(s) of this game.
     * @return the winner(s)
     */
    public List<Player> getWinner() {
        return winner;
    }

    /**
     * Get the current tile to play.
     * @return legal tile for this turn
     */
    public Tile getCurrTile() {
        return currTile;
    }

    /**
     * Check whether the game is running.
     * @return is running or not
     */
    public boolean isRunning() {
        return isRunning;
    }

    /**
     * package-private method for test.
     * @return the game board
     */
    public Board getBoard() {
        return board;
    }

    /**
     * Get all neighbors of the tiles on the board. Used to highlight the possible tile placement.
     * @return a list of location
     */
    public List<Location> getAllNeighboringLoc() {
        return new ArrayList<>(board.getAllNeighboringLoc());
    }

    /**
     * Get the index of the current player. Used for the meeple color.
     * @return the index of the current player.
     */
    public int getCurrPlayerIndex() {
        return turnCount % playerList.size();
    }

    /**
     * Whether the tile placement has been finished in this turn. Used to avoid tile duplication in one turn.
     * @return tile has been placed or not
     */
    public boolean isTilePlaced() {
        return tilePlaced;
    }

    private void showError(String str) {
        for (GameBoardChangeListener boardListener : gameBoardChangeListeners) {
            boardListener.showError(str);
        }
    }

    private void updateCurrTile() {
        for (GameBoardChangeListener boardListener : gameBoardChangeListeners) {
            boardListener.updateNextTileImage();
        }
    }

    private void notifyNextTurn() {
        for (GameBoardChangeListener boardListener : gameBoardChangeListeners) {
            boardListener.updateNextTurn();
        }
    }

    private void notifyTilePlacement(Tile t, Location loc) {
        for (GameBoardChangeListener boardListener : gameBoardChangeListeners) {
            boardListener.handleTilePlacement(t, loc);
        }
    }

    private void notifyMeeplePlacement(Meeple meeple, Orientation ori) {
        for (GameBoardChangeListener boardListener : gameBoardChangeListeners) {
            boardListener.handleMeeplePlacement(meeple, ori);
        }
    }

    private void notifyReturnMeeple(Meeple m) {
        for (GameBoardChangeListener boardListener : gameBoardChangeListeners) {
            boardListener.clearMeeple(m);
        }
    }

    private void notifyGameOver(List<Player> winners) {
        for (GameBoardChangeListener boardListener : gameBoardChangeListeners) {
            boardListener.gameOver(winners);
        }
    }

    private void updatePlayerStatus(Player p) {
        for (PlayerStatusChangeListener playerListener : playerStatusChangeListeners) {
            playerListener.scoreMeepleUpdated(p);
        }
    }

    /**
     * Return the 1-based turn count. Used to show turn count in GUI.
     * @return the 1-based turn count
     */
    public int getNoOfTurns() {
        return turnCount + 1;
    }
}
