package guicomponents;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import utilities.Updateable;

public class OptionsFrame extends JFrame implements ActionListener {
	private ParameterSelector ps;
	private JButton doneButton;
	HashMap<String,String> options;
	private Updateable parent;
	
	public OptionsFrame(String title,String optionsFileName,HashMap<String,String> options,Updateable parent,String helpText) {
		this.parent = parent;
		this.options = options;
		this.setTitle(title);
		this.setSize(800, 500);
		ps = new ParameterSelector(optionsFileName,this.options,this.parent);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		JPanel mainPanel = new JPanel(new GridBagLayout());
		this.add(mainPanel,BorderLayout.CENTER);
		
		JPanel topPanel = new JPanel();
		JTextArea helpArea = new JTextArea(5,40);
		helpArea.setText(helpText);
		helpArea.setLineWrap(true);
		helpArea.setWrapStyleWord(true);
		helpArea.setEditable(false);
		helpArea.setBackground(this.getBackground()); 
		topPanel.add(helpArea);
		this.add(topPanel,BorderLayout.NORTH);
		
		GridBagConstraints c = new GridBagConstraints();
		
		doneButton = new JButton("Done");
		doneButton.addActionListener(this);
		
		c.gridx=0;
		c.gridy=0;
		mainPanel.add(ps);
		c.gridx=0;
		c.gridy=1;
		mainPanel.add(doneButton,c);
		
//		this.setVisible(true);

	}
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == doneButton) {
//			System.out.println("Pressed done");
			this.setVisible(false);
//			this.grabOptions();
//			this.parent.updateStagePanel();
//			parent.setVisible(true);
//			this.rbcgui.doneMenu(this);
		}
	}
//	private void grabOptions() {
//		// gets the options from the parameter selector
//		ps.grabOptions();
//	}
	public void makeVisible() {
		this.setVisible(true);
	}
}
