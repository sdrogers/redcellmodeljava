import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Insets;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import com.opencsv.CSVReader;

public class ParameterSelector extends JPanel implements ListSelectionListener,ActionListener {
	private JLabel title;
	private JList<Parameter> potentialParamList;
	private DefaultListModel<Parameter> potentialParams;
	private JList<Parameter> currentParamList;
	private DefaultListModel<Parameter> currentParams;
	private JButton removeButton,addButton;
	private final String fileName;
	private JTextField descriptionField;
	HashMap<String,String> options;
	public ParameterSelector(String fileName,HashMap<String,String> options) {
		this.options = options;
		this.fileName = fileName;
		this.setLayout(new BorderLayout());
		title = new JLabel("a title");
		this.add(title);
		
		JPanel centerPanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		JPanel bottomPanel = new JPanel();
		JPanel topPanel = new JPanel();
		
		this.add(topPanel, BorderLayout.NORTH);
		potentialParams = new DefaultListModel<Parameter>();
		potentialParamList = new JList<Parameter>();
		potentialParamList.setModel(potentialParams);
		potentialParamList.addListSelectionListener(this);
		potentialParamList.setBorder(BorderFactory.createTitledBorder("Available parameters"));
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(5,5,5,5);		
		centerPanel.add(new JScrollPane(potentialParamList),c);
		addButton = new JButton("Add new option value");
		addButton.addActionListener(this);
		bottomPanel.add(addButton);
		this.add(bottomPanel,BorderLayout.SOUTH);
		//Load the parameters and default values
		loadSettingsFile(); // populates potentialParams
		currentParams = new DefaultListModel<Parameter>();
		currentParamList = new JList<Parameter>();
		currentParamList.setModel(currentParams);
		currentParamList.setVisibleRowCount(10);
		currentParamList.setPrototypeCellValue(
				new Parameter(
						"a longish longer name",
						"10.0",
						"some",
						"a description",
						null));
		c.gridx = 1;
		c.gridy = 0;
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		currentParamList.setBorder(BorderFactory.createTitledBorder("Changed parameters"));
		centerPanel.add(new JScrollPane(currentParamList),c);
		this.add(centerPanel, BorderLayout.CENTER);
		removeButton = new JButton("Remove");
		removeButton.addActionListener(this);
		bottomPanel.add(removeButton);
		
		descriptionField = new JTextField(30);
		descriptionField.setEditable(false);
		topPanel.add(descriptionField);
		
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		if(e.getValueIsAdjusting()) {
			Parameter selected = potentialParamList.getSelectedValue();
			if(selected != null) {
				// Set the description text
				descriptionField.setText(selected.getDescription());
			}
		}
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == removeButton) {
			System.out.println("You clicked on remove");
			Parameter pr = currentParamList.getSelectedValue();
			if(pr != null) {
				currentParams.removeElement(pr);
			}
		}else if(e.getSource() == addButton) {
			Parameter selected = potentialParamList.getSelectedValue();
			if(selected != null) {
				updateCurrentParams(selected);
				potentialParamList.clearSelection();
				descriptionField.setText("");
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
			Parameter newpar = new Parameter(selected.getName(),inputValue,selected.getUnits(),selected.getDescription(),null);
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
	private void loadSettingsFile() {
		System.out.println(this.fileName);
		CSVReader reader = null;
		HashMap<String,Parameter> keys = new HashMap<String,Parameter>();
		try {
			InputStream is = ClassLoader.getSystemResourceAsStream(this.fileName);
//			System.out.println(ClassLoader.getSystemResourceAsStream(this.fileName));
//			System.out.println(is);
			InputStreamReader isr = new InputStreamReader(is);
			reader = new CSVReader(isr);
//			reader = new CSVReader(new FileReader(this.fileName));
			String[] line;
			while ((line = reader.readNext()) != null ) {
				String parameter_name = line[0].trim();
				String parameter_value = line[1].trim();
				String parameter_units = line[2].trim();
				String parameter_description = line[3].trim();
				HashSet<String> allowedValues = null;
				if(line[4].length()>0) {
					String[] tokens = line[4].split(",");
					allowedValues = new HashSet<String>();
					for(String t: tokens) {
						allowedValues.add(t);
					}
				}
				Parameter pr = new Parameter(parameter_name,parameter_value,parameter_units,parameter_description,allowedValues);
				System.out.println(pr);
				potentialParams.addElement(pr);
				keys.put(pr.getName(),pr);
			}
		}catch(FileNotFoundException e) {
			e.printStackTrace();
		}catch(IOException e) {
			e.printStackTrace();
		}
		for(String s: this.options.keySet()) {
			if(keys.containsKey(s)) {
				Parameter pr = keys.get(s);
				Parameter newPr = new Parameter(pr.getName(),this.options.get(s),pr.getUnits(),pr.getDescription(),pr.getAllowedValues());
				currentParams.addElement(newPr);
			}
		}
	}
	public void grabOptions() {
		for(Object p: currentParams.toArray()) {
			Parameter pr = (Parameter) p;
			String parameterName = pr.getName();
			String parameterValue = pr.getValue();
			options.put(parameterName,parameterValue);
		}
	}
}
