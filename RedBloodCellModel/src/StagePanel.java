import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class StagePanel extends JPanel implements ActionListener {
	private JButton editButton,deleteButton;
	private DSSettings dSSettings;
	private JTextArea commentArea;
	private ExperimentScreen experimentScreen;
	public StagePanel(DSSettings dSSettings,ExperimentScreen es) {
		JPanel contentPanel = new JPanel(new GridLayout(0,2));
		this.dSSettings = dSSettings;
		this.experimentScreen = es;
		commentArea = new JTextArea(dSSettings.toString(),5,20);
		commentArea.setEditable(false);
		contentPanel.add(new JScrollPane(commentArea));
		this.add(contentPanel);
		
		JPanel buttonPanel = new JPanel(new GridLayout(0,1));
		editButton = new JButton("Edit");
		deleteButton = new JButton("delete");
		deleteButton.addActionListener(this);
		buttonPanel.add(editButton);
		buttonPanel.add(deleteButton);
		contentPanel.add(buttonPanel);
	}
	
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == deleteButton) {
			// instruct the parent to delete this stage
			this.experimentScreen.deleteStage(this.dSSettings);
		}
		
	}
	

}
