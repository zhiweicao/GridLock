package screen;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Sprites {
    private HashMap<BufferedImage,Integer> horizontalSprites = new HashMap<>();
    private HashMap<BufferedImage,Integer> verticalSprites = new HashMap<>();
    private HashMap<BufferedImage,Integer> playerSprites = new HashMap<>();

    public Sprites() {
        addImages();
    }

    public void addImages() {
        try {
            horizontalSprites.put(createImage("Horizontal_Block_2_1.png"),2);
            horizontalSprites.put(createImage("Horizontal_Block_2_2.png"),2);
            horizontalSprites.put(createImage("Horizontal_Block_2_3.png"),2);
            horizontalSprites.put(createImage("Horizontal_Block_3.png"),3);

            verticalSprites.put(createImage("Vertical_Block_2_1.png"),2);
            verticalSprites.put(createImage("Vertical_Block_2_2.png"),2);
            verticalSprites.put(createImage("Vertical_Block_2_3.png"),2);
            verticalSprites.put(createImage("Vertical_Block_3.png"),3);

            playerSprites.put(createImage("Player.png"),2);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets image given the file name
     * @param fileName
     * @return Buffered image from sprites folder
     * @throws IOException
     */
    public static BufferedImage createImage(String fileName) throws IOException {
        String directory = "sprites";
        File f = new File (directory,fileName);
        return ImageIO.read(f);
    }


    /**
     * Gets scaled image given the file name
     * @param fileName
     * @param width
     * @param height
     * @return
     * @throws IOException
     */
    public static BufferedImage createScaledImage(String fileName, int width, int height) throws IOException {
        String directory = "sprites";
        File f = new File (directory,fileName);
        BufferedImage tmpimg = ImageIO.read(f);
        Image img = tmpimg.getScaledInstance(width, height, BufferedImage.SCALE_SMOOTH);
        return Utils.toBufferedImage(img);
    }

    /**
     * Gets imageicon given the file name
     * @param fileName
     * @param width
     * @param height
     * @return imageIcon
     * @throws IOException
     */
    public static ImageIcon createScaledImageIcon(String fileName, int width, int height) throws IOException {
        return new ImageIcon(createScaledImage(fileName,width,height));
    }

    /**
     * Gets random car image
     * @param length
     * @param horizontal
     * @return
     */
    public BufferedImage randomImage(int length, boolean horizontal) {
        Stream<Map.Entry<BufferedImage,Integer>> stream = (horizontal) ? horizontalSprites.entrySet().stream() : verticalSprites.entrySet().stream();

        List<BufferedImage> list = stream.filter(e->e.getValue() == length).map(Map.Entry::getKey).collect(Collectors.toList());
        if (list.isEmpty()) return null;
        Collections.shuffle(list);
        return list.get(0);
    }

    /**
     * Get player car image
     * @param length
     * @return
     */
    public BufferedImage playerImage(int length) {
        Stream<Map.Entry<BufferedImage,Integer>> stream = playerSprites.entrySet().stream();
        List<BufferedImage> list = stream.filter(e->e.getValue() == length).map(Map.Entry::getKey).collect(Collectors.toList());
        if (list.isEmpty()) return null;
        Collections.shuffle(list);
        return list.get(0);
    }
}
