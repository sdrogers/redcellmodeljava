package guicomponents;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import utilities.ExampleProtocols;
import utilities.ExperimentalSettings;

public class RBCWelcomeScreen extends JFrame implements ActionListener {
	private JButton newButton,loadButton;
	private JFileChooser jfc;
	private JButton pkgButton;
	public RBCWelcomeScreen() {
		this.setSize(500,600);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle("RBC Model, GUI V2.0");
		
		newButton = new JButton("New experiment");
		loadButton = new JButton("Load from file");
		
		newButton.addActionListener(this);
		loadButton.addActionListener(this);
		
		JPanel centerPanel = new JPanel(new BorderLayout());

		JPanel buffPanel = new JPanel(new BorderLayout());
		JTextArea infoArea = new JTextArea(10,20);
		infoArea.setEditable(false);
		infoArea.append("Welcome to the RBC model\n");
		infoArea.append("========================\n\n");
		infoArea.append("Some helpful words will appear here in due course");
		buffPanel.add(infoArea,BorderLayout.NORTH);
		buffPanel.add(new ImagePanel(),BorderLayout.CENTER);
		buffPanel.setBackground(Color.WHITE);
		centerPanel.add(buffPanel,BorderLayout.CENTER);
		
		pkgButton = new JButton("PKG 30, PAG 50");
		pkgButton.addActionListener(this);
		centerPanel.add(pkgButton,BorderLayout.SOUTH);
		this.add(centerPanel, BorderLayout.CENTER);
		
		JPanel buttonPanel = new JPanel(new FlowLayout());
		
		buttonPanel.add(newButton);
		buttonPanel.add(loadButton);
		
		this.add(buttonPanel,BorderLayout.SOUTH);
		this.setVisible(true);
		
		jfc = new JFileChooser();
	}
	
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == newButton) {
			// create an experimentScreen with some empty settings
			new ExperimentScreen(new ExperimentalSettings());
		}else if(e.getSource() == loadButton) {
			// Load the options from a file
			File workingDirectory = new File(System.getProperty("user.dir"));
			jfc.setCurrentDirectory(workingDirectory);
			jfc.showOpenDialog(this);
			String fileName = jfc.getSelectedFile().getPath();
			new ExperimentScreen(new ExperimentalSettings(fileName));
		}else if(e.getSource() == pkgButton) {
			new ExperimentScreen(ExampleProtocols.getPKG());
		}
	}
	
	public static void main(String[] args) {
		new RBCWelcomeScreen();
	}
}
