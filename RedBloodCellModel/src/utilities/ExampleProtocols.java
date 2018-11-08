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
}
