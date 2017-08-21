import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class TestGui extends JFrame implements ActionListener {
	private JButton loadButton,saveButton,runButton;
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
		panel = new JPanel(new GridLayout(0,1));
		loadButton = new JButton("Load");
		loadButton.addActionListener(this);
		saveButton = new JButton("Save");
		saveButton.addActionListener(this);
		saveButton.setEnabled(false);
		runButton = new JButton("Run");
		runButton.addActionListener(this);
		runButton.setEnabled(false);
		panel.add(loadButton);
		panel.add(runButton);
		panel.add(saveButton);
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
			System.out.println("Load!");
			int returnVal = jfc.showOpenDialog(this);
			options = new HashMap<String,String>();
			options = LoadProtocol.loadOptions(jfc.getSelectedFile().getPath());
			runButton.setEnabled(true);
		}else if(e.getActionCommand() == "Run") {
			ArrayList<String> usedoptions = new ArrayList<String>();
			rbc = new RBC_model();
			rbc.setup(options, usedoptions);
			textArea.append("\n\nOptions:");
			for(String o : options.keySet()) {
				textArea.append("\n" + o + ": " + options.get(o));
			}
			rbc.setupDS(options, usedoptions);
			if(usedoptions.size() < options.size()) {
				textArea.append("\nWARNING: UNUSED OPTIONS...");
				for(String a: options.keySet()) {
					if(!usedoptions.contains(a)) {
						textArea.append("\n"+"a");
					}
				}
			}
			rbc.runall();
			saveButton.setEnabled(true);
			textArea.append("\n\nRunning...");
		}else if(e.getActionCommand()== "Save") {
			System.out.println("Save");
			int returnVal = jfc.showSaveDialog(this);
			rbc.writeCsv(jfc.getSelectedFile().getPath());
		}
	}
	public static void main(String[] args) {
		new TestGui();
	}

}
