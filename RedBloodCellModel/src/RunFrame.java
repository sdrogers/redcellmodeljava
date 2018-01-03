import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class RunFrame extends JFrame implements ActionListener{
	private RBC_model rbc;
	private JFrame source;
	private HashMap<String,String> options;
	private JTextArea ta;
	private JButton saveButton,returnButton,quitButton;
	private JFileChooser jfc = new JFileChooser();
	public RunFrame(RBC_model rbc,HashMap<String,String> options,JFrame source) {
		File workingDirectory = new File(System.getProperty("user.dir"));
		jfc.setCurrentDirectory(workingDirectory);

		this.rbc = rbc;
		this.source = source;
		this.options = options;
		this.setTitle("Run the model");
		this.setSize(600, 600);
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
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
		returnButton = new JButton("Add additional stage");
		quitButton = new JButton("Exit");
		returnButton.addActionListener(this);
		quitButton.addActionListener(this);
		bottomPanel.add(returnButton);
		bottomPanel.add(quitButton);
		
	}
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == saveButton) {
			// save the output
			int returnVal = jfc.showSaveDialog(this);
			rbc.writeCsv(jfc.getSelectedFile().getPath());
		}else if(e.getSource() == quitButton) {
			System.exit(0);
		}else if(e.getSource() == returnButton) {
			this.setVisible(false);
			this.source.setVisible(true);
		}
	}
	public void runModel() {
		ArrayList<String> usedoptions = new ArrayList<String>();
		rbc.setup(options, usedoptions);
		rbc.setupDS(options, usedoptions);
		LoadProtocol.printOptions(options);
		rbc.runall(ta);
		saveButton.setEnabled(true);
	}

}
