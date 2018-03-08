import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class StagePanel extends JPanel implements ActionListener {
	private JButton editTimeButton,editTransportButton,editTempButton,deleteButton,editFractionButton;
	private DSSettings dSSettings;
	private JTextArea commentArea;
	private JTextArea optionArea;
	private ExperimentScreen experimentScreen;
	private OptionsFrame timeScreen,tempPermScreen,transportScreen,fractionScreen;
	public StagePanel(DSSettings dSSettings,ExperimentScreen es) {
		JPanel outerContent = new JPanel(new BorderLayout());
		JPanel contentPanel = new JPanel(new GridLayout(0,2));
		outerContent.add(contentPanel,BorderLayout.CENTER);
		this.dSSettings = dSSettings;
		this.experimentScreen = es;
		commentArea = new JTextArea(dSSettings.getComments(),3,20);
		
		
		optionArea = new JTextArea(dSSettings.getOptionString(),5,20);
		optionArea.setEditable(false);
		contentPanel.add(optionArea);
		
		outerContent.add(new JScrollPane(commentArea),BorderLayout.NORTH);
		this.add(outerContent);
		
		JPanel buttonPanel = new JPanel(new GridLayout(0,1));
		editTimeButton = new JButton("Time & Data Output Frequency");
		editTransportButton = new JButton("Transport Inhibition & Stimulation");
		editTempButton = new JButton("Temperature & Permeabilities");
		editFractionButton = new JButton("Cell Fraction & Medium Composition");
		editTimeButton.addActionListener(this);
		editTransportButton.addActionListener(this);
		editTempButton.addActionListener(this);
		editFractionButton.addActionListener(this);
		deleteButton = new JButton("Delete This DS Stage");
		deleteButton.addActionListener(this);
		buttonPanel.add(editTimeButton);
		buttonPanel.add(editFractionButton);
		buttonPanel.add(editTempButton);
		buttonPanel.add(editTransportButton);
		
		
		
		
		
		buttonPanel.add(deleteButton);
		contentPanel.add(buttonPanel);
	}
	public void updateStagePanel() {
		this.optionArea.setText(dSSettings.getOptionString());
	}
	public void disableDelete() {
		this.deleteButton.setEnabled(false);
	}
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == deleteButton) {
			// instruct the parent to delete this stage
			this.experimentScreen.deleteStage(this.dSSettings);
		}else if(e.getSource() == editTimeButton) {
			// edit the parameters of this DS
			timeScreen = new OptionsFrame("Time Options","SettingFiles/timeOptions.csv",this.dSSettings.getOptions(), this);
			timeScreen.makeVisible();
		}else if(e.getSource() == editTempButton) {
			tempPermScreen = new OptionsFrame("Temperature & permeability Options","SettingFiles/temppermeabilityDSOptions.csv",this.dSSettings.getOptions(),this);
			tempPermScreen.makeVisible();
		}else if(e.getSource() == editTransportButton) {
			transportScreen = new OptionsFrame("Transport Options","SettingFiles/transportDSOptions.csv",this.dSSettings.getOptions(),this);
			transportScreen.makeVisible();
		}else if(e.getSource() == editFractionButton) {
			fractionScreen = new OptionsFrame("Fraction and Medium Options","SettingFiles/cellfractionDSOptions.csv",this.dSSettings.getOptions(),this);
			fractionScreen.makeVisible();
		}
		
	}
	public void processComment() {
		// checks for # at beginning of each comment line
		String newString = "";
		String commentString = commentArea.getText();
		for(String line: commentString.split("\n")) {
			if(line.length() > 0) {
				String newLine = line;
				if(!line.startsWith("#")) {
					newLine = "# " + line;
				}
				newString += newLine + "\n";
			}
		}
		this.commentArea.setText(newString);
		this.dSSettings.setComments(newString);
	}
	

}
