import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class RSPanel extends JPanel {
	private ExperimentalSettings experimentalSettings;
	private JButton editButton;
	public RSPanel(ExperimentalSettings es) {
		this.experimentalSettings = es;
		JPanel contentPanel = new JPanel(new GridLayout(0,1));
		JTextArea rSArea = new JTextArea(this.experimentalSettings.getRSComments(),3,30);
		rSArea.setEditable(false);
		contentPanel.add(new JScrollPane(rSArea));
		
		JTextArea oArea = new JTextArea(this.experimentalSettings.getRSString(),5,30);
		oArea.setEditable(false);
		contentPanel.add(new JScrollPane(oArea));
		
		editButton = new JButton("Edit");
		contentPanel.add(editButton);
		
		this.add(contentPanel);
	}
}
