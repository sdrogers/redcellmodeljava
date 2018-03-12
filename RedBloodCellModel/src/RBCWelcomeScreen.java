import java.awt.BorderLayout;
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

public class RBCWelcomeScreen extends JFrame implements ActionListener {
	private JButton newButton,loadButton;
	private JFileChooser jfc;
	public RBCWelcomeScreen() {
		this.setSize(500,500);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle("RBC Model, GUI V2.0");
		
		newButton = new JButton("New experiment");
		loadButton = new JButton("Load from file");
		
		newButton.addActionListener(this);
		loadButton.addActionListener(this);
		
		JTextArea infoArea = new JTextArea(10,20);
		infoArea.setEditable(false);
		infoArea.append("Welcome to the RBC model\n");
		infoArea.append("========================\n\n");
		infoArea.append("Some helpful words will appear here in due course");
		this.add(infoArea, BorderLayout.CENTER);
		
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
			
		}
	}
	
	public static void main(String[] args) {
		new RBCWelcomeScreen();
	}
}
