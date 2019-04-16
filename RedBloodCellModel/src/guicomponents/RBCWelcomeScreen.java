package guicomponents;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.BevelBorder;

import utilities.ExampleProtocols;
import utilities.ExperimentalSettings;

public class RBCWelcomeScreen extends JFrame implements ActionListener {
	private JButton newButton,loadButton;
	private JFileChooser jfc;
	private JButton pkgButton,protocolAButton;
	public RBCWelcomeScreen() {
		this.setSize(500,450);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle("RBC Model, GUI V2.0");
		
		newButton = new JButton("New experiment");
		loadButton = new JButton("Load from file");
		
		newButton.addActionListener(this);
		loadButton.addActionListener(this);
		
		JPanel centerPanel = new JPanel(new BorderLayout());

//		JPanel buffPanel = new JPanel(new BorderLayout());
//		JTextArea infoArea = new JTextArea(10,20);
//		infoArea.setEditable(false);
//		infoArea.append("Welcome to the RBC model\n");
//		infoArea.append("========================\n\n");
//		infoArea.append("Some helpful words will appear here in due course");
//		buffPanel.add(infoArea,BorderLayout.NORTH);
//		buffPanel.add(new ImagePanel(),BorderLayout.CENTER);
//		buffPanel.setBackground(Color.WHITE);
		centerPanel.add(new ImagePanel(),BorderLayout.CENTER);
		

		this.add(centerPanel);
		
		
		JPanel buttonPanel = new JPanel(new GridLayout(0,1));
		
		JPanel demoProtocols = new JPanel(new GridLayout(0,1));
		pkgButton = new JButton("PKG 30, PAG 50");
		pkgButton.addActionListener(this);
		demoProtocols.add(pkgButton);
		
		protocolAButton = new JButton("Protocol A");
		protocolAButton.addActionListener(this);
		demoProtocols.add(protocolAButton);
		
		demoProtocols.setBorder(BorderFactory.createTitledBorder("Demo protocols"));
		JPanel bottomButtons = new JPanel(new FlowLayout());
		bottomButtons.add(newButton);
		bottomButtons.add(loadButton);
		bottomButtons.setBorder(BorderFactory.createTitledBorder("Options"));
		buttonPanel.add(bottomButtons);
		
		buttonPanel.add(demoProtocols);
		
		centerPanel.add(buttonPanel,BorderLayout.SOUTH);
//		this.add(buttonPanel,BorderLayout.SOUTH);
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
		}else if(e.getSource() == protocolAButton) {
			new ExperimentScreen(ExampleProtocols.getA());
		}
	}
	
	public static void main(String[] args) {
		new RBCWelcomeScreen();
	}
}
