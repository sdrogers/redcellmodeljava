package guicomponents;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class ImagePanel extends JPanel{
	private BufferedImage image;
	public ImagePanel() {
		this.setBackground(Color.BLUE);
		try {
			image = ImageIO.read(
					ClassLoader.getSystemResourceAsStream("SettingFiles/RBCannotated500.jpg"));
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	@Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int w = this.getWidth();
        int h = this.getHeight();
        g.drawImage(image, 0, 0, w,h,0,0,500,338,this);
//        	g.drawImage(image, 0, 0, 500,700,0,0,1050,1050,this);
    }
}
