package utilities;

import java.util.LinkedList;

public class ExampleProtocols {
	public static ExperimentalSettings getPKG() {
		ExperimentalSettings example = new ExperimentalSettings();
		LinkedList<DSSettings> d = example.getDSStages();
		d.get(0).put("Time","30.0");
		d.get(0).put("PKG", "30.0");
		d.get(0).put("PAG","50.0");
		return example;
	}
	public static ExperimentalSettings getA() {
		// Protocol A from paper
		ExperimentalSettings example = new ExperimentalSettings();
		LinkedList<DSSettings> d = example.getDSStages();
		d.get(0).put("Time", "30.0");
		d.get(0).put("Cell volume fraction", "0.1");
		d.add(new DSSettings());
		d.get(1).put("Time", "60.0");
		d.get(1).put("PMCA", "100.0");
		d.get(1).put("Na/K pump", "100.0");
		
		return example;
	}
	public static ExperimentalSettings getB() {
		// Protocol A from paper
		ExperimentalSettings example = new ExperimentalSettings();
		LinkedList<DSSettings> d = example.getDSStages();
		d.get(0).put("Time", "30.0");
		d.get(0).put("Cell volume fraction", "0.1");
		d.get(0).put("Cycles per print", "11");
		d.add(new DSSettings());
		d.get(1).put("Time", "600.0");
		d.get(1).put("PMCA", "100.0");
		d.get(1).put("Na/K pump", "100.0");
		
		return example;
	}
}
