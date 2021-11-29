package yuyang.hyy.game.carcassonne.gui;

import yuyang.hyy.game.carcassonne.core.Game;
import yuyang.hyy.game.carcassonne.core.GameBoardChangeListener;
import yuyang.hyy.game.carcassonne.core.Location;
import yuyang.hyy.game.carcassonne.core.Meeple;
import yuyang.hyy.game.carcassonne.core.Orientation;
import yuyang.hyy.game.carcassonne.core.Player;
import yuyang.hyy.game.carcassonne.core.Tile;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The main game panel, containing the game map, tile and meeple placement.
 */
public class GameBoardPanel extends JPanel implements GameBoardChangeListener {

    private final Game game;
    private final List<BufferedImage> images;
    private BufferedImage lastTileImage;
    private final JButton[][] squares;
    private final Map<Meeple, ButtonImagePair> meeples;
    private JButton rotateClockwiseButton, rotateAntiClockwiseButton, placeMeeple, latestTile, endTurn;
    private JLabel nextTile;
    private final JLabel errorBar;
    private final PlayerStatusPanel playerStatusPanel;
    private final Color[] colors;

    private static final int NINTY = 90, BOARD_LENGTH = 143, SEVENTY_ONE = 71, FOUR = 4, SIX = 6;
    // Allow player status panel to use the colors
    static final Color[] ALL_COLORS = {Color.CYAN, Color.red, Color.yellow, Color.green, Color.orange};

