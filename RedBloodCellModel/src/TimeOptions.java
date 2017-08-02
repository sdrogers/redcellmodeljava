import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class TimeOptions extends JFrame implements ActionListener {
	JTextField timeField;
	private HashMap<String,String> options;
	private RBCGui rbcgui;
	public TimeOptions(RBCGui parent,HashMap<String,String> options) {
		super("Time Options");
		
		this.rbcgui = parent;
		this.options = options;
		
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());
		
		JLabel timeLabel = new JLabel("Experiment time:");
		timeField = new JTextField("30",10);
		panel.add(timeLabel);
		panel.add(timeField);
		JButton nextButton = new JButton("Next");
		nextButton.addActionListener(this);
		panel.add(nextButton);
		this.add(panel);
		this.setSize(300, 300);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(false);
	}
	public void makeVisible() {
		this.setVisible(true);
	}
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand() == "Next") {
			// store the values in options
			this.options.put("time", this.timeField.getText());
			this.setVisible(false);
			this.rbcgui.doneTime();
		}

		
	}

}
