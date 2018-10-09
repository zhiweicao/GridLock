package screen;

import backend.Board;

import javax.imageio.ImageIO;
import javax.swing.*;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.*;
import static screen.Utils.*;
public class Menu extends JLayeredPane {
    private JButton play;
    private JButton help;
    private JButton quit;
    private JLabel title;
    private SubMenu subMenu;
    private GridLockFrame gridLockFrame;
    private int iconSize;
    private BoardSettings boardSettings;

    public Menu(GridLockFrame gridLockFrame, BoardSettings boardSettings) {
        this.boardSettings = boardSettings;
        iconSize = 100;
        setLayout(null);
        this.gridLockFrame = gridLockFrame;
        play = new JButton("");
        help = new JButton("");
        quit = new JButton("");
        title = new JLabel();
        try {
            title.setIcon(Sprites.createScaledImageIcon("title.png", 572, 101));

        } catch (IOException e) {
            e.printStackTrace();
        }

        title.setBounds(60, 60, 572, 101);

        setIcon(play, "play", 50, 360, iconSize, iconSize);
        setIcon(help, "help", 175, 360, iconSize, iconSize);
        setIcon(quit, "quit", 300, 360, iconSize, iconSize);

        play.addActionListener(this::submenu);
        help.addActionListener(this::tutorial);
        quit.addActionListener(this::quit);

        add(play);
        add(help);
        add(quit);
        add(title);

        subMenu = new SubMenu();
        add(subMenu);
        subMenu.setBounds(0, 0, 1000, 600);
        subMenu.setVisible(false);
        subMenu.setEasy(this::setEasy);
        subMenu.setMedium(this::setMedium);
        subMenu.setHard(this::setHard);
        subMenu.setAiMode(this::setAiMode);

        setSize(200, 200);
    }


    private void submenu(ActionEvent e) {
        play.setVisible(false);
        help.setVisible(false);
        quit.setVisible(false);
        subMenu.setVisible(true);
        moveToFront(subMenu);
    }

    protected void resetMenu() {
        play.setVisible(true);
        help.setVisible(true);
        quit.setVisible(true);
        subMenu.setVisible(false);
    }

    private void tutorial(ActionEvent e) {
        gridLockFrame.setScreen(ScreenState.INTUTORIAL);
    }

    private void quit(ActionEvent e) {
        System.exit(0);
    }

    public void paintComponent(Graphics g) {
        String BackgoundAddress = "sprites" + File.separator + "Manu_Background.png";
        try {
            BufferedImage background = ImageIO.read(new File(BackgoundAddress));
            Image img = background.getScaledInstance(1000, 600, BufferedImage.SCALE_SMOOTH);
            g.drawImage(img, 0, 0, null);

        } catch (IOException e) {

        }
    }


    private void setEasy(ActionEvent e) {
        //set easy
//        boardSettings.setDifficulty(1);
        new GuiWorker(gridLockFrame, this,boardSettings,1).execute();
    }
    private void setMedium(ActionEvent e) {
        //set medium
        boardSettings.setAiMode(false);
        new GuiWorker(gridLockFrame, this,boardSettings,2).execute();

    }
    private void setHard(ActionEvent e) {
        //set hard
        boardSettings.setAiMode(false);
        new GuiWorker(gridLockFrame, this,boardSettings,3).execute();

    }
    private void setAiMode(ActionEvent e) {
        //set Ai Mode
        boardSettings.setAiMode(true);
        new GuiWorker(gridLockFrame, this,boardSettings,1).execute();
    }

}
class GuiWorker extends SwingWorker<Integer, Integer> {

    /*
     * https://stackoverflow.com/questions/9392227/resizing-animated-gif-while-keeping-its-animation-using-java
     * This should just create a frame that will hold a progress bar until the
     * work is done. Once done, it should remove the progress bar from the dialog
     * and add a label saying the task complete.
     */

    private GridLockFrame frame;
    private BoardSettings boardSettings;
    private Menu menu;
    private int difficulty;
    private JLabel loading;
    public GuiWorker(GridLockFrame frame, Menu menu, BoardSettings boardSettings, int difficulty) {
        this.frame = frame;
        this.menu = menu;
        this.boardSettings = boardSettings;
        this.difficulty = difficulty;
        ImageIcon image = new ImageIcon("sprites/loading.gif");
        image.setImage(image.getImage().getScaledInstance(100, 100, Image.SCALE_DEFAULT));
        loading = new JLabel(image);
        loading.setBounds(450,250,100,100);

    }

    @Override
    protected Integer doInBackground() throws Exception {
        menu.add(loading,new Integer(3));
        frame.getFrame().getContentPane().repaint();
        frame.getFrame().revalidate();
        boolean aiMode = boardSettings.isAiMode();
        boardSettings.setDifficulty(difficulty);
        boardSettings.setAiMode(aiMode);
        menu.resetMenu();
        return 0;
    }

    @Override
    protected void done() {
        menu.remove(loading);
        frame.setScreen(ScreenState.INGAME);
    }

}