package utilities;
import java.util.HashMap;

public class ResultHash {
	private HashMap<String,Double> map;
	private Double time;
	public ResultHash(Double time) {
		this.time = time;
		this.map = new HashMap<String,Double>();
	}
	public void setItem(String key, Double val) {
		this.map.put(key, val);
	}
	public Double getItem(String key) {
		return this.map.get(key);
	}
	public Double getTime() {
		return this.time;
	}
}
