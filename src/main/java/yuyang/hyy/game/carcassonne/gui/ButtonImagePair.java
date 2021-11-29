package yuyang.hyy.game.carcassonne.gui;

import javax.swing.JButton;
import java.awt.image.BufferedImage;

/**
 * An extra pair class here to store the JButton and the corresponding tile image.
 * Used to obtain the original tile image after the meeple is cleared.
 */
public class ButtonImagePair {
    private final JButton button;
    private final BufferedImage image;

    /**
     * Constructor.
     * @param jButton JButton of the tile.
     * @param bufferedImage Image of the tile.
     */
    public ButtonImagePair(JButton jButton, BufferedImage bufferedImage) {
        button = jButton;
        image = bufferedImage;
    }

    /**
     * Image getter.
     * @return tile image.
     */
    public BufferedImage getImage() {
        return image;
    }

    /**
     * JButton getter.
     * @return JButton
     */
    public JButton getButton() {
        return button;
    }
}
