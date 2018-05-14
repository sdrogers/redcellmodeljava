package guicomponents;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import utilities.DSSettings;
import utilities.ExperimentalSettings;

public class ExperimentScreen extends JFrame implements ActionListener {
	private ExperimentalSettings experimentalSettings; 
	private JButton addButton,writeSettingsButton,runButton,closeButton;
	private JPanel dSPanel;
	private HashMap<DSSettings,StagePanel> panelSettings;
	private CommentsPanel cp;
	private RSPanel r;
	public ExperimentScreen(ExperimentalSettings es) {
		this.panelSettings = new HashMap<DSSettings,StagePanel>();
		this.experimentalSettings = es;
		this.setSize(1500,1000);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		JPanel mainPanel = new JPanel(new BorderLayout());
		
		JPanel statePanel = new JPanel(new GridLayout(0,2));
		
		dSPanel = new JPanel(new GridLayout(0,1));
		
		JPanel rsPanel = new JPanel(new GridLayout(0,1));
		cp = new CommentsPanel(this.experimentalSettings);
		cp.setBorder(BorderFactory.createTitledBorder("Overall Comments"));
		
		
		r = new RSPanel(this.experimentalSettings);
		r.setBorder(BorderFactory.createTitledBorder("Reference State"));
		
		rsPanel.add(r);
		rsPanel.add(cp);
		
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
		
		writeSettingsButton = new JButton("Write Protocol file");
		writeSettingsButton.addActionListener(this);
		buttonPanel.add(writeSettingsButton);
		
		closeButton = new JButton("Close Experiment");
		closeButton.addActionListener(this);
		buttonPanel.add(closeButton);
		
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
			this.r.processComment();
			this.experimentalSettings.writeFile(this);
		}else if(e.getSource() == runButton) {
			if(checkOptions()) {
				new RunFrame(this.experimentalSettings,this);
			}else {
				// error!
			}
		}else if(e.getSource() == closeButton) {
			// reset the experiment
			this.setVisible(false);
			this.dispose();
		}
	}
	private boolean checkOptions() {
		int pos = 1;
		for(DSSettings d: this.experimentalSettings.getDSStages()) {
			if(!d.getOptions().containsKey("time")) {
				JOptionPane.showMessageDialog(this, "Please enter time for stage "+pos);
				return false;
			}
			pos += 1;
		}
		return true;
	}
}
