package yuyang.hyy.game.carcassonne.gui;

import yuyang.hyy.game.carcassonne.core.Game;
import yuyang.hyy.game.carcassonne.core.Player;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * A JPanel to let users add player names and start the game.
 */
public class GameStart extends JPanel {

    private static final String GAME_NAME = "Carcassonne";

    /** The JFrame from which this chat is established. */
    private JFrame parentFrame;

    /** The participants in this game. */
    private final List<Player> playerList;

    /**
     * Constructor of the starting panel.
     * @param frame parent frame
     */
    public GameStart(JFrame frame) {
        this.parentFrame = frame;
        this.playerList = new ArrayList<>();

        // Create the components to add to the panel.
        JLabel participantLabel = new JLabel("Name: ");

        // Must be final to be accessible to the anonymous class.
        final JTextField participantText = new JTextField(20);

        JButton participantButton = new JButton("Add participant");
        JPanel participantPanel = new JPanel();
        participantPanel.setLayout(new BorderLayout());
        participantPanel.add(participantLabel, BorderLayout.WEST);
        participantPanel.add(participantText, BorderLayout.CENTER);
        participantPanel.add(participantButton, BorderLayout.EAST);

        // Defines an action listener to handle the addition of new participants
        ActionListener newParticipantListener = e -> {
            String name = participantText.getText().trim();
            if (!name.isEmpty() && !playerList.contains(name)) {
                Player newPlayer = new Player(name);
                playerList.add(newPlayer);
            }
            participantText.setText("");
            participantText.requestFocus();
        };

        // notify the action listener when participant Button is pressed
        participantButton.addActionListener(newParticipantListener);

        // notify the action listener when "Enter" key is hit
        participantText.addActionListener(newParticipantListener);

        JButton createButton = new JButton("Start Game");
        createButton.addActionListener(e -> {
            // Starts a new chat when the createButton is clicked.
            if (playerList.size() >= 2 && playerList.size() <= 5) {
                startNewGame();
            }
        });

        // Adds the components we've created to the panel (and to the window).
        setLayout(new BorderLayout());
        add(participantPanel, BorderLayout.NORTH);
        add(createButton, BorderLayout.SOUTH);
        setVisible(true);
    }

    /**
     * Starts a new Carcassone game.
     */
    private void startNewGame() {
        parentFrame.dispose();
        parentFrame = null;

        Game gameSystem = new Game(playerList);

        // Creates a new window.

        JFrame frame = new JFrame(GAME_NAME);
        frame.setSize(1280, 720);
        GameBoardPanel gameBoardPanel = new GameBoardPanel(gameSystem);

        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        frame.setContentPane(gameBoardPanel);
        frame.setResizable(true);
        frame.setVisible(true);

    }
}
