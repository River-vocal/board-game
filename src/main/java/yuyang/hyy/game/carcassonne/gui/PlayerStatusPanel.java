package yuyang.hyy.game.carcassonne.gui;

import yuyang.hyy.game.carcassonne.core.Game;
import yuyang.hyy.game.carcassonne.core.Player;
import yuyang.hyy.game.carcassonne.core.PlayerStatusChangeListener;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.util.HashMap;
import java.util.Map;

/**
 * The panel to display the players' information. Including player name, current score, meeple number and color.
 */
public class PlayerStatusPanel extends JPanel implements PlayerStatusChangeListener {

    private final JLabel currentPlayer;
    private final Map<Player, JLabel> playerStatusMap;
    private final Game game;

    /**
     * Constructor containing the initial player information.
     * @param newGame Carcassone game system.
     */
    public PlayerStatusPanel(Game newGame) {
        game = newGame;
        game.addPlayerStatusChangeListener(this);
        currentPlayer = new JLabel("This is " + game.getCurrPlayer().toString() + "'s turn | Turn No. 1");
        playerStatusMap = new HashMap<>();

        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        this.add(currentPlayer);
        JLabel gameStatus = new JLabel("Game status:");
        this.add(gameStatus);
        for (Player player : game.getPlayerList()) {
            JLabel info = new JLabel(String.format("Player %s  Score: %d, Remaining Meeples: %d",
                    player, player.getScore(), player.getMeeples().size()));
            info.setForeground(GameBoardPanel.ALL_COLORS[game.getPlayerList().indexOf(player)]);
            playerStatusMap.put(player, info);
            this.add(info);
        }
    }

    /**
     * Update the score and meeple no. of one player.
     * @param player player to be updated.
     */
    @Override
    public void scoreMeepleUpdated(Player player) {
        playerStatusMap.get(player).setText(String.format("Player %s  Score: %d, Remaining Meeples: %d",
                player, player.getScore(), player.getMeeples().size()));
    }

    @Override
    public void updateCurrPlayer() {
        currentPlayer.setText("This is " + game.getCurrPlayer().toString() + "'s turn | Turn No. " + game.getNoOfTurns());
    }


}
