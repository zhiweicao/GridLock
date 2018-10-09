package screen;

import backend.Board;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.event.MouseAdapter;
import java.io.File;
import java.io.IOException;

public class tutorialScreen extends GameScreen {
    private JLabel finger_normal;
    private JLabel finger_press;
    private JLabel guide;
    private JLabel guide2;
    private Point guideSize = new Point(1000,600);
    private Point guideStart =  new Point(0,0);
    private Timer counterTimer;
    private boolean trigger_finger = false;
    private boolean firstLevel = true;

    private WinMenu winMenu;

    private int fingerSize = 50;
    private Point fingerStart = new Point(350,210);
    private Point fingerEnd = new Point(700,210);
    private int timePass = 0;
    private int speed = 1;

    private int backgroundWidth = 1000;
    private int backgroundHeight = 600;
    private JPanel tops;
    protected JLabel time  = new JLabel("Time: 00:00");
    protected JLabel counter;
    protected JLabel hints;
    protected Timer t;

    private boolean guide2Trigger =false;
    private int boardWidth = 600;
    private int boardHeight = 600;
    private int menuBarHeight = 40;
    private int timePassTimer = 0;
    private boolean isWin = false;
    private boolean winQuit = false;
    private boolean winNext = false;
    private Timer eventTimer;

    public tutorialScreen(Board board, GridLockFrame gridLockFrame, BoardSettings boardSettings) {
        this.board = board;
        this.boardScreen = new BoardScreen(this, board,boardSettings);
        this.gridLockFrame = gridLockFrame;
        this.boardSettings = boardSettings;

        setLayout(null);
        tops = initialiseMenuBar();
        add(tops);
        tops.setPreferredSize(new Dimension(1000,50));
        tops.setBounds(0,0,1000,50);
        // disable all the button
        for (Component c: tops.getComponents()) {
            if (c instanceof JButton) {
                for (ActionListener act: ((JButton) c).getActionListeners()) {
                    ((JButton) c).removeActionListener(act);
                }
            }
        }
        // keep Quit working
        Component[] all = tops.getComponents();
        ((JButton)all[all.length-1]).addActionListener(this::quit);


        guide = new JLabel();
        guide2 = new JLabel();
        finger_normal = new JLabel();
        finger_press = new JLabel();

        boardScreen.setPreferredSize(new Dimension(500,400));
        add(boardScreen,new Integer(1));
        boardScreen.setBounds(150, menuBarHeight+5, boardWidth, boardHeight);
        setComponentZOrder(boardScreen,0);


        add(finger_normal,new Integer(2));
        add(finger_press,new Integer(2));
        finger_normal.setVisible(false);
        finger_press.setVisible(false);

        add(guide, new Integer(3));
        add(guide2,new Integer(3));
        guide.setBounds(guideStart.x,guideStart.y, guideSize.x, guideSize.y);
        guide.addMouseListener( new guidDisappear());
        guide2.setBounds(guideStart.x,guideStart.y, guideSize.x, guideSize.y);
        guide2.addMouseListener( new guidDisappear());

        winMenu = new WinMenu();
        add(winMenu,new Integer(4));
        winMenu.setBounds(0,0,1000,600);
        winMenu.setVisible(false);

        winMenu.setQuitTrigger(this::quit);
        winMenu.setNextLevelTrigger(this::nextLevel);
        TutorialDimension();
        try {
            guide.setIcon(Sprites.createScaledImageIcon("guide.png",guideSize.x, guideSize.y));
            guide2.setIcon(Sprites.createScaledImageIcon("guide2.png",guideSize.x, guideSize.y));
            finger_normal.setIcon(Sprites.createScaledImageIcon("finger_normal.png",fingerSize,fingerSize));
            finger_press.setIcon(Sprites.createScaledImageIcon("finger_press.png",fingerSize, fingerSize));
        } catch (IOException e) {
            e.printStackTrace();
        }
        guide2.setVisible(false);
        eventTimer = new Timer(10,this::eventTimer);
        eventTimer.start();
        t = new Timer(1000,this::timer);
        t.start();
    }


    /**
     * A trigger to make the guide vanish
     */
    class guidDisappear extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            if (guide2Trigger) {
                guide2.setVisible(false);
                trigger_finger = true;
            } else {
                guide2Trigger = true;
                guide.setVisible(false);
                guide2.setVisible(true);
            }
        }
    }

    /**
     * This is a Timer that used to display the finger as expected
     */
    private void eventTimer(ActionEvent e) {
        if (trigger_finger) {
            timePass++;
        }
        if (firstLevel) {
            if (timePass == 20) {
                finger_normal.setVisible(true);
                moveToFront(finger_normal);
                finger_normal.setBounds(fingerStart.x,fingerStart.y, fingerSize, fingerSize);
            }
            if (timePass == 150) {
                finger_normal.setVisible(false);
                finger_press.setVisible(true);
                moveToFront(finger_press);
                finger_press.setBounds(fingerStart.x,fingerStart.y, fingerSize, fingerSize);

            }
            if (timePass > 150) {
                if (fingerStart.x < fingerEnd.x) {
                    if (timePass % 5 == 0) speed++;
                    fingerStart.x = fingerStart.x + speed;
                    finger_press.setLocation(fingerStart);
                }
            }
        } else {
            switch (timePass) {
                case 150:
                    fingerStart = new Point(500,280);
                    fingerEnd = new Point(700,280);
                    break;
                case 400:
                    fingerStart = new Point(420,35);
                    fingerEnd = new Point(420,400);
                    break;
                case 700:
                    fingerStart = new Point(350,210);
                    fingerEnd = new Point(700,210);
                    break;
            }
            if (timePass > 150 && timePass < 400) {
                if (fingerStart.x < fingerEnd.x) {
                    fingerStart.x += speed;
                    finger_press.setLocation(fingerStart);
                }
            } else if (timePass > 400  && timePass < 700) {
                if (fingerStart.y < fingerEnd.y) {
                    fingerStart.y += speed;
                    finger_press.setLocation(fingerStart);
                }
            } else if (timePass > 700) {
                if (fingerStart.x < fingerEnd.x) {
                    fingerStart.x += speed;
                    finger_press.setLocation(fingerStart);
                }
            }
        }
        refreshFrame();
    }

    /**
     * Make the winMenu appear.
     */
    @Override
    public void win() {
        winMenu.setVisible(true);
    }

    /**
     * There is several setting to make sure the quit executing currectly
     */
    @Override
    protected void quit(ActionEvent e) {
        boardScreen.initialiseBoard();
        refreshFrame();
        gridLockFrame.setScreen(ScreenState.MENU);
        eventTimer.stop();
    }

    /**
     * prepare the screen to the nextLevel tutorial
     */
    protected void nextLevel(ActionEvent e) {
        firstLevel = false;
        winMenu.setVisible(false);
        winMenu.setNewGameTrigger(this::quit);
        winMenu.switchScreen();
        board.loadTutorialMapEnd();
        boardScreen.initialiseBoard();
        refreshFrame();
        timePass = 0;
        speed = 5;
        eventTimer.restart();
        finger_normal.setVisible(false);
        finger_press.setVisible(true);
    }


    /**
     * Fix the tutorial screen layout problem
     */
    public void TutorialDimension() {
        gridDimensions = 75;
        gridLockFrame.getFrame().revalidate();
        boardScreen.initialiseBoard();
    }


}
