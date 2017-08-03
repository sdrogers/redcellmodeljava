import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public abstract class MenuFrame extends JFrame implements ActionListener {
	protected RBC_model rbc;
	protected HashMap<String,String> options;
	protected RBCGui rbcgui;
	protected JPanel panel;
	protected HashMap<JTextField,String> fieldMap;

	public MenuFrame(RBC_model rbc, RBCGui rbcgui, HashMap<String,String> options, String title) {
		super(title);
		this.rbc = rbc;
		this.options = options;
		this.rbcgui = rbcgui;
		
		this.fieldMap = new HashMap<JTextField,String>();
		
		panel = new JPanel(new GridLayout(0,2));
		
		
		
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
			this.setVisible(false);
			this.grabOptions();
			this.rbcgui.doneMenu(this);
		}
	}
	public abstract void grabOptions();
	
	
	

}
