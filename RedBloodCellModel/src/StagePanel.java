import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class StagePanel extends JPanel implements ActionListener, Updateable {
	private JButton editTimeButton,editTransportButton,editTempButton,deleteButton,editFractionButton,editPiezoButton;
	private DSSettings dSSettings;
	private JTextArea commentArea;
	private JTextArea optionArea;
	private ExperimentScreen experimentScreen;
	private OptionsFrame timeScreen,tempPermScreen,transportScreen,fractionScreen,piezoScreen;
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
		editTransportButton = new JButton("Transport Inhibition");
		editTempButton = new JButton("Temperature & Permeabilities");
		editFractionButton = new JButton("Cell Fraction & Medium Composition");
		editPiezoButton = new JButton("PIEZO");
		editTimeButton.addActionListener(this);
		editTransportButton.addActionListener(this);
		editTempButton.addActionListener(this);
		editFractionButton.addActionListener(this);
		editPiezoButton.addActionListener(this);
		deleteButton = new JButton("Delete This DS Stage");
		deleteButton.addActionListener(this);
		buttonPanel.add(editTimeButton);
		buttonPanel.add(editFractionButton);
		buttonPanel.add(editTempButton);
		buttonPanel.add(editTransportButton);
		buttonPanel.add(editPiezoButton);
		
		
		
		
		
		buttonPanel.add(deleteButton);
		contentPanel.add(buttonPanel);
	}
	public void update() {
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
			timeScreen = new OptionsFrame("Time Options","SettingFiles/timeOptions.csv",this.dSSettings.getOptions(), this,"");
			timeScreen.makeVisible();
		}else if(e.getSource() == editTempButton) {
			tempPermScreen = new OptionsFrame("Temperature & permeability Options","SettingFiles/temppermeabilityDSOptions.csv",this.dSSettings.getOptions(),this,"");
			tempPermScreen.makeVisible();
		}else if(e.getSource() == editTransportButton) {
			String helpText = "The default Fmax value, F, is modified according to F*(100-X)/100 "
					+ "where X is the % inhibition you wish to apply."
					+ "  F stays modified in successive stages unless modified again.  "
					+ "Each successive modification applies the entered % inhibition with regard to the original default value."
					+ "  To return to the original default value enter “0” (zero).";
//			String helpText = "Percent changes for each transporter modify only their default Fmax values.\n"
//					+ "Default values are modified according to D*(100-I/100) where D is the default and I is the value entered here.\n"
//					+ "Fmax values stay modified in successive stages.\n"
//					+ "If changed again, change applies to original default value.\n"
//					+ "For example, to inhibit by 80% in stage 1 enter 80. To return to the default in stage 2, enter 100.";
			transportScreen = new OptionsFrame("Transport Options","SettingFiles/transportDSOptions.csv",this.dSSettings.getOptions(),this,helpText);
			transportScreen.makeVisible();
		}else if(e.getSource() == editFractionButton) {
			fractionScreen = new OptionsFrame("Fraction and Medium Options","SettingFiles/cellfractionDSOptions.csv",this.dSSettings.getOptions(),this,"");
			fractionScreen.makeVisible();
		}else if(e.getSource() == editPiezoButton) {
			piezoScreen = new OptionsFrame("Piezo Options","SettingFiles/piezoDSOptions.csv",this.dSSettings.getOptions(),this,"");
			piezoScreen.makeVisible();
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
