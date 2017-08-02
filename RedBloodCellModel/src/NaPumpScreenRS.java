import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class NaPumpScreenRS extends JFrame implements ActionListener {
	private RBCGui rbcgui;
	private HashMap<String,String> options;
	private RBC_model rbc;
	
	private JTextField na_eflux_fwd,na_eflux_rev;
	private HashMap<JTextField,String> fieldMap;
	public NaPumpScreenRS(RBCGui r,HashMap<String,String> options, RBC_model rbc) {
		super("Na Pump Screen");
		this.options = options;
		this.rbcgui = r;
		this.rbc = rbc;
		fieldMap = new HashMap<JTextField,String>();
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());
		
		NaPump napump = this.rbc.getNapump();
		
		panel.add(new JLabel("Na eflux fwd (" + napump.getFluxFwd() + ")"));
		na_eflux_fwd = new JTextField("",10);
		panel.add(na_eflux_fwd);
		fieldMap.put(na_eflux_fwd, "na-eflux-fwd");
		
		panel.add(new JLabel("Na eflux rev (" + napump.getFluxRev() + ")"));
		na_eflux_rev = new JTextField("",10);
		panel.add(na_eflux_rev);
		fieldMap.put(na_eflux_rev, "na-eflux-rev");
		
		JButton nextButton = new JButton("Next");
		nextButton.addActionListener(this);
		panel.add(nextButton);
		this.add(panel);
		this.setSize(300, 300);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(false);
	}
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand() == "Next") {
			this.setVisible(false);
			for(JTextField t: fieldMap.keySet()) {
				if(t.getText().length() > 0) {
					options.put(fieldMap.get(t), t.getText());
				}
			}
			rbcgui.doneNaRS();
		}		
	}
	public void makeVisible() {
		this.setVisible(true);
	}

}
