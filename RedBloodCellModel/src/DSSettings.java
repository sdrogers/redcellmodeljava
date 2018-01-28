import java.util.HashMap;

public class DSSettings {
	private String description;
	private HashMap<String,String> stageOptions;
	public DSSettings() {
		this.stageOptions = new HashMap<String,String>();
		this.description = "";
	}
	public void appendComment(String line) {
		this.description += line + "\n";
	}
	public String toString() {
		String rString = "";
		rString += this.description;
		for(String key: stageOptions.keySet()) {
			rString += key + " " + stageOptions.get(key) + "\n";
		}
		return rString;
	}
	public void put(String key,String value) {
		this.stageOptions.put(key, value);
	}
}
