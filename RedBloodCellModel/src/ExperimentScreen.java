import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ExperimentScreen extends JFrame implements ActionListener {
	private ExperimentalSettings experimentalSettings; 
	private JButton addButton,writeSettingsButton,runButton;
	private JPanel dSPanel;
	private HashMap<DSSettings,StagePanel> panelSettings;
	private CommentsPanel cp;
	private RBC_model rbc;
	public ExperimentScreen(ExperimentalSettings es) {
		this.rbc = new RBC_model();
		this.panelSettings = new HashMap<DSSettings,StagePanel>();
		this.experimentalSettings = es;
		this.setSize(1000,1000);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		JPanel mainPanel = new JPanel(new BorderLayout());
		
		JPanel statePanel = new JPanel(new GridLayout(0,2));
		
		dSPanel = new JPanel(new GridLayout(0,1));
		
		JPanel rsPanel = new JPanel(new GridLayout(0,1));
		cp = new CommentsPanel(this.experimentalSettings);
		cp.setBorder(BorderFactory.createTitledBorder("Overall Comments"));
		rsPanel.add(cp);
		
		RSPanel r = new RSPanel(this.experimentalSettings);
		r.setBorder(BorderFactory.createTitledBorder("Reference State"));
		
		rsPanel.add(r);
		
		statePanel.add(rsPanel);
		statePanel.add(new JScrollPane(dSPanel));
		mainPanel.add(statePanel,BorderLayout.CENTER);
		this.populateDSPanel();
		this.add(mainPanel);
		
		
//		mainPanel.add(referenceStatePanel,BorderLayout.NORTH);
		
		
		JPanel buttonPanel = new JPanel(new GridLayout(0,3));
		addButton = new JButton("Add DS stage");
		addButton.addActionListener(this);
		buttonPanel.add(addButton);
		
		writeSettingsButton = new JButton("Write Settings file");
		writeSettingsButton.addActionListener(this);
		buttonPanel.add(writeSettingsButton);
		
		runButton = new JButton("Run Model");
		runButton.addActionListener(this);
		buttonPanel.add(runButton);
		mainPanel.add(buttonPanel,BorderLayout.SOUTH);
		
		this.setVisible(true);
	}
	public void deleteStage(DSSettings ds) {
		this.experimentalSettings.remove(ds);
		this.clearDSPanel();
		this.populateDSPanel();
	}
	public void clearDSPanel() {
		for(DSSettings d: this.panelSettings.keySet()) {
			this.dSPanel.remove(this.panelSettings.get(d));
		}
	}
	public void populateDSPanel() {
		int dSStage = 1;
		for(DSSettings d: this.experimentalSettings.getDSStages()) {
			StagePanel temp = new StagePanel(d,this);
			String title = "Dynamic Stage: " + dSStage++;
			temp.setBorder(BorderFactory.createTitledBorder(title));
			dSPanel.add(temp);
			panelSettings.put(d, temp);
		}
		if(this.experimentalSettings.getNStages() == 1) {
			DSSettings onlySettings = this.experimentalSettings.getDSStages().get(0);
			StagePanel onlyPanel = panelSettings.get(onlySettings);
			onlyPanel.disableDelete();
		}
		this.revalidate();
	}
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == addButton) {
			// add a new DS
			DSSettings newStage = new DSSettings();
			this.experimentalSettings.addStage(newStage);
			this.clearDSPanel();
			this.populateDSPanel();
		}else if(e.getSource() == writeSettingsButton) {
			// Process the comments to ensure there is a # at the start of each line
			this.experimentalSettings.setOverallComments(this.cp.processComments());
			for(DSSettings d: panelSettings.keySet()) {
				panelSettings.get(d).processComment();
			}
			this.experimentalSettings.writeFile(this);
		}else if(e.getSource() == runButton) {
			new RunFrame(this.rbc,this.experimentalSettings,this);
		}
	}
}
