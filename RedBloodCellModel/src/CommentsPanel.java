import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class CommentsPanel extends JPanel {
	private ExperimentalSettings experimentalSettings;
	private JButton editButton;
	public CommentsPanel(ExperimentalSettings es) {
		this.experimentalSettings = es;
		JTextArea ta = new JTextArea(this.experimentalSettings.getOverallComments(),10,30);
		this.add(new JScrollPane(ta));
	}

}
