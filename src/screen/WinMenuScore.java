package screen;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static screen.Utils.*;

public class WinMenuScore extends JLayeredPane {
    private int backgroundWidth;
    private int backgroundHeight;
    private MenuPanel menuPanel;
    private BufferedImage background;
    public WinMenuScore(int score, int width, int height) {
        int coordinateX = 0;
        int coordinateY = 0;
        backgroundWidth = width;
        backgroundHeight = height;
        menuPanel = new MenuPanel(score);
        setLayout(null);
        coordinateX = (width - 457)/2;
        coordinateY = (height - 338)/2;
        menuPanel.setBounds(coordinateX,coordinateY,457,338);
        add(menuPanel);

    }
    public void setReplay(ActionListener e) {
        menuPanel.setReplayListener(e);
    }
    public void setQuit(ActionListener e) {
        menuPanel.setQuitListener(e);
    }
    public void setNewGame(ActionListener e) {
        menuPanel.setNewGameListener(e);
    }
    @Override
    public void paintComponent(Graphics g) {
        try {
            File boardFile = new File("sprites"+File.separator+"subMenuBackground.png");
            background = ImageIO.read(boardFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        g.drawImage(background, 0, 0, backgroundWidth, backgroundHeight, null);
    }

    /**
     * Create a Inner JPanel that is only handle the layout of the score board
     */
    private class MenuPanel extends  JLayeredPane {
        JButton replay = new JButton("");
        JButton quit = new JButton("");
        JButton newGame = new JButton("");
        private MenuPanel(int score) {
            setLayout(null);


            JLabel star1 = new JLabel();
            JLabel star2 = new JLabel();
            JLabel star3 = new JLabel();
            try {
                star1.setIcon(Sprites.createScaledImageIcon("winMenuStar.png",99,95));
                star2.setIcon(Sprites.createScaledImageIcon("winMenuStar.png",99,95));
                star3.setIcon(Sprites.createScaledImageIcon("winMenuStar.png",99,95));
            } catch (IOException e) {
                e.printStackTrace();
            }

            star1.setVisible(false);
            star2.setVisible(false);
            star3.setVisible(false);

            star1.setBounds(54,48,99,95);
            star2.setBounds(179,48,99,95);
            star3.setBounds(306,48,99,95);

            setIcon(replay,"winMenuButtonReplay", 24,194,123,105);
            setIcon(newGame, "winMenuBottonNewGame",168, 194,123,105);
            setIcon(quit,"winMenuButtonQuit", 312,194,123, 105);
            add(replay);
            add(newGame);
            add(quit);
            add(star1);
            add(star2);
            add(star3);
            // set the score
            if (score > 0) {
                star1.setVisible(true);
                if (score > 1) {
                    star2.setVisible(true);
                    if (score > 2) {
                        star3.setVisible(true);

                    }
                }
            }

        }
        public void paintComponent(Graphics g) {
            BufferedImage Menubackground = null;
            try {
                File boardFile = new File("sprites"+File.separator+"winMenuBackground.png");
                Menubackground = ImageIO.read(boardFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            g.drawImage(Menubackground, 0, 0, 457, 338, null);
        }

        public void setReplayListener(ActionListener e) {
            replay.addActionListener(e);
        }
        public void setQuitListener(ActionListener e) {
            quit.addActionListener(e);
        }
        public void setNewGameListener(ActionListener e) {
            newGame.addActionListener(e);
        }
    }

}
