package yuyang.hyy.game.carcassonne;

import yuyang.hyy.game.carcassonne.gui.GameStart;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

/**
 * Main class to start the game.
 */
public class Main {

    private static final String START_NAME = "Carcassonne Registration";

    /**
     * Main method to start.
     * @param args No arg here.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::playerRegistration);
    }

    private static void playerRegistration() {
        //add frame and set its closing operation
        JFrame frame = new JFrame(START_NAME);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        //add chat client, participants will be added by SimpleChatClient
        frame.add(new GameStart(frame));

        //display the JFrame
        frame.pack();
        frame.setResizable(false);
        frame.setVisible(true);
    }
}
