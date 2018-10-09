package screen;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import java.io.*;
import javax.imageio.ImageIO;
public class Utils {

    //https://stackoverflow.com/questions/13605248/java-converting-image-to-bufferedimage
    /**
     * Converts a given Image into a BufferedImage
     *
     * @param img The Image to be converted
     * @return The converted BufferedImage
     */
    public static BufferedImage toBufferedImage(Image img)
    {
        if (img instanceof BufferedImage)
        {
            return (BufferedImage) img;
        }

        // Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        // Return the buffered image
        return bimage;
    }

    /**
     * Set the button in right position, reduce the tedious code. (only used for the null layout)
     * @param button   Button need to be set
     * @param fileName  filename of the background image(must be store in the sprites)
     * @param initialX  The x coordinate that will be display on the screen
     * @param initialY  The y coordinate that will be display on the screen
     * @param iconSizeWidth the button width
     * @param iconSizeHeight  the button height
     */
    public static void setIcon(JButton button, String fileName, int initialX, int initialY,int iconSizeWidth, int iconSizeHeight) {
        try {
            File tmpFile = new File("sprites"+File.separator+fileName+".png");
            BufferedImage tmpimg = ImageIO.read(tmpFile);
            Image img = tmpimg.getScaledInstance(iconSizeWidth, iconSizeHeight, BufferedImage.SCALE_SMOOTH);
            ImageIcon icon = new ImageIcon(img);
            button.setIcon(icon);
            button.setBorderPainted(false);
            button.setContentAreaFilled(false);
            button.setBounds(initialX,initialY,iconSizeWidth,iconSizeHeight);
        } catch (IOException e) {
        }

    }

}
