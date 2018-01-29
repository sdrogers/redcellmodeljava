import java.awt.Component;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class ExperimentalSettings {
	private HashMap<String,String> rSOptions;
	private LinkedList<DSSettings> dSStages;
	private String inputFileName = null;
	private String overallComments;
	private String rSComments;
	private int loadState;
	public ExperimentalSettings() {
		// Constructor to start with an empty settings object
		this.rSOptions = new HashMap<String,String>();
		this.dSStages = new LinkedList<DSSettings>();
		this.dSStages.add(new DSSettings());
	}
	public ExperimentalSettings(String fileName) {
		// Constructor to populate from a file
		this.inputFileName = fileName;
		this.rSOptions = new HashMap<String,String>();
		this.dSStages = new LinkedList<DSSettings>();
		this.loadState = 0; // changes as we load things 0 = ready for rs
		this.overallComments = "";
		this.rSComments = "";
		this.loadFromFile();
	}
	public int getNStages() {
		return this.dSStages.size();
	}
	
	private void loadFromFile() {
		BufferedReader reader = null;
		File file = new File(this.inputFileName);
		try {
			reader = new BufferedReader(new FileReader(file));
			String line = null;
			DSSettings currentDSSettings = null;
			while((line = reader.readLine()) != null) {
//				System.out.println(line);
				if(line.startsWith("#")) {
					// this is a comment line
					if(this.loadState == 0) {
						this.overallComments += line + "\n";
					}else if(this.loadState == 1) {
						this.rSComments += line + "\n";
					}else {
						currentDSSettings.appendComment(line);
					}
				}else if(line.startsWith("RS")) {
					this.loadState = 1;
				}else if(line.startsWith("DS")) {
					// a new DS is being defined
					currentDSSettings = new DSSettings();
					this.dSStages.add(currentDSSettings);
					this.loadState += 1;
				}else if(line.length() > 1) {
					// It's a parameter
					String[] tokens = line.trim().split(" ");
					String key = tokens[0];
					String value = tokens[1];
					if(this.loadState == 1) {
						// add to the reference state settings
						rSOptions.put(key, value);
					}else if(this.loadState > 1) {
						// add to the current dynamic state
						currentDSSettings.put(key,value);
					}
				}
			}
		}catch(FileNotFoundException e) {
			e.printStackTrace();
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	public String getRSString() {
		String rString = "";
		for(String key: this.rSOptions.keySet()) {
			rString += key + " " + this.rSOptions.get(key) + "\n"; 
		}
		return rString;
	}
	public String toString() {
		String rString = this.overallComments + "\n";
		rString += "RS\n";
		rString += this.rSComments;
		for(String key: rSOptions.keySet()) {
			rString += key + " " + rSOptions.get(key) + "\n";
		}
		rString += "\n";
		for(DSSettings d: this.dSStages) {
			rString += "DS\n";
			rString += d;
			rString += "\n";
		}
		return rString;
	}
	public String getOverallComments() {
		return this.overallComments;
	}
	public void setOverallComments(String comments) {
		this.overallComments = comments;
	}
	public String getRSComments() {
		return this.rSComments;
	}
	public HashMap<String,String> getRSOptions() {
		return this.rSOptions;
	}
	public void writeFile(JFrame parent) {
		JFileChooser saveJfc = new JFileChooser();
		int returnVal = saveJfc.showSaveDialog(parent); // Note that null doesn't work as parent!
		String outputFileName = saveJfc.getSelectedFile().getPath();
		File file = new File(outputFileName);
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(file));
			writer.write(this.toString());
		}catch(IOException e) {
			e.printStackTrace();
		}finally {
			if(writer!= null) {
				try {
					writer.close();
				}catch(IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	public LinkedList<DSSettings> getDSStages() {
		return this.dSStages;
	}
	public void addStage(DSSettings newStage) {
		this.dSStages.add(newStage);
	}
	public void remove(DSSettings stage) {
		this.dSStages.remove(stage);
	}
//	public static void main(String [] args) {
//		// main for testing
//		JFileChooser jfc = new JFileChooser();
//		jfc.showOpenDialog(null);
//		ExperimentalSettings es = new ExperimentalSettings(jfc.getSelectedFile().getPath());
//		
////		System.out.println(es);
////		es.writeFile();
//		ExperimentScreen eScreen = new ExperimentScreen(es);
//	}
}
