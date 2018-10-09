package screen;

import backend.Board;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

import javax.imageio.ImageIO;
import javax.swing.*;

import static screen.Utils.*;

public class BoardScreen extends JPanel {
    private BufferedImage background;
    private GameScreen gameScreen;
    private ArrayList<JLabel> labels = new ArrayList<>();
    private JLabel gridLabel = new JLabel();
    private int boardX = 0;
    private int boardY = 0;
    private Sprites sprites = new Sprites();
    private Point playerPoint = new Point();

    @Override
    public void paintComponent(Graphics g) {
    }

    private int boardSize;
    private int gridDimensions;
    private BoardSettings boardSettings;

    private Board board;
    public BoardScreen(GameScreen gameScreen, Board board, BoardSettings boardSettings) {
        this.gameScreen = gameScreen;
        this.board = board;
        this.boardSettings = boardSettings;
        setLayout(null);

        initialiseBoard();

//        setSize(background.getHeight(),background.getLength());
    }

    /**
     * Iterates over every grid coordinate.
     * If block has not been initialised, gets the block information from board
     * and takes an appropriate sprite to display it
     */
    public void initialiseBoard() {
        gridDimensions = gameScreen.getGridDimensions();
        boardSize = board.getDimensions();
        boardX = gridDimensions*2;
        setPreferredSize(new Dimension((boardSize+4) * gridDimensions,boardSize * gridDimensions));
        try {
            File backgroundF = new File("sprites" + File.separator + "Board" + ".png");
            background = ImageIO.read(backgroundF);
            background = toBufferedImage(
                    background.getScaledInstance(boardSize * gridDimensions, boardSize * gridDimensions, Image.SCALE_SMOOTH));
        } catch (IOException e) {
            e.printStackTrace();
        }
        gridLabel.setIcon(new ImageIcon(background));
        gridLabel.setBounds(boardX, boardY, background.getWidth(), background.getHeight());


        for (JLabel label : labels) {
            remove(label);
        }
        labels.clear();
        //Iterate over grid coordinates and polls board for block information
        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                boolean b = board.isSeen(row,col);
                if (b) continue;
                int length = board.getBlockLength(row,col);
                boolean horizontal = board.getBlockHorizontal(row,col);
                if (board.isPlayer(row,col)) {
                    playerPoint = coordToPoint(col,row);
                }

                //Gets image depending if player or not
                BufferedImage img = (board.isPlayer(row,col)) ? sprites.playerImage(length) : sprites.randomImage(length, horizontal);
                img = gameScreen.resize(img);
                addCar(col,row,length,horizontal,img);
            }
        }

        remove(gridLabel);
        add(gridLabel);
    }

    /**
     * Finds a single misplaced block compared to the backend
     * Creates thread to move misplaced block into position
     * @return Thread to perform the car movement
     */
    public Thread undo() {
        //Iterate over grid coordinates and polls board for block information
        JLabel misplaced = null;
        int r = 0;
        int c = 0;
        int length = 0;
        boolean horizontal = false;
        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                boolean isSeen = board.isSeen(row,col);
                boolean valid = board.validBlock(row,col);
                Point p = coordToPoint(col,row);

                Optional<JLabel> opt = labels.stream().filter(l->l.getBounds().contains(p)).findFirst();
                Optional<JLabel> opt2 = labels.stream().filter(l->l.getLocation().equals(p)).findFirst();

                //Found new coordinate
                if (!isSeen && valid && !opt2.isPresent()) {
                    r = row;
                    c = col;
                    continue;
                }
                //Found misplaced block
                if (!valid && opt.isPresent()) {
                    misplaced = opt.get();
                    continue;
                }
            }
        }

        if (misplaced == null) return null;
        final JLabel car = misplaced;
        final int col = c;
        final int row = r;
        Thread moveCar = new Thread(()->{
            Point dest = coordToPoint(col,row);
            Point start = car.getLocation();
            double increments = 100;
            for (double i = 0; i <= increments; i++) {
                Point p = new Point();
                p.x = (int)(start.x+(dest.x-start.x)*(i/increments));
                p.y = (int)(start.y+(dest.y-start.y)*(i/increments));
//            misplaced.setBounds(p.x, p.y, misplaced.getWidth(), misplaced.getHeight());
                car.setLocation(p);
                gameScreen.refreshFrame();
                repaint();
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
//        moveCar.start();
        return moveCar;
    }

    /**
     * Adds an image to the on screen grid at coordinates (x,y)
     * @param x
     * @param y
     * @param img
     */
    private void addCar(int x, int y, int length, boolean horizontal, BufferedImage img) {
        JLabel label = new JLabel(new ImageIcon(img));
        MouseHandler mh  = new MouseHandler(length,horizontal);
        label.addMouseListener(mh);
        label.addMouseMotionListener(mh);
        add(label);
        Point p = coordToPoint(x,y);
        label.setBounds(p.x, p.y, img.getWidth(), img.getHeight());
        labels.add(label);
    }


    public Point coordToPoint(Point c) {
        return coordToPoint(c.x,c.y);
    }


    /**
     * Converts the coordinates on a grid to a Point on the screen
     * @param x
     * @param y
     * @return Point with x and y as screen coordinates
     */
    public Point coordToPoint(int x, int y) {
        return new Point(x*gridDimensions+gridLabel.getLocation().x,y*gridDimensions+gridLabel.getLocation().y);
    }

    /**
     * Converts the coordinates of a Point on the screen to grid coordinates
     * @param p
     * @return Point with x and y as grid coordinates
     */
    private Point pointToCoord(Point p) {
        return new Point((p.x-gridLabel.getLocation().x)/gridDimensions,(p.y-gridLabel.getLocation().y)/gridDimensions);
    }

    /**
     * Gets playerPoint
     *
     * @return value of playerPoint
     */
    public Point getPlayerPoint() {
        return playerPoint;
    }


    /**
     * Handles the clicking and movement of blocks
     */
    class MouseHandler extends MouseAdapter {

        private Point initialPress;
        private Point labelCoords;
        private Point labelPosition;
        private JLabel currentLabel;
        private int upperBound;
        private int lowerBound;
        private int length;

        private boolean horizontal;
        public MouseHandler(int length, boolean horizontal) {
            this.length = length;
            this.horizontal = horizontal;
        }

        /**
         * Get details of block when clicked
         * @param e
         */
        @Override
        public void mousePressed(MouseEvent e) {
            initialPress = e.getPoint();
            currentLabel = (JLabel) e.getComponent();
            labelPosition = currentLabel.getLocation();
            labelCoords = pointToCoord(labelPosition);
            this.upperBound = board.getUpperBound(labelCoords.x, labelCoords.y);
            this.lowerBound = board.getLowerBound(labelCoords.x, labelCoords.y);
        }

        /**
         * Move block to new position as mouse drags
         * @param e
         */
        @Override
        public void mouseDragged(MouseEvent e) {
            if (horizontal) {
                int x = e.getPoint().x - initialPress.x;
                labelPosition.x = Math.min(Math.max(labelPosition.x+x,lowerBound*gridDimensions+gridLabel.getLocation().x), (upperBound+1-length)*gridDimensions+gridLabel.getLocation().x);
            } else {
                int y = e.getPoint().y - initialPress.y;
                labelPosition.y = Math.min(Math.max(labelPosition.y+y,lowerBound*gridDimensions+gridLabel.getLocation().y), (upperBound+1-length)*gridDimensions+gridLabel.getLocation().y);
            }
            currentLabel.setLocation(labelPosition);
        }

        /**
         * Snap to grid, check win condition
         * @param e
         */
        @Override
        public void mouseReleased(MouseEvent e) {
            JLabel component = (JLabel) e.getComponent();
            Point location = component.getLocation();
            location.x = (int)Math.round(location.x/(double)gridDimensions)*gridDimensions;
            location.y = (int)Math.round(location.y/(double)gridDimensions)*gridDimensions;
            component.setLocation(location);

            Point p2 = pointToCoord(location);

            if (board.moveBlock(labelCoords.y, labelCoords.x,p2.y,p2.x)) {
                if (!board.validWin() && boardSettings.isAiMode()) {
                    gameScreen.enableTops(false);
                    board.AIMoveBlock();
                    gameScreen.undo();
                }
                gameScreen.updateCounter();
            }
            if (board.validWin()) {
                final int currX = location.x;
                new Thread(()->{
                    for (int i = currX; i < currX+gridDimensions*boardSize; i+=2) {
                        location.x = i;
                        component.setLocation(location);
                        gameScreen.refreshFrame();
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException ee) {
                            ee.printStackTrace();
                        }
                    }
                }).start();
                gameScreen.win();
            }
        }
    }
}
