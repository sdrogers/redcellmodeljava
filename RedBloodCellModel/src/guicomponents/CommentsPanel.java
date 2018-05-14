package guicomponents;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import utilities.ExperimentalSettings;

public class CommentsPanel extends JPanel {
	private ExperimentalSettings experimentalSettings;
	private JTextArea ta;
	private JButton editButton;
	public CommentsPanel(ExperimentalSettings es) {
		this.experimentalSettings = es;
		ta = new JTextArea(this.experimentalSettings.getOverallComments(),10,30);
		this.add(new JScrollPane(ta));
	}
	public String processComments() {
		// makes sure that each line starts with #
		String rString = "";
		String commentString = ta.getText();
		String[] lines = commentString.split("\n");
		for(String line: lines) {
			if(line.length() > 0) { // Ignore empty lines
				String newline = line;
				if(!newline.startsWith("#")) {
					newline = "# " + newline;
				}
				rString += newline + "\n";
			}
		}
		this.ta.setText(rString);
		return rString;
	}

}
