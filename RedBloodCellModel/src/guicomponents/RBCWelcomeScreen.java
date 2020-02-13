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
	private JButton pkgButton,protocolAButton,protocolBButton,protocolCButton,protocolDButton,protocolEButton,protocolFButton,protocolGButton;
	private JPanel buttonPanel;
	public RBCWelcomeScreen() {
		this.setSize(500,450);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle("RBC Model, GUI V2.0");
		
		newButton = new JButton("New experiment");
		loadButton = new JButton("Load from file");
		
		newButton.addActionListener(this);
		loadButton.addActionListener(this);
		
		JPanel centerPanel = new JPanel(new BorderLayout());

		centerPanel.add(new ImagePanel(),BorderLayout.CENTER);
		

		this.add(centerPanel);
		
		
		buttonPanel = new JPanel(new GridLayout(0,1));
		
		
		
		JPanel bottomButtons = new JPanel(new FlowLayout());
		bottomButtons.add(newButton);
		bottomButtons.add(loadButton);
		bottomButtons.setBorder(BorderFactory.createTitledBorder("Options"));
		buttonPanel.add(bottomButtons);
		//addDemoProtocols();

		
		centerPanel.add(buttonPanel,BorderLayout.SOUTH);

		this.setVisible(true);
		
		jfc = new JFileChooser();
	}
	
	
	private void addDemoProtocols() {
		JPanel demoProtocols = new JPanel(new GridLayout(0,1));
		pkgButton = new JButton("PKG 30, PAG 50");
		pkgButton.addActionListener(this);
		demoProtocols.add(pkgButton);
		
		protocolAButton = new JButton("Protocol A");
		protocolAButton.addActionListener(this);
		demoProtocols.add(protocolAButton);

//		protocolBButton = new JButton("Protocol B");
//		protocolBButton.addActionListener(this);
//		demoProtocols.add(protocolBButton);
//
//
//		protocolCButton = new JButton("Protocol C");
//		protocolCButton.addActionListener(this);
//		demoProtocols.add(protocolCButton);
//
//		protocolDButton = new JButton("Protocol D");
//		protocolDButton.addActionListener(this);
//		demoProtocols.add(protocolDButton);
//
//		
//		protocolEButton = new JButton("Protocol E");
//		protocolEButton.addActionListener(this);
//		demoProtocols.add(protocolEButton);
//
//		
//		protocolFButton = new JButton("Protocol F");
//		protocolFButton.addActionListener(this);
//		demoProtocols.add(protocolFButton);
//
//		
//		protocolGButton = new JButton("Protocol G");
//		protocolGButton.addActionListener(this);
//		demoProtocols.add(protocolGButton);

		
		
		demoProtocols.setBorder(BorderFactory.createTitledBorder("Demo protocols"));
		
		
		buttonPanel.add(demoProtocols);
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
