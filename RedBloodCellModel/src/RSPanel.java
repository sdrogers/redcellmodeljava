import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import java.util.HashMap;

public class RSPanel extends JPanel implements ActionListener,Updateable{
	private ExperimentalSettings experimentalSettings;
	private JButton editButton;
	private OptionsFrame rsScreen;
	private JTextArea oArea;
	public RSPanel(ExperimentalSettings es) {
		this.experimentalSettings = es;
		JPanel contentPanel = new JPanel(new GridLayout(0,2));
		JTextArea rSArea = new JTextArea(this.experimentalSettings.getRSComments(),3,20);
		rSArea.setEditable(false);
		JPanel leftPanel = new JPanel(new GridLayout(0,1));
		leftPanel.add(new JScrollPane(rSArea));
		
		oArea = new JTextArea(this.experimentalSettings.getRSString(),5,20);
		oArea.setEditable(false);
		leftPanel.add(new JScrollPane(oArea));
		contentPanel.add(leftPanel);
		
		this.add(contentPanel);
		
		// Add the stage panels
		JPanel buttonPanel = new JPanel(new GridLayout(0,1));

		editButton = new JButton("Edit Reference State");
		editButton.addActionListener(this);
		buttonPanel.add(editButton);

//		naPump = new JButton("NaPump");
//		noName = new JButton("No name");
//		hBa = new JButton("hBa");
//		calcium = new JButton("Calcium");
//		buttonPanel.add(noName);
//		buttonPanel.add(hBa);
//		buttonPanel.add(calcium);
//		
//		naPump.addActionListener(this);
//		noName.addActionListener(this);
//		hBa.addActionListener(this);
//		calcium.addActionListener(this);
		
		contentPanel.add(buttonPanel);
		
	}
	public void update() {
		// Updates the parameter panel
		HashMap<String,String> params = experimentalSettings.getRSOptions();
		String newContent = "";
		for(String key: params.keySet()) {
			newContent += key + " " + params.get(key) + "\n";
		}
		oArea.setText(newContent);
		
	}
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == editButton) {
			rsScreen = new OptionsFrame("Reference State Options","SettingFiles/RSOptions.csv",this.experimentalSettings.getRSOptions(),this,"");
			rsScreen.makeVisible();
		}
	}
}
