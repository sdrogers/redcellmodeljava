package modelcomponents;

import java.io.Serializable;

// Class to represent the chemical species being modelled
public class Species implements Serializable {
	private Double concentration = 0.0;
	private Double amount = 0.0;
	private Integer z = 1;
	public void set_z(int z) {
		this.z = z;
	}
	public Double getConcentration() {
		return this.concentration;
	}
	public void setConcentration(Double concentration) {
		this.concentration = concentration;
	}
	public void setAmount(Double amount) {
		this.amount = amount;
	}
	public Double getAmount() {
		return this.amount;
	}
	public Integer getZ() {
		return this.z;
	}
}
