package screen;

import backend.Board;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;

import static screen.Utils.toBufferedImage;


public class GameScreen extends JLayeredPane {
    protected Board board;
    protected BoardScreen boardScreen;
    protected GridLockFrame gridLockFrame;
    protected BufferedImage background;
    private BufferedImage road;
    protected JPanel tops;
    protected JLabel time  = new JLabel("Time: 00:00");
    protected JLabel counter;
    protected JLabel hints;
    protected Timer t;

    private WinMenuScore winMenu;

    private int iconHeight = 7;
    protected int iconSizeWidth = 60;
    protected int iconSizeHeight = 27;

    private int backgroundWidth = 1000;
    private int backgroundHeight = 600;

    private int boardWidth = 600;
    private int boardHeight = 600;
    private int menuBarHeight = 40;

    private int timePass = 0;
    private int hintsUsed = 0;
    private int maxHints = 5;

    protected int gridDimensions = 100;
    protected BoardSettings boardSettings;

    protected Font F = new Font("Bradley Hand", Font.BOLD, 20);
    private GridBagConstraints c = new GridBagConstraints();

    public GameScreen() {
    }

    public GameScreen(Board board, GridLockFrame gridLockFrame, BoardSettings boardSettings) {
        this.board = board;
        this.boardScreen = new BoardScreen(this, board, boardSettings);
        this.gridLockFrame = gridLockFrame;
        this.boardSettings = boardSettings;

        tops = initialiseMenuBar();
        setLayout(new GridBagLayout());
        c.gridx = 1;
        c.gridy = 0;
        add(tops,c);
        addBoard();
//        winMenu = new WinMenuScore(3,500,300);

        try {
            road = Sprites.createImage("road.png");
            road = resize(road);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Create and add the menu bar
     */
    protected JPanel initialiseMenuBar() {
        try {
            File boardFile = new File("sprites"+File.separator+"GameBackground.png");
            background = ImageIO.read(boardFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        JPanel menu = new JPanel(new FlowLayout(FlowLayout.CENTER,10,0));
        menu.setOpaque(false);
        counter = new JLabel("Counter: 0  ");
        counter.setPreferredSize(new Dimension(200,40));
        hints = new JLabel("Hints: "+ hintsUsed + "/" + maxHints);
        hints.setPreferredSize(new Dimension(150,40));
        time.setPreferredSize(new Dimension(200,40));
        counter.setFont(F);
        time.setFont(F);
        hints.setFont(F);
        counter.setForeground(Color.white);
        time.setForeground(Color.white);
        hints.setForeground(Color.white);

        JButton undo = new JButton("");
        JButton quit = new JButton("");
        JButton replay = new JButton("");
        JButton random = new JButton("");
        JButton hint = new JButton("");

        t = new Timer(1000,this::timer);
        t.start();
        try {
            quit.setIcon(Sprites.createScaledImageIcon("GameIconQuit.png",iconSizeWidth,iconSizeHeight));
            undo.setIcon(Sprites.createScaledImageIcon("GameIconUndo.png",iconSizeWidth,iconSizeHeight));
            replay.setIcon(Sprites.createScaledImageIcon("GameIconReplay.png",iconSizeWidth,iconSizeHeight));
            random.setIcon(Sprites.createScaledImageIcon("GameIconRandom.png",iconSizeWidth,iconSizeHeight));
            hint.setIcon(Sprites.createScaledImageIcon("gameScreenHint.png",iconSizeWidth,iconSizeHeight));
        } catch (IOException e) {
            e.printStackTrace();
        }

        quit.setBorderPainted(false);
        quit.setContentAreaFilled(false);
        undo.setBorderPainted(false);
        undo.setContentAreaFilled(false);
        replay.setBorderPainted(false);
        replay.setContentAreaFilled(false);
        random.setBorderPainted(false);
        random.setContentAreaFilled(false);
        hint.setBorderPainted(false);
        hint.setContentAreaFilled(false);

        quit.addActionListener(this::quit);
        undo.addActionListener(this::undo);
        replay.addActionListener(this::replay);
        random.addActionListener(this::random);
        hint.addActionListener(this::hint);

        menu.add(counter);
        menu.add(hints);
        menu.add(time);
        menu.add(undo);
        menu.add(random);
        menu.add(hint);
        menu.add(replay);
        menu.add(quit);

    return menu;
    }

    /**
     * Position boardScreen in panel
     */
    private void addBoard() {
        c.gridx = 1;
        c.gridy = 1;
        c.ipady = 10;
        add(boardScreen,c);
        c.ipady = 0;
    }

    /**
     * Action for undo button
     * @param e
     */
    private void undo(ActionEvent e) {
        if (!board.doUndo()) return;
        undo();
        updateCounter();
    }

    /**
     * Called on win, display win menu
     */
    public void win() {
        setLayout(null);
        winMenu = new WinMenuScore(calculateStarScore(),gridLockFrame.getFrame().getWidth(),gridLockFrame.getFrame().getHeight());
        winMenu.setQuit(this::quit);
        winMenu.setReplay(this::replay);
        winMenu.setNewGame(this::newGame);
        add(winMenu,new Integer(4));
        winMenu.setBounds(0,0,Math.max(boardScreen.getWidth(),tops.getWidth()),boardScreen.getHeight()+tops.getWidth());
//        winMenu.setBounds(0,0,1000,600);
    }

    private int calculateStarScore() {
        int movesMade = board.moveCounter();
        int minMoves = board.getMinMoves();

        if (movesMade == minMoves) return 3;
        if (movesMade < minMoves*1.3) return 2;
        else return 1;
    }

    /**
     * Randomise the cars by reinitialising board
     * @param e
     */
    public void random(ActionEvent e) {
        boardScreen.initialiseBoard();
        refreshFrame();
    }

    /**
     * Hint button action
     * @param e
     */
    public void hint(ActionEvent e) {
        if (hintsUsed >= maxHints || !board.doHint()) return;
        undo();
        hintsUsed++;
        updateHintsCounter();
        updateCounter();
        if (board.validWin()) win();
    }

    /**
     * Update move counter JLabel
     */
    public void updateCounter() {
        if (boardSettings.isAiMode()) {
            counter.setText("Counter: " + board.moveCounter());
        } else {
            counter.setText("Counter: " + board.moveCounter() + "/" + board.getMinMoves());
        }
        if (board.moveCounter() > board.getMinMoves()) {
            counter.setForeground(Color.RED);
        } else {
            counter.setForeground(Color.WHITE);
        }
    }

    /**
     * Update hint counter JLabel
     */
    public void updateHintsCounter() {
        if (hintsUsed == maxHints) {
            hints.setForeground(Color.RED);
        } else {
            hints.setForeground(Color.WHITE);
        }
        hints.setText("Hints: " + hintsUsed + "/" + maxHints);
    }

    /**
     * Timer event to update timer JLabel
     * @param e
     */
    protected void timer(ActionEvent e) {
        int second = 0;
        int minute = 0;
        String minuteS = "";
        String secondS = "";
        if (timePass > 0) {
            second = timePass%60;
            minute = timePass/60;

        }
        secondS =Integer.toString(second);
        minuteS =Integer.toString(minute);
        if (second < 10) {
            secondS = "0" + Integer.toString(second);
        }
        if (minute < 10) {
            minuteS = "0" + Integer.toString(minute);
        }time.setText("Time:"+" "+minuteS+":"+secondS);
        timePass++;
    }

    /**
     * Quit button action
     * @param e
     */
    protected void quit(ActionEvent e) {
        resetBoard();
        gridLockFrame.setScreen(ScreenState.MENU);
    }

    /**
     * Replay game button action
     * @param e
     */
    protected void replay(ActionEvent e) {
        resetBoard();
        gridLockFrame.setScreen(ScreenState.INGAME);
    }

    protected void newGame(ActionEvent e) {
        board.setNewMap(boardSettings.getDifficulty());
        resetBoard();
        gridLockFrame.setScreen(ScreenState.INGAME);
    }

    public void enableTops(boolean enabled) {
        for (Component c : tops.getComponents()) {
            c.setEnabled(enabled);
        }
    }

    /**
     * Undo function, perform car animation
     * and disable menu bar
     */
    public void undo() {
        Thread undo = boardScreen.undo();
        if (undo == null) return;
        enableTops(false);
        Thread reenable = new Thread(()-> {
            while (undo.isAlive()) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            enableTops(true);
        });
        undo.start();
        reenable.start();

        refreshFrame();
    }

    /**
     * Reset the board
     */
    public void resetBoard() {
        removeAll();
        setLayout(new GridBagLayout());

        c.gridx = 1;
        c.gridy = 0;
        add(tops,c);
        addBoard();

        resetTimer();
        board.resetBoard();
        boardScreen.initialiseBoard();
        maxHints = boardSettings.getMaxHints();
        hintsUsed = 0;
        updateCounter();
        updateHintsCounter();
        refreshFrame();
    }

    /**
     * Next level button in win menu
     * Creates next map
     * @param e
     */
    protected void nextLevel(ActionEvent e) {
        resetBoard();
    }

    /**
     * Refresh the frame for repainting
     */
    public void refreshFrame() {
        gridLockFrame.getFrame().revalidate();
        gridLockFrame.getFrame().getContentPane().repaint();
    }

    /**
     * Reset the timer
     */
    public void resetTimer() {
        timePass= 0;
    }


    /**
     * Scales a buffered image to the grid dimensions of the board.
     * @pre assumes image is in the correct ratio to be scaled
     * @param img
     * @return buffered image scaled to the grid dimensions.
     */
    public BufferedImage resize(BufferedImage img) {
        int shortestSide = Math.min(img.getHeight(),img.getWidth());
        double scale = gridDimensions/(double)shortestSide;
        return toBufferedImage(
                img.getScaledInstance((int)(img.getWidth()*scale),(int)(img.getHeight()*scale),Image.SCALE_SMOOTH));
    }

    private Color color = new Color(138,185,66);
    @Override
    public void paintComponent(Graphics g) {
        g.drawImage(background, 0, 0, gridLockFrame.getFrame().getWidth(), gridLockFrame.getFrame().getHeight(), null);
        g.setColor(color);
        g.fillRect(0, 0, 99999, 40);

        Point p = new Point(boardScreen.getPlayerPoint());
        p.y += boardScreen.getY();
        g.drawImage(road,0,p.y,null);
        g.drawImage(road,boardScreen.getLocation().x+(board.getDimensions()+1)*gridDimensions,p.y,null);
    }


    /**
     * Get grid dimension
     * @return gridDimensions
     */
    public int getGridDimensions() {
        return gridDimensions;
    }
}
