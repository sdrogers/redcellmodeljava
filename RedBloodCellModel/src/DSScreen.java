import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JPanel;

public class DSScreen extends MenuFrame implements ActionListener{
	private JButton timeOptions,cellfractionOptions,transportOptions,permeabilityOptions;
	private JButton runButton;
	private TimeScreen timeScreen;
	public DSScreen(RBCGui rbcgui,HashMap<String,String> options,RBC_model rbc) {
		super(rbc,rbcgui,options,"Dynamic state options");
		
		timeScreen = new TimeScreen(rbcgui,options,rbc);
		
		runButton = new JButton("Run");
		runButton.addActionListener(this);
		timeOptions = new JButton("Time options");
		timeOptions.addActionListener(this);
		panel.add(timeOptions);
		panel.add(runButton);
		
		
	}
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand() == "Time options") {
			System.out.println("Clicked time");
			timeScreen.makeVisible();
		}
		if(e.getActionCommand() == "Run") {
			this.setVisible(false);
			rbcgui.doneDS();
			
		}
	}
	public void makeVisible() {
		this.setVisible(true);
	}
	@Override
	public void grabOptions() {
		// TODO Auto-generated method stub
		
	}
}
