import java.util.HashSet;

public class Parameter {
	private final String name;
	private final String value;
	private final String units;
	private final String description;
	private HashSet<String> allowedValues;
	public Parameter(String name,String value,String units, String description, HashSet<String> allowedValues) {
		this.name = name;
		this.value = value;
		this.allowedValues = allowedValues;
		this.units = units;
		this.description = description;
	}
	public String getName() {
		return name;
	}
	public String getUnits() {
		return units;
	}
	public String getDescription() {
		return description;
	}
	public String getValue() {
		return value;
	}
	public String toString() {
		String s = name + ": " + value;
		if(units.length()>0) {
			s += " (" + units + ")";
		}
		return s;
	}
	public int hashCode() {
		return name.hashCode(); // hash on name so we dont end up with duplicates
	}
	public HashSet<String> getAllowedValues() {
		return this.allowedValues;
	}
}
