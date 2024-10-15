package modelcomponents;

import java.io.Serializable;

public class PassiveMg implements Serializable {
	private final Region cell; 
	private final Region medium;
	private final Goldman goldman;
	private Double flux;
	private Double fcalm;
	
	public PassiveMg(Region cell, Region medium, Goldman goldman) {
		this.cell = cell;
		this.medium = medium;
		this.goldman = goldman;
		this.setFlux(0.03);
		this.setFcalm(0.050);
	}

	public void compute_flux(Double I_18) {
		this.setFlux(-(this.getFcalm()/I_18)*2*this.goldman.getGoldmanFactor()*((this.medium.Mgf.getConcentration()-this.cell.Mgf.getConcentration()*Math.exp(2*this.goldman.getGoldmanFactor()))/(1-Math.exp(2*this.goldman.getGoldmanFactor()))));
	}
	public Double getFcalm() {
		return fcalm;
	}
	public void setFcalm(Double fcalm) {
		this.fcalm = fcalm;
	}
	public Double getFlux() {
		return flux;
	}
	public void setFlux(Double flux) {
		this.flux = flux;
	}
}
