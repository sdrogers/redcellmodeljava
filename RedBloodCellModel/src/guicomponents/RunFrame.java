package guicomponents;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;

import modelcomponents.RBC_model;
import utilities.DSSettings;
import utilities.ExperimentalSettings;

// Class that represents the screen we see when we run
// also responsible for instantiating the actual model and running it

public class RunFrame extends JFrame implements ActionListener{
	private RBC_model rbc;
	private JFrame source;
	private HashMap<String,String> options;
	private JTextArea ta;
	private JButton saveButton,returnButton,quitButton;
	private JFileChooser jfc = new JFileChooser();
	private ExperimentalSettings experimentalSettings;
	public RunFrame(ExperimentalSettings es,JFrame source) {
		this.experimentalSettings = es;
		File workingDirectory = new File(System.getProperty("user.dir"));
		jfc.setCurrentDirectory(workingDirectory);

		this.rbc = rbc;
		this.source = source;
//		this.options = options;
		this.setTitle("Run the model");
		this.setSize(600, 600);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		JPanel layoutPanel = new JPanel(new BorderLayout());
		this.add(layoutPanel);
		ta = new JTextArea();
		layoutPanel.add(new JScrollPane(ta),BorderLayout.CENTER);
		saveButton = new JButton("Save output");
		JPanel bottomPanel = new JPanel();
		bottomPanel.add(saveButton);
		layoutPanel.add(bottomPanel,BorderLayout.SOUTH);
		saveButton.addActionListener(this);
		saveButton.setEnabled(false);
		returnButton = new JButton("Close Window");
		returnButton.addActionListener(this);
		bottomPanel.add(returnButton);
		
		this.setVisible(true);
		
		this.runModel();
		
	}
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == saveButton) {
			// save the output
			int returnVal = jfc.showSaveDialog(this);
			String fName = jfc.getSelectedFile().getPath();
			if(!fName.endsWith(".csv")) {
				fName += ".csv";
			}
			rbc.writeCsv(fName);
		}else if(e.getSource() == returnButton) {
			this.setVisible(false);
			this.dispose();
		}
	}
	public void runModel() {
		ArrayList<String> usedoptions = new ArrayList<String>();
		//
		rbc = new RBC_model();
		rbc.setup(experimentalSettings.getRSOptions(), usedoptions);
		for(DSSettings d: experimentalSettings.getDSStages()) {
			rbc.setupDS(d.getOptions(), usedoptions);
			rbc.runall(ta);
		}
		saveButton.setEnabled(true);
	}

}
