import java.util.HashMap;

public class DSSettings {
	private String description;
	private HashMap<String,String> stageOptions;
	public DSSettings() {
		this.stageOptions = new HashMap<String,String>();
		this.description = "";
	}
	public HashMap<String,String> getOptions() {
		return this.stageOptions;
	}
	public String getComments() {
		return this.description;
	}
	public void setComments(String newComments) {
		this.description = newComments;
	}
	public String getOptionString() {
		String rString = "";
		for(String key: stageOptions.keySet()) {
			rString += key + " " + stageOptions.get(key) + "\n";
		}
		return rString;
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
