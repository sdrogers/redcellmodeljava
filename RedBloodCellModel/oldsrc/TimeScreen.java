import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class TimeScreen extends MenuFrame implements ActionListener{
	private JTextField experimentTime;
	private JButton doneButton;
	public TimeScreen(RBCGui rbcgui, HashMap<String, String> options,RBC_model rbc) {
		super(rbc, rbcgui, options, "Time options");
		// TODO Auto-generated constructor stub
		experimentTime = new JTextField();
		panel.add(new JLabel("Experiment time (minutes)"));
		panel.add(experimentTime);
		fieldMap.put(experimentTime, "time");
		
		doneButton = new JButton("Done");
		doneButton.addActionListener(this);
		panel.add(doneButton);
	}
	@Override
	public void grabOptions() {
		// TODO Auto-generated method stub
		for(JTextField tf: fieldMap.keySet()) {
			options.put(fieldMap.get(tf),tf.getText());
		}
		
	}
	
}
