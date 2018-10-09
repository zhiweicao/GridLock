package screen;

import backend.Board;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import static screen.Utils.*;

class SubMenu extends JLayeredPane {
    private int backgroundWidth = 1000;
    private int backgroundHeight = 600;
    private int iconWidth = 244;
    private int iconHeight = 58;
    private BufferedImage background;
    private JButton easy = new JButton("");
    private JButton medium = new JButton("");
    private JButton hard = new JButton("");
    private JButton aiMode = new JButton("");

    public SubMenu(){
        try {
            background = Sprites.createImage("subMenuBackground.png");
        } catch (IOException e) {
            e.printStackTrace();
        }

        int topLeftCorner = 220;
        int gap = 10;

        add(easy);
        add(medium);
        add(hard);
        add(aiMode);
        setIcon(easy,"easyIcon",56,topLeftCorner,iconWidth,iconHeight);
        setIcon(medium,"mediumIcon",56,topLeftCorner + iconHeight + gap,iconWidth,iconHeight);
        setIcon(hard,"hardIcon",56,topLeftCorner + iconHeight*2 + gap*2,iconWidth,iconHeight);
        setIcon(aiMode,"aimodeIcon",56,topLeftCorner + iconHeight*3 + gap*3,iconWidth,iconHeight);

        setLayout(null);
    }
    @Override
    public void paintComponent(Graphics g) {
        g.drawImage(background, 0, 0, backgroundWidth, backgroundHeight, null);
    }

    /**
     * add listener for each button
     */
    public void setEasy(ActionListener e) {
        //set easy
        easy.addActionListener(e);
    }
    public void setMedium(ActionListener e) {
        //set medium
        medium.addActionListener(e);
    }
    public void setHard(ActionListener e) {
        //set hard
        hard.addActionListener(e);
    }
    public void setAiMode(ActionListener e) {
        aiMode.addActionListener(e);
    }

}
