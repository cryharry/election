package election;

import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class JImagePanel extends JPanel {
	Image image;
	public JImagePanel() {}
	public JPanel getPanel(String st_id) {
		ImageIcon icon = new ImageIcon("C:\\Uni_cool\\image\\"+st_id+".jpg");
		JImagePanel panel = new JImagePanel(icon.getImage());
		return panel;
	}
	public JImagePanel(Image image) {
		this.image = image;
	}
	public void paintComponent(Graphics g) {
		g.drawImage(this.image,0,0,this);
	}
	public static void main(String[] args) {
		
	}
}
