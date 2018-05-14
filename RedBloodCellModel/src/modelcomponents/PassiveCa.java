package modelcomponents;

public class PassiveCa {
	private Region cell; 
	private Region medium;
	// needs ref to goldman object for goldman_factor
	private Goldman goldman;
	private Double flux;
	private Double calk;
	private Double calik;
	private Double fcalm;
	
	public PassiveCa(Region cell, Region medium, Goldman goldman) {
		this.cell = cell;
		this.medium = medium;
		// needs ref to goldman object for goldman_factor
		this.goldman = goldman;
		this.setFlux(0.03);
		this.calk = 0.8;
		this.calik = 0.0002;
		this.setFcalm(0.050);
	}
	// in PIEZO, calreg = 1...
	public void compute_flux(Double I_18) {
		Double calreg = (this.calik/(this.calik+this.cell.Caf.getConcentration()))*(this.medium.Caf.getConcentration()/(this.calk+this.medium.Caf.getConcentration()));
		this.setFlux(-(this.getFcalm()/I_18)*calreg*2*this.goldman.getGoldmanFactor()*((this.medium.Caf.getConcentration()-this.cell.Caf.getConcentration()*Math.exp(2*this.goldman.getGoldmanFactor()))/(1-Math.exp(2*this.goldman.getGoldmanFactor()))));
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
