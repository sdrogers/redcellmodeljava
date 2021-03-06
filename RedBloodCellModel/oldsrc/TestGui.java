import java.awt.GridLayout;
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

public class TestGui extends JFrame implements ActionListener {
	private JButton loadButton,saveButton,runButton,resetButton;
	private JPanel panel;
	private RBC_model rbc;
	private String options_file;
	private String output_file;
	private HashMap<String,String> options;
	private JFileChooser jfc = new JFileChooser();
	private JTextArea textArea;
	private JScrollPane scrollPane;
	public TestGui() {
		super("RBC Simple GUI");
		options = new HashMap<String,String>();
		rbc = new RBC_model();
		File workingDirectory = new File(System.getProperty("user.dir"));
		jfc.setCurrentDirectory(workingDirectory);
		panel = new JPanel(new GridLayout(0,1));
		loadButton = new JButton("Load");
		loadButton.addActionListener(this);
		saveButton = new JButton("Save");
		saveButton.addActionListener(this);
		saveButton.setEnabled(false);
		runButton = new JButton("Run");
		runButton.addActionListener(this);
		runButton.setEnabled(false);
		resetButton = new JButton("Reset");
		resetButton.addActionListener(this);
		resetButton.setEnabled(false);
		panel.add(loadButton);
		panel.add(runButton);
		panel.add(saveButton);
		panel.add(resetButton);
		textArea = new JTextArea();
		scrollPane = new JScrollPane(textArea);
		panel.add(scrollPane);
		this.add(panel);
		this.setSize(300, 300);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        
	}
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand() == "Load") {
			int returnVal = jfc.showOpenDialog(this);
//			options = new HashMap<String,String>();
			options = LoadProtocol.loadOptions(jfc.getSelectedFile().getPath(),options);
			runButton.setEnabled(true);
			saveButton.setEnabled(false);
		}else if(e.getActionCommand() == "Run") {
			ArrayList<String> usedoptions = new ArrayList<String>();
//			rbc = new RBC_model();
			textArea.append("\n\nOptions:");
			for(String o : options.keySet()) {
				textArea.append("\n" + o + ": " + options.get(o));
			}

			if(rbc.getStage() == 0) {
				textArea.append("\n\nSetting up RS");
				rbc.setup(options, usedoptions);
			}
			rbc.setupDS(options, usedoptions);
			textArea.append("\n\nSetting up DS for stage " + rbc.getStage());
			if(usedoptions.size() < options.size()) {
				textArea.append("\nWARNING: UNUSED OPTIONS...");
				for(String a: options.keySet()) {
					if(!usedoptions.contains(a)) {
						textArea.append("\n"+a);
					}
				}
			}
			rbc.runall();
			saveButton.setEnabled(true);
			textArea.append("\n\nRunning...");
			resetButton.setEnabled(true);
		}else if(e.getActionCommand()== "Save") {
			System.out.println("Save");
			int returnVal = jfc.showSaveDialog(this);
			rbc.writeCsv(jfc.getSelectedFile().getPath());
		}else if(e.getActionCommand() == "Reset") {
			rbc = new RBC_model();
			options = new HashMap<String,String>();
			resetButton.setEnabled(false);
			runButton.setEnabled(false);
			saveButton.setEnabled(false);
		}
	}
	public static void main(String[] args) {
		new TestGui();
	}

}
