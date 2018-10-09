package screen;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import static screen.Utils.*;

public class WinMenu extends JLayeredPane {
    private int backgroundWidth = 1000;
    private int backgroundHeight = 600;
    private int buttonWidth = 230;
    private int buttonHeight = 71;
    private JButton nextLevel = new JButton("");
    private JButton quit = new JButton("");
    private JButton newGame = new JButton("");
    private String backgroundName;
    private BufferedImage background;
    public WinMenu(){

        setLayout(null);
        add(nextLevel, new Integer(1));
        add(quit,new Integer(1));
        add(newGame,new Integer(1));
        setIcon(newGame,"tutorialWinMenuButtonNewGame",392,237,buttonWidth,buttonHeight);
        setIcon(nextLevel,"tutorialWinMenuButtonNextLevel",392,237,buttonWidth,buttonHeight);
        setIcon(quit,"tutorialWinMenuButtonQuit",392,325,buttonWidth,buttonHeight);



        newGame.setVisible(false);
        nextLevel.setVisible(true);
        backgroundName = "tutorialOneFinish.png";
    }

    @Override
    public void paintComponent(Graphics g) {
        try {
            background = Sprites.createImage(backgroundName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        g.drawImage(background, 0, 0, backgroundWidth, backgroundHeight, null);
    }

    /**
     * Add a listener to the buttons
     */
    public void setQuitTrigger(ActionListener actionListener) {
        quit.addActionListener(actionListener);
    }
    public void setNextLevelTrigger(ActionListener actionListener) {
        nextLevel.addActionListener(actionListener);
    }
    public void setNewGameTrigger(ActionListener actionListener) {
        newGame.addActionListener(actionListener);
    }

    /**
     * Prepare the win Menu for the second tutorial
     */
    public void switchScreen() {
        newGame.setVisible(true);
        nextLevel.setVisible(false);
        backgroundName = "tutorialTwoFinish.png";
    }
}
