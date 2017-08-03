import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class TimeOptions extends MenuFrame {
	private JTextField timeField;
	public TimeOptions(RBCGui rbcgui,HashMap<String,String> options,RBC_model rbc) {
		super(rbc,rbcgui,options,"Time Options");
		
		panel.add(new JLabel("Experiment time:"));
		timeField = new JTextField("30",10);
		panel.add(timeField);

		panel.add(new JLabel());
		JButton nextButton = new JButton("Next");
		nextButton.addActionListener(this);
		panel.add(nextButton);
	}
	public void makeVisible() {
		this.setVisible(true);
	}
	public void grabOptions() {
		// TODO Auto-generated method stub
		
	}


}