    /**
     * Constructor of the game board panel.
     * @param newGame Carcassone game system
     */
    public GameBoardPanel(Game newGame) {
        game = newGame;
        game.addGameBoardChangeListener(this);
        images = cropImages();
        meeples = new HashMap<>();
        colors = new Color[game.getPlayerList().size()];
        // According to the game rule, we have utmost 5 players.
        System.arraycopy(ALL_COLORS, 0, colors, 0, colors.length);

        // Set up the game map.
        squares = new JButton[BOARD_LENGTH][BOARD_LENGTH];
        JPanel gameMap = createBoardPanel();
        JScrollPane scroller = new JScrollPane(gameMap);
        game.gameStart();

        errorBar = new JLabel("Welcome to Carcassonne!");

        playerStatusPanel = new PlayerStatusPanel(game);

        JPanel tilePlacement = createTilePlacementPanel();
        JPanel meeplePlacement = createMeeplePlacementPanel();
        JPanel sideBoard = new JPanel();
        sideBoard.setLayout(new BoxLayout(sideBoard, BoxLayout.PAGE_AXIS));

        GroupLayout groupLayout = new GroupLayout(sideBoard);
        groupLayout.setVerticalGroup(
                groupLayout.createSequentialGroup()
                        .addComponent(playerStatusPanel)
                        .addComponent(tilePlacement)
                        .addComponent(meeplePlacement)
                        .addComponent(errorBar)
        );

        groupLayout.setHorizontalGroup(
                groupLayout.createSequentialGroup()
                        .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment
                                .CENTER)
                                .addComponent(playerStatusPanel)
                                .addComponent(tilePlacement)
                                .addComponent(meeplePlacement)
                                .addComponent(errorBar))
        );
        sideBoard.setLayout(groupLayout);

        setLayout(new BorderLayout());
        add(scroller, BorderLayout.CENTER);
        add(sideBoard, BorderLayout.LINE_END);
        Rectangle bounds = scroller.getViewport().getViewRect();
        Dimension size = scroller.getViewport().getViewSize();
        int x = (size.width - bounds.width) / 2;
        int y = (size.height - bounds.height) / 2;
        scroller.getViewport().setViewPosition(new Point(x - 300, y - 300));
        scroller.getVerticalScrollBar().setUnitIncrement(16);
        scroller.getHorizontalScrollBar().setUnitIncrement(16);
    }

    private JPanel createBoardPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(BOARD_LENGTH, BOARD_LENGTH));

        JButton firstTile = new JButton();
        firstTile.setPreferredSize(new Dimension(NINTY, NINTY));
        firstTile.setIcon(new ImageIcon(getTileImage(game.getCurrTile(), game.getRotationTimes())));

        // Create all of the squares and display them.
        for (int row = 0; row < BOARD_LENGTH; row++) {
            for (int col = 0; col < BOARD_LENGTH; col++) {
                if (row == SEVENTY_ONE && col == SEVENTY_ONE) {
                    panel.add(firstTile);
                } else {
                    squares[row][col] = new JButton();
                    squares[row][col].setPreferredSize(new Dimension(0, 0));
                    squares[row][col].setVisible(false);

                    int r = row;
                    int c = col;
                    squares[row][col].addActionListener(e -> {
                        try {
                            if (game.isTilePlaced()) {
                                return;
                            }
                            game.placeTile(game.getCurrTile(), frontToBack(new Location(r, c)));
                        } catch (AssertionError ae) {
                            showError("Tile has been placed this turn!");
                        }

                    });
                    panel.add(squares[row][col]);
                }

            }
        }
        updateBoard();
        return panel;
    }

    private JPanel createTilePlacementPanel() {
        JPanel tmpPanel = new JPanel();
        nextTile = new JLabel(new ImageIcon(getTileImage(game.getCurrTile(), game.getRotationTimes())));
        JLabel nextTileInstruction = new JLabel("Next Tile:");
        rotateClockwiseButton = new JButton("Rotate Clockwise");
        rotateAntiClockwiseButton = new JButton("Rotate Anticlockwise");
        rotateClockwiseButton.setPreferredSize(new Dimension(NINTY, NINTY));
        rotateAntiClockwiseButton.setPreferredSize(new Dimension(NINTY, NINTY));

        rotateClockwiseButton.addActionListener(e -> {
            game.rotateCurrTileClockwise();
        });

        rotateAntiClockwiseButton.addActionListener(e -> {
            game.rotateCurrTileAntiClockwise();
        });

        // https://docs.oracle.com/javase/tutorial/uiswing/layout/group.html
        // https://docs.oracle.com/javase/tutorial/uiswing/layout/groupExample.html
        GroupLayout groupLayout = new GroupLayout(tmpPanel);
        groupLayout.setVerticalGroup(
                groupLayout.createSequentialGroup()
                        .addComponent(nextTileInstruction)
                        .addComponent(nextTile)
                        .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(rotateAntiClockwiseButton)
                                .addComponent(rotateClockwiseButton))
        );

        groupLayout.setHorizontalGroup(
                groupLayout.createSequentialGroup()
                        .addComponent(rotateAntiClockwiseButton)
                        .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment
                                .CENTER)
                                .addComponent(nextTileInstruction)
                                .addComponent(nextTile))
                        .addComponent(rotateClockwiseButton)
        );
        tmpPanel.setLayout(groupLayout);
        return tmpPanel;
    }

    private JPanel createMeeplePlacementPanel() {
        placeMeeple = new JButton("Place a meeple");
        ButtonGroup orientaions = new ButtonGroup();
        JRadioButton top = new JRadioButton("Top");
        orientaions.add(top);
        JRadioButton right = new JRadioButton("Right");
        orientaions.add(right);
        JRadioButton down = new JRadioButton("Down");
        orientaions.add(down);
        JRadioButton left = new JRadioButton("Left");
        orientaions.add(left);
        JRadioButton center = new JRadioButton("Center");
        orientaions.add(center);

        endTurn = new JButton("End Turn");
        endTurn.addActionListener(e -> {
            try {
                game.endTurnUpdate();
                game.nextTurn();
            } catch (AssertionError ae) {
                showError("You must place a tile before finishing the turn!");
            }

        });

        placeMeeple.addActionListener(e -> {
            try {
                if (top.isSelected()) {
                    game.placeMeeple(Orientation.TOP);
                } else if (right.isSelected()) {
                    game.placeMeeple(Orientation.RIGHT);
                } else if (down.isSelected()) {
                    game.placeMeeple(Orientation.DOWN);
                } else if (left.isSelected()) {
                    game.placeMeeple(Orientation.LEFT);
                } else if (center.isSelected()) {
                    game.placeMeeple(Orientation.CENTER);
                } else {
                    showError("You must select an orientation to place the meeple!");
                }
            } catch (AssertionError ae) {
                showError("You must place a tile before the meeple placement!");
            } catch (IndexOutOfBoundsException ie) {
                showError("You don't have a meeple!");
            }

        });


        JLabel meepleInstruction = new JLabel("Please choose meeple location:");

        // https://docs.oracle.com/javase/tutorial/uiswing/layout/group.html
        // https://docs.oracle.com/javase/tutorial/uiswing/layout/groupExample.html
        JPanel placeMeeplePanel = new JPanel();
        GroupLayout groupLayout = new GroupLayout(placeMeeplePanel);
        groupLayout.setVerticalGroup(
                groupLayout.createSequentialGroup()
                        .addComponent(meepleInstruction)
                        .addComponent(top)
                        .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment
                                .BASELINE)
                                .addComponent(left)
                                .addComponent(center)
                                .addComponent(right))
                        .addComponent(down)
                        .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment
                                .BASELINE)
                                .addComponent(placeMeeple)
                                .addComponent(endTurn))
        );
        groupLayout.setHorizontalGroup(
                groupLayout.createSequentialGroup()
                        .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment
                                .LEADING)
                                .addComponent(left)
                                .addComponent(placeMeeple))
                        .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment
                                .CENTER)
                                .addComponent(meepleInstruction)
                                .addComponent(top)
                                .addComponent(center)
                                .addComponent(down))
                        .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment
                                .TRAILING)
                                .addComponent(right)
                                .addComponent(endTurn))
        );
        placeMeeplePanel.setLayout(groupLayout);
        placeMeeple.setEnabled(false);
        endTurn.setEnabled(false);
        return placeMeeplePanel;
    }

    private void updateBoard() {
        List<Location> availableLoc = game.getAllNeighboringLoc();
        for (Location eachLoc : availableLoc) {
            Location boardLoc = backToFront(eachLoc);
            //squares[boardLoc.getX()][boardLoc.getY()].setPreferredSize(new Dimension(NINTY, NINTY));
            squares[boardLoc.getX()][boardLoc.getY()].setVisible(true);
            squares[boardLoc.getX()][boardLoc.getY()].setEnabled(true);
        }
    }

    /**
     * Private helper to handle the difference between the index of Location class and that of JButton 2d arrays.
     * @param frontLoc the location index in the "frontend"
     * @return the location index in the "backend"
     */
    private Location frontToBack(Location frontLoc) {
        return new Location(frontLoc.getY() - SEVENTY_ONE, SEVENTY_ONE - frontLoc.getX());
    }

    private Location backToFront(Location backLoc) {
        return new Location(SEVENTY_ONE - backLoc.getY(), SEVENTY_ONE + backLoc.getX());
    }

    /**
     * Method from piazza post. Crop an image to a list of buffered images.
     * @return A list of buffered images
     */
    public static List<BufferedImage> cropImages() {
        List<BufferedImage> bufferedImages = new ArrayList<>();
        BufferedImage image = null;
        try {
            image = ImageIO.read(new File("src/main/resources/Carcassonne.png"));
        } catch (IOException e) {
            throw new IllegalArgumentException("Something is wrong when reading file!" + e);
        }
        for (int row = 0; row < FOUR; row++) {
            for (int col = 0; col < SIX; col++) {
                BufferedImage tileImage = image.getSubimage(col * NINTY, row * NINTY, NINTY, NINTY);
                bufferedImages.add(tileImage);
            }
        }
        return bufferedImages;
    }

    @Override
    public void handleTilePlacement(Tile t, Location loc) {
        clearError();
        nextTile.setIcon(null);
        Location locOnBoard = backToFront(loc);
        lastTileImage = getTileImage(t, game.getRotationTimes());
        squares[locOnBoard.getX()][locOnBoard.getY()].setIcon(new ImageIcon(lastTileImage));
        latestTile = squares[locOnBoard.getX()][locOnBoard.getY()];
        rotateAntiClockwiseButton.setEnabled(false);
        rotateClockwiseButton.setEnabled(false);
        placeMeeple.setEnabled(true);
        endTurn.setEnabled(true);
    }

    @Override
    public void handleMeeplePlacement(Meeple m, Orientation ori) {
        clearError();
        int xAxis = 0, yAxis = 0;
        switch (ori) {
            case CENTER:
                xAxis = NINTY / 2;
                yAxis = NINTY / 2;
                break;
            case TOP:
                xAxis = NINTY / 2;
                yAxis = SIX;
                break;
            case RIGHT:
                xAxis = NINTY - SIX;
                yAxis = NINTY / 2;
                break;
            case DOWN:
                xAxis = NINTY / 2;
                yAxis = NINTY - SIX;
                break;
            case LEFT:
                xAxis = SIX;
                yAxis = NINTY / 2;
                break;
            default:
                showError("You should choose an orientation!");
        }
        ButtonImagePair buttonImagePair = new ButtonImagePair(latestTile, lastTileImage);
        meeples.put(m, buttonImagePair);
        latestTile.setIcon(new ImageIcon(withCircle(lastTileImage, getColor(), xAxis, yAxis, SIX)));
        playerStatusPanel.scoreMeepleUpdated(game.getCurrPlayer());
        placeMeeple.setEnabled(false);
    }

    @Override
    public void updateNextTurn() {
        updateNextTileImage();
        updateBoard();
        rotateClockwiseButton.setEnabled(true);
        rotateAntiClockwiseButton.setEnabled(true);
        playerStatusPanel.updateCurrPlayer();
        placeMeeple.setEnabled(false);
        endTurn.setEnabled(false);
    }

    @Override
    public void clearMeeple(Meeple meepleReturned) {
        playerStatusPanel.scoreMeepleUpdated(meepleReturned.getOwner());
        ButtonImagePair newPair = meeples.get(meepleReturned);
        JButton oriButton = newPair.getButton();
        BufferedImage oriImage = newPair.getImage();
        oriButton.setIcon(new ImageIcon(oriImage));
    }

    @Override
    public void updateNextTileImage() {
        nextTile.setIcon(new ImageIcon(getTileImage(game.getCurrTile(), game.getRotationTimes())));
    }

    @Override
    public void showError(String str) {
        errorBar.setText(str);
    }

    @Override
    public void gameOver(List<Player> winners) {
        for (Player p : game.getPlayerList()) {
            playerStatusPanel.scoreMeepleUpdated(p);
        }
        rotateAntiClockwiseButton.setEnabled(false);
        rotateClockwiseButton.setEnabled(false);
        endTurn.setEnabled(false);
        placeMeeple.setEnabled(false);
        errorBar.setText("Winner(s) is(are) " + winners.toString());
        JFrame frame = (JFrame) SwingUtilities.getRoot(this);
        showDialog(frame, "Winner!", winners + " just won the game!");

    }

    /**
     * Method from piazza. Rotate the buffered image.
     * @param src buffered image to be rotated
     * @param n rotation times (clockwise)
     * @return the buffered image after rotation
     */
    public static BufferedImage rotateClockwise(BufferedImage src, int n) {
        int weight = src.getWidth();
        int height = src.getHeight();

        AffineTransform at = AffineTransform.getQuadrantRotateInstance(n, weight / 2.0, height / 2.0);
        AffineTransformOp op = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);

        BufferedImage dest = new BufferedImage(weight, height, src.getType());
        op.filter(src, dest);
        return dest;
    }

    /**
     * Method from piazza. Draw a circle to represent a meeple.
     * @param src source image
     * @param color color of the circle
     * @param x x index of circle position
     * @param y y index of circle position
     * @param radius radius of the circle
     * @return the image after drawing a circle
     */
    public static BufferedImage withCircle(BufferedImage src, Color color, int x, int y, int radius) {
        BufferedImage dest = new BufferedImage(src.getWidth(), src.getHeight(), src.getType());

        Graphics2D g = (Graphics2D) dest.getGraphics();
        g.drawImage(src, 0, 0, null);
        g.setColor(color);
        g.fillOval(x - radius, y - radius, 2 * radius, 2 * radius);
        g.dispose();

        return dest;
    }

    private BufferedImage getTileImage(Tile tile, int rotationTimes) {
        BufferedImage tmpImage = images.get(tile.getIndex());
        if (rotationTimes % FOUR != 0) {
            tmpImage = rotateClockwise(tmpImage, rotationTimes);
        }
        return tmpImage;
    }

    private Color getColor() {
        return colors[game.getCurrPlayerIndex()];
    }

    private void clearError() {
        errorBar.setText(null);
    }

    private static void showDialog(Component component, String title, String message) {
        JOptionPane.showMessageDialog(component, message, title, JOptionPane.INFORMATION_MESSAGE);
    }
}
