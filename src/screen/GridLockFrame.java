package screen;

import backend.Board;

import javax.swing.*;
import java.awt.*;

public class GridLockFrame {
    private JFrame frame;
    private Menu menu;
    private GameScreen gameScreen;
    private tutorialScreen tutorialScreen;
    private Board board;
    private BoardSettings boardSettings;
    public GridLockFrame() {
        this.board = new Board();
        this.frame = new JFrame();
        this.boardSettings = new BoardSettings(board);
        this.menu = new Menu(this,boardSettings);
        this.gameScreen = new GameScreen(board,this, boardSettings);

        frame.setResizable(false);
        frame.getContentPane().add(menu);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(new Dimension(1000,600));
        frame.setLocationRelativeTo(null);

    }

    public void setVisible() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//    		frame.pack();
        frame.setVisible(true);
    }

    public void setScreen(ScreenState screenState) {
        switch (screenState) {
            case MENU:
                frame.getContentPane().removeAll();
                frame.getContentPane().add(menu);
                frame.setSize(new Dimension(1000,600));
                frame.setLocationRelativeTo( null );
                break;
            case INGAME:
                gameScreen.resetBoard();
                frame.getContentPane().removeAll();
                frame.getContentPane().add(gameScreen);
                frame.setSize(frame.getContentPane().getSize());
                frame.pack();
                break;
            case INTUTORIAL:
                board.loadTutorialMapIntro();
                tutorialScreen = new tutorialScreen(board, this,boardSettings);
                frame.getContentPane().removeAll();
                frame.getContentPane().add(tutorialScreen);
                frame.setSize(frame.getContentPane().getSize());
                frame.setSize(new Dimension(1000,600));
                frame.setLocationRelativeTo( null );
        }
        frame.getContentPane().repaint();
        frame.revalidate();
        frame.setLocationRelativeTo(null);
    }

    public JFrame getFrame() {
        return frame;
    }
}
