package guicomponents;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import utilities.DSSettings;
import utilities.Updateable;

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
			if(timeScreen == null) {
				timeScreen = new OptionsFrame("Time Options","/timeOptions.csv",this.dSSettings.getOptions(), this,"");
			}
			timeScreen.makeVisible();
		}else if(e.getSource() == editTempButton) {
			if(tempPermScreen == null) {
				tempPermScreen = new OptionsFrame("Temperature & permeability Options","/temppermeabilityDSOptions.csv",this.dSSettings.getOptions(),this,"");
			}
			tempPermScreen.makeVisible();
		}else if(e.getSource() == editTransportButton) {
			String helpText = "The default Fmax value, F, is modified according to F*(100-X)/100 "
					+ "where X is the % inhibition you wish to apply."
					+ "  F stays modified in successive stages unless modified again.  "
					+ "Each successive modification applies the entered % inhibition with regard to the original default value."
					+ "  To return to the original default value enter “0” (zero)."
					+ "\n\nThe same equation delivers stimulation if you enter negative numbers. "
					+ "It applies to JS and PMCA entries only. For an n-fold stimulation, enter “–n*100”. For example, enter “-200” for a two-fold stimulation";
			if(transportScreen == null) {
				transportScreen = new OptionsFrame("Transport Options","/transportDSOptions.csv",this.dSSettings.getOptions(),this,helpText);
			}
			transportScreen.makeVisible();
		}else if(e.getSource() == editFractionButton) {
			if(fractionScreen == null) {
				String helpText = "Default medium composition in the initial Reference steady-state (mM):\n" + 
						"NaCl, 145; KCl, 5; HEPES-Na, pH 7.4 at 37oC, 10; MgCl2, 0.2; CaCl2, 1.0";
				fractionScreen = new OptionsFrame("Fraction and Medium Options","/cellfractionDSOptions.csv",this.dSSettings.getOptions(),this,helpText);
			}
			fractionScreen.makeVisible();
		}else if(e.getSource() == editPiezoButton) {
			if(piezoScreen == null) {
				piezoScreen = new OptionsFrame("Piezo Options","/piezoDSOptions.csv",this.dSSettings.getOptions(),this,"");
			}
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
