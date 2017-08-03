import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class WelcomeFrame extends MenuFrame {
	private RBCGui parent;
	public WelcomeFrame(RBCGui rbcgui, HashMap<String,String> options, RBC_model rbc) {
		super(rbc,rbcgui,options,"Welcome");
		
		JButton startButton = new JButton("Next");
		startButton.addActionListener(this);
		panel.add(startButton);
		
	}
	@Override
	public void grabOptions() {
		// TODO Auto-generated method stub
		// Does nothing
	}
	
}
