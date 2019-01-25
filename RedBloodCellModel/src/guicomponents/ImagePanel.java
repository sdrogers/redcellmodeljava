package guicomponents;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class ImagePanel extends JPanel{
	private BufferedImage image;
	public ImagePanel() {
		try {
			InputStream is = ClassLoader.getSystemResourceAsStream("SettingFiles/RBC.jpg");
			image = ImageIO.read(
					ClassLoader.getSystemResourceAsStream("SettingFiles/RBC.jpg"));
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	@Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
//        g.drawImage(image, 0, 0, this);
        	g.drawImage(image, 0, 0, 500,500,0,0,1050,1050,this);
    }
}
