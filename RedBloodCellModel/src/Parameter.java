import java.util.HashSet;

public class Parameter {
	private final String name;
	private final String value;
	private HashSet<String> allowedValues;
	public Parameter(String name,String value, HashSet<String> allowedValues) {
		this.name = name;
		this.value = value;
		this.allowedValues = allowedValues;
	}
	public String getName() {
		return name;
	}
	public String getValue() {
		return value;
	}
	public String toString() {
		return name + ": " + value;
	}
	public int hashCode() {
		return name.hashCode(); // hash on name so we dont end up with duplicates
	}
	public HashSet<String> getAllowedValues() {
		return this.allowedValues;
	}
}
