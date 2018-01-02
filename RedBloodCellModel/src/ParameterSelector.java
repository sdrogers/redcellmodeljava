import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.HashSet;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class ParameterSelector extends JPanel implements ListSelectionListener,ActionListener {
	private JLabel title;
	private JList<Parameter> potentialParams;
	private JList<Parameter> currentParamList;
	private DefaultListModel<Parameter> currentParams;
	private JButton removeButton;
	public ParameterSelector() {
//		this.setLayout(new BorderLayout());
		title = new JLabel("a title");
		this.add(title);
		
		// Load the parameters and default values
		loadParams();
		
		currentParams = new DefaultListModel<Parameter>();
		currentParamList = new JList<Parameter>();
		currentParamList.setModel(currentParams);
		currentParamList.setVisibleRowCount(10);
		currentParamList.setPrototypeCellValue(new Parameter("a longish parameter name","a large number",null));
		this.add(currentParamList);
		
		removeButton = new JButton("Remove");
		removeButton.addActionListener(this);
		this.add(removeButton);
		
		
	}
	private void loadParams() {
		Parameter[] possPars = new Parameter[5];
		String[] names = {"Fred","Ella","Matthew","Sarah","Simon"};
		for(int i=0;i<5;i++) {
			possPars[i] = new Parameter(names[i],""+(i*10.0), null);
		}
		potentialParams = new JList<Parameter>(possPars);
		potentialParams.addListSelectionListener(this);
		this.add(potentialParams);
	}
	@Override
	public void valueChanged(ListSelectionEvent e) {
		if(e.getValueIsAdjusting()) {
			Parameter selected = potentialParams.getSelectedValue();
			if(selected != null) {
				System.out.println(selected);
				updateCurrentParams(selected);
			}
		}
		potentialParams.clearSelection();
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == removeButton) {
			System.out.println("You clicked on remove");
			Parameter pr = currentParamList.getSelectedValue();
			if(pr != null) {
				currentParams.removeElement(pr);
			}
		}
	}
	private void updateCurrentParams(Parameter selected) {
		// Does a parameter of this name exist?
		
		String inputValue = JOptionPane.showInputDialog("Please input a value for "+selected.getName()).trim();
		if(checkInput(inputValue,selected)) {
			boolean exists = false;
			int pos = 0;
			Parameter foundParameter = null;
			for(Object p: currentParams.toArray()) {
				Parameter pa = (Parameter) p;
				if(selected.getName().equals(pa.getName())) {
					foundParameter = pa;
					exists = true;
					break;
				}
				pos ++;
			}
			if(exists) {
				currentParams.removeElement(foundParameter);			
			}
			Parameter newpar = new Parameter(selected.getName(),inputValue,null);
			currentParams.addElement(newpar);
		}else {
			// Input error
			JOptionPane.showMessageDialog(null, "Incorrect input for " + selected.getName());
		}
	}
	private boolean checkInput(String newValue,Parameter p) {
		if(p.getAllowedValues() == null) {
			// Just has to be parsable as a double
			try {
				Double.parseDouble(newValue);
				return true;
			}catch(NumberFormatException e) {
				return false;
			}
		}else {
			if(p.getAllowedValues().contains(newValue)) {
				return true;
			}else
				return false;
		}
	}
}
