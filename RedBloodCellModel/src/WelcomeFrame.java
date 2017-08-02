import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class WelcomeFrame extends JFrame implements ActionListener{
	private RBCGui parent;
	public WelcomeFrame(String title,RBCGui parent) {
		super(title);
		this.parent = parent;
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());
		
		JButton startButton = new JButton("Start");
		startButton.addActionListener(this);
		panel.add(startButton);
		this.add(panel);
		this.setSize(300, 300);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
	}
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand() == "Start") {
			this.setVisible(false);
			this.parent.doneWelcome();
		}
	}
	
}
