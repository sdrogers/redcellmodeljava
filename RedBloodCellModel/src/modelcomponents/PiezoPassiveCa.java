package modelcomponents;

import java.io.Serializable;

public class PiezoPassiveCa implements Serializable{
	private final Region cell; 
	private final Region medium;
	private final PiezoGoldman goldman;
	private Double flux;
	private Double fcalm;
	private Double calk,calik;
	
	public PiezoPassiveCa(Region cell, Region medium, PiezoGoldman goldman) {
		this.cell = cell;
		this.medium = medium;
		this.goldman = goldman;
		this.calk = 0.8;
		this.calik = 0.0002;
		this.setFlux(0.0);
		this.setFcalm(0.0); 
	}
	public void compute_flux(Double I_18) {
//		Double calreg = (this.calik/(this.calik+this.cell.Caf.getConcentration()))*(this.medium.Caf.getConcentration()/(this.calk+this.medium.Caf.getConcentration()));
		Double calreg = 1.0; // Calreg isn't used in this channel...
		this.setFlux(-(this.getFcalm()/I_18)*2*calreg*this.goldman.getGoldmanFactor()*((this.medium.Caf.getConcentration()-this.cell.Caf.getConcentration()*Math.exp(2*this.goldman.getGoldmanFactor()))/(1-Math.exp(2*this.goldman.getGoldmanFactor()))));
	}
	public Double getFcalm() {
		return this.fcalm;
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

