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
	private OptionsFrame timeScreen,transportScreen,tempPermScreen;
	private JPanel layoutPanel;
	private boolean doneTime;
	private HashMap<String,String> options;
	private DSSettings dSSettings;
	private StagePanel parent;
	public DSScreen(DSSettings dSSettings,StagePanel parent) {
		this.setTitle("Dynamic state options");
		this.setSize(800, 300);
		this.dSSettings = dSSettings;
		this.parent = parent;
//		this.rbc = rbc;
		this.options = this.dSSettings.getOptions();
		
		layoutPanel = new JPanel(new GridBagLayout());
		
//		timeScreen = new OptionsFrame("Time Options","resources/settingfiles/timeOptions.csv",options, this);
		timeScreen = new OptionsFrame("Time Options","SettingFiles/timeOptions.csv",options, this.parent);
		transportScreen = new OptionsFrame("Transport Options","SettingFiles/transportDSOptions.csv",options,this.parent);
		tempPermScreen = new OptionsFrame("Temperature & permeability Options","SettingFiles/temppermeabilityDSOptions.csv",options,this.parent);
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
		this.add(layoutPanel);
		
		
	}
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == timeOptions) {
			System.out.println("Clicked time");
			timeScreen.makeVisible();
//			this.setVisible(false);
		}else if(e.getSource() == transportOptions) {
			transportScreen.makeVisible();
//			this.setVisible(false);
		}else if(e.getSource() == permeabilityOptions) {
			tempPermScreen.makeVisible();
//			this.setVisible(false);
		}
	}
	public void makeVisible() {
		this.setVisible(true);
	}


}
