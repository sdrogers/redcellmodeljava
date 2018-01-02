import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class DSScreen extends JFrame implements ActionListener{
	private JButton timeOptions,cellfractionOptions,transportOptions,permeabilityOptions;
	private JButton runButton;
	private OptionsFrame timeScreen,transportScreen,tempPermScreen;
	private JPanel layoutPanel;
	private RBCGui rbcgui;
	private boolean doneTime;
	public DSScreen(RBCGui rbcgui,HashMap<String,String> options,RBC_model rbc) {
		this.setTitle("Dynamic state options");
		this.rbcgui = rbcgui;
		this.setSize(800, 300);
		layoutPanel = new JPanel(new GridBagLayout());
		
		timeScreen = new OptionsFrame("Time Options","resources/settingfiles/timeOptions.csv",rbcgui,options, this);
		transportScreen = new OptionsFrame("Transport Options","resources/settingfiles/transportDSOptions.csv",rbcgui,options,this);
		tempPermScreen = new OptionsFrame("Temperature & permeability Options","resources/settingfiles/temppermeabilityDSOptions.csv",rbcgui,options,this);
		runButton = new JButton("Run");
		runButton.addActionListener(this);
		runButton.setEnabled(false);
		timeOptions = new JButton("Time options");
		timeOptions.addActionListener(this);
		transportOptions = new JButton("Transport options");
		transportOptions.addActionListener(this);
		permeabilityOptions = new JButton("Temperature & permeability options");
		permeabilityOptions.addActionListener(this);
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		layoutPanel.add(timeOptions,c);
		c.gridx = 1;
		layoutPanel.add(transportOptions,c);
		c.gridx = 2;
		layoutPanel.add(permeabilityOptions,c);
		
		c.gridy = 1;
		c.gridx = 0;
		c.gridwidth = 3;
		c.fill = GridBagConstraints.HORIZONTAL;
		layoutPanel.add(runButton,c);
		this.add(layoutPanel);
		
		
	}
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == timeOptions) {
			System.out.println("Clicked time");
			timeScreen.makeVisible();
			runButton.setEnabled(true); // need to check that something sensible has been done with time
			this.setVisible(false);
		}else if(e.getSource() == transportOptions) {
			transportScreen.makeVisible();
			this.setVisible(false);
		}else if(e.getSource() == permeabilityOptions) {
			tempPermScreen.makeVisible();
			this.setVisible(false);
		}else if(e.getActionCommand() == "Run") {
			this.setVisible(false);
			rbcgui.doneDS();
			
		}
	}
	public void makeVisible() {
		this.setVisible(true);
	}

}
