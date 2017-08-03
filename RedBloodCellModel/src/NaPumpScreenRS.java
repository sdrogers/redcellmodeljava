import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class NaPumpScreenRS extends MenuFrame {
	
	private JTextField na_eflux_fwd,na_eflux_rev;
	public NaPumpScreenRS(RBCGui r,HashMap<String,String> options, RBC_model rbc) {
		super(rbc,r,options,"Na Pump Screen");
		
		
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
		panel.add(new JLabel());
		panel.add(nextButton);
		
	}
	public void grabOptions() {
		for(JTextField t: fieldMap.keySet()) {
			if(t.getText().length() > 0) {
				options.put(fieldMap.get(t), t.getText());
			}
		}
	}
	
	public void makeVisible() {
		this.setVisible(true);
	}

}
