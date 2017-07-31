
public class CarrierMediated {
	private Region cell;
	private Region medium;
	private Double flux_Na;
	private Double flux_K;
	private Double permeability_Na;
	private Double permeability_K;
	
	public CarrierMediated(Region cell, Region medium) {
		this.cell = cell;
		this.medium = medium;
		this.setFlux_Na(0.0);
		this.setFlux_K(0.0);
		this.setPermeability_K(0.0);
		this.setPermeability_Na(0.0);
	}	
	public void compute_permeabilities() {
		this.setPermeability_Na(Math.abs(this.getFlux_Na()/(this.cell.Na.getConcentration()*this.cell.A.getConcentration()-this.medium.Na.getConcentration()*this.medium.A.getConcentration())));
		this.setPermeability_K(Math.abs(this.getFlux_K()/(this.cell.K.getConcentration()*this.cell.A.getConcentration()-this.medium.K.getConcentration()*this.medium.A.getConcentration()))); 
	}
	
	public void compute_flux(Double I_18) {
		this.setFlux_Na(-(this.getPermeability_Na()/I_18)*(this.cell.Na.getConcentration()*this.cell.A.getConcentration()-this.medium.Na.getConcentration()*this.medium.A.getConcentration()));
		this.setFlux_K(-(this.getPermeability_K()/I_18)*(this.cell.K.getConcentration()*this.cell.A.getConcentration()-this.medium.K.getConcentration()*this.medium.A.getConcentration()));
	}
	public Double getFlux_Na() {
		return flux_Na;
	}
	public void setFlux_Na(Double flux_Na) {
		this.flux_Na = flux_Na;
	}
	public Double getFlux_K() {
		return flux_K;
	}
	public void setFlux_K(Double flux_K) {
		this.flux_K = flux_K;
	}
	public Double getPermeability_Na() {
		return permeability_Na;
	}
	public void setPermeability_Na(Double permeability_Na) {
		this.permeability_Na = permeability_Na;
	}
	public Double getPermeability_K() {
		return permeability_K;
	}
	public void setPermeability_K(Double permeability_K) {
		this.permeability_K = permeability_K;
	}

}
