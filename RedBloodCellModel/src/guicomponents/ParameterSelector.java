package guicomponents;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import com.opencsv.CSVReader;

import utilities.Parameter;
import utilities.Updateable;

public class ParameterSelector extends JPanel implements ListSelectionListener,ActionListener {
	private JLabel title;
	private JList<Parameter> potentialParamList;
	private DefaultListModel<Parameter> potentialParams;
	private JList<Parameter> currentParamList;
	private DefaultListModel<Parameter> currentParams;
	private JButton removeButton,addButton;
	private final String fileName;
	private JTextArea descriptionField;
	HashMap<String,String> options;
	private Updateable parentComp;
	public ParameterSelector(String fileName,HashMap<String,String> options,Updateable parent) {
		this.options = options;
		this.fileName = fileName;
		this.parentComp = parent;
		this.setLayout(new BorderLayout());
		title = new JLabel("Change parameters");
		this.add(title);
		
		JPanel centerPanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		JPanel bottomPanel = new JPanel();
		JPanel topPanel = new JPanel();
//		topPanel.add(new JTextArea(helpText));
		this.add(topPanel, BorderLayout.NORTH);
		potentialParams = new DefaultListModel<Parameter>();
		potentialParamList = new JList<Parameter>();
		potentialParamList.setModel(potentialParams);
		potentialParamList.addListSelectionListener(this);
		potentialParamList.setBorder(BorderFactory.createTitledBorder("Default values"));
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(5,5,5,5);		
		centerPanel.add(new JScrollPane(potentialParamList),c);
		addButton = new JButton("Modify value");
		addButton.addActionListener(this);
		bottomPanel.add(addButton);
		this.add(bottomPanel,BorderLayout.SOUTH);
		//Load the parameters and default values
		
		currentParams = new DefaultListModel<Parameter>();
		currentParamList = new JList<Parameter>();
		currentParamList.setModel(currentParams);
		currentParamList.setVisibleRowCount(8);
		currentParamList.setPrototypeCellValue(
				new Parameter(
						"a longish longer name",
						"10.0",
						"some",
						"a description",
						null,
						"a much longer display name..please fit"));
		c.gridx = 1;
		c.gridy = 0;
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		currentParamList.setBorder(BorderFactory.createTitledBorder("Modified values"));
		centerPanel.add(new JScrollPane(currentParamList),c);
		this.add(centerPanel, BorderLayout.CENTER);
		removeButton = new JButton("Remove");
		removeButton.addActionListener(this);
		bottomPanel.add(removeButton);
		
		descriptionField = new JTextArea();
		descriptionField.setText("Click on an entry to see a description");
		descriptionField.setEditable(false);
		descriptionField.setBackground(topPanel.getBackground());
		topPanel.add(descriptionField);
		
		loadSettingsFile(); // populates potentialParams
		
		
		potentialParamList.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				if(evt.getClickCount() == 2) {
					Parameter selected = potentialParamList.getSelectedValue();
					if(selected != null) {
						updateCurrentParams(selected);
						potentialParamList.clearSelection();
						descriptionField.setText("");
						// Tell the parent that it has updated
						
					}
				}
			}
		});
		
		currentParamList.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				if(evt.getClickCount() == 2) {
					Parameter pr = currentParamList.getSelectedValue();
					if(pr != null) {
						currentParams.removeElement(pr);
						options.remove(pr.getName());
						if(pr.getName().equals("piezo")) {
							removePiezo();
						}
						parentComp.update();
					}
				}
			}
		});
		
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
				if(pr.getName().equals("piezo")) {
					removePiezo();
				}
				currentParams.removeElement(pr);
				options.remove(pr.getName());
				this.parentComp.update();
			}
		}else if(e.getSource() == addButton) {
			Parameter selected = potentialParamList.getSelectedValue();
			if(selected != null) {
				updateCurrentParams(selected);
				potentialParamList.clearSelection();
				descriptionField.setText("");
				// Tell the parent that it has updated
				
			}
		}
	}
	private void updateCurrentParams(Parameter selected) {
		// Does a parameter of this name exist?
		String displayString = "Please input a value for "+selected.getDisplayName();
		if(selected.getAllowedValues() != null) {
			displayString += "  (permitted values = ";
			for(String v: selected.getAllowedValues()) {
				displayString += v + ", ";
			}
			displayString = displayString.substring(0,displayString.length()-2) + ")";
		}
		String inputValue = JOptionPane.showInputDialog(displayString).trim();
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
			Parameter newpar = new Parameter(selected.getName(),inputValue,selected.getUnits(),selected.getDescription(),null,selected.getDisplayName());
			currentParams.addElement(newpar);
			
			// A temporary hack (heard that before)
			// adds default piezo time and accuracy if piezo turned on
			if(newpar.getName().equals("piezo") && newpar.getValue().equals("yes")) {
				addPiezo();
			}
			this.grabOptions();
			this.parentComp.update();
		}else {
			// Input error
			JOptionPane.showMessageDialog(null, "Incorrect input for " + selected.getName());
		}
	}
	// Method to add all piezo once yes is clicked
	private void addPiezo() {
		Object[] allParams = potentialParams.toArray();
		for(Object o: allParams) {
			Parameter p = (Parameter) o;
			boolean found = false;
			for(Object chosenO: currentParams.toArray()) {
				Parameter chosenP = (Parameter) chosenO;
				if(chosenP.getName().equals(p.getName())) {
					found = true;
					break;
				}
			}
			if(!found) {
				currentParams.addElement(p);
			}
		}
	}
	// Method to remove all piezo once no is clicked
	private void removePiezo() {
		Object[] allParams = potentialParams.toArray();
		for(Object o: allParams) {
			Parameter p = (Parameter) o;
			for(Object chosenO: currentParams.toArray()) {
				Parameter chosenP = (Parameter) chosenO;
				if(chosenP.getName().equals(p.getName()) && chosenP.getValue() == p.getValue()) {
					currentParams.removeElement(chosenP);
					options.remove(chosenP.getName());
				}
			}
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
			for(String key : p.getAllowedValues()) {
				System.out.println("******" + key);
			}
			System.out.println("++++++" + newValue);
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
			line = reader.readNext(); // Read the headings
			System.out.println(this.fileName);
			for(String s: line) {
				System.out.println("\t"+s);
			}
			while ((line = reader.readNext()) != null ) {
				String display_name = line[0].trim();
				String parameter_name = line[1].trim();
				String parameter_value = line[2].trim();
				String parameter_units = line[3].trim();
				String parameter_description = line[4].trim();
				HashSet<String> allowedValues = null;
				if(line[5].length()>0) {
					String[] tokens = line[5].split(",");
					allowedValues = new HashSet<String>();
					for(String t: tokens) {
						allowedValues.add(t);
					}
				}
//				Parameter pr = new Parameter(parameter_name,parameter_value,parameter_units,parameter_description,allowedValues,display_name);
				Parameter pr = new Parameter(display_name,parameter_value,parameter_units,parameter_description,allowedValues,display_name);
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
//				Parameter newPr = new Parameter(pr.getName(),this.options.get(s),pr.getUnits(),pr.getDescription(),pr.getAllowedValues(),pr.getDisplayName());
				Parameter newPr = new Parameter(pr.getDisplayName(),this.options.get(s),pr.getUnits(),pr.getDescription(),pr.getAllowedValues(),pr.getDisplayName());
				System.out.println(newPr);
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
