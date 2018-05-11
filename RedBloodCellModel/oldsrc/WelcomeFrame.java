import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class WelcomeFrame extends JFrame implements ActionListener{
	private JPanel layoutPanel;
	private JButton dsButton;
	private JButton rsButton;
	private DSScreen dsScreen;
	HashMap<String,String> options;
	private RBC_model rbc_model;
	public WelcomeFrame() {
		
		this.setTitle("RBC model");
		this.setSize(300, 300);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		layoutPanel = new JPanel();
		dsButton = new JButton("Leave RS defaults and skip to DS screen");
		dsButton.addActionListener(this);
		rsButton = new JButton("Modify RS");
		rsButton.addActionListener(this);
		layoutPanel.add(rsButton);
		rsButton.setEnabled(false);
		layoutPanel.add(dsButton);
		
		options = new HashMap<String,String>();
		
		rbc_model = new RBC_model();
		dsScreen = new DSScreen(options,rbc_model);
		this.add(layoutPanel);
		this.setVisible(true);
	}
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == dsButton) {
			this.setVisible(false);
			dsScreen.setVisible(true);
		}
	}
	public static void main(String[] args) {
		new WelcomeFrame();
	}
	
}
