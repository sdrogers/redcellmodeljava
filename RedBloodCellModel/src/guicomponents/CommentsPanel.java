package guicomponents;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import utilities.ExperimentalSettings;

// Wrapper for a JPanel and text area that processes the 
// comments to ensure formatting is correct

public class CommentsPanel extends JPanel {
	private ExperimentalSettings experimentalSettings;
	private JTextArea ta;
	private JButton editButton;
	private JTextField fileName;
	public CommentsPanel(ExperimentalSettings es) {
		this.experimentalSettings = es;
//		ta = new JTextArea(this.experimentalSettings.getOverallComments(),10,30);
//		this.add(new JScrollPane(ta));
		fileName = new JTextField(50);
		fileName.setEditable(false);
		this.add(new JLabel("Protocol file:"));
		this.add(fileName);
		processFilename();
		
	}
	public void processFilename() {
		if(this.experimentalSettings.getFileName() != null) {
			this.fileName.setText(this.experimentalSettings.getFileName());
		}
	}
	public String processComments() {
		// makes sure that each line starts with #
//		String rString = "";
//		String commentString = ta.getText();
//		String[] lines = commentString.split("\n");
//		for(String line: lines) {
//			if(line.length() > 0) { // Ignore empty lines
//				String newline = line;
//				if(!newline.startsWith("#")) {
//					newline = "# " + newline;
//				}
//				rString += newline + "\n";
//			}
//		}
//		this.ta.setText(rString);
//		return rString;
		return ""; // TODO: REMOVE ALL THIS
	}

}
