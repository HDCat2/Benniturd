import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class ImagePanel extends JPanel{

    private BufferedImage image;
    private int w, h;

    public ImagePanel(String fname, int w, int h) {
       try {                
          image = ImageIO.read(new File(fname));
       } catch (IOException ex) {}
       this.w = w;
       this.h = h;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, w, h, this);            
    }

}