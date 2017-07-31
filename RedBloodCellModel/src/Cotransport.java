
public class Cotransport {
	private Double permeability = 0.000000001;
	private Double flux_A = 0.0;
	private Double flux_Na = 0.0;
	private Double flux_K = 0.0;
	private Region cell;
	private Region medium;
	private Double zero_factor = 0.0;

	public Cotransport(Region cell, Region medium) {
		this.cell = cell;
		this.medium = medium;
	}
	
	public void compute_zero_factor() {
		this.zero_factor = (this.cell.Na.getConcentration()*this.cell.K.getConcentration()*Math.pow(this.cell.A.getConcentration(),2.0))/
				(this.medium.Na.getConcentration()*this.medium.K.getConcentration()*Math.pow(this.medium.A.getConcentration(), 2.0));
	}
	public void compute_flux(Double I_18) {
		Double I_12 = -(this.getPermeability()/I_18)*
				(Math.pow(this.cell.A.getConcentration(),2.0)*this.cell.Na.getConcentration()*this.cell.K.getConcentration() - 
						this.zero_factor*(Math.pow(this.medium.A.getConcentration(), 2.0)*this.medium.Na.getConcentration()*this.medium.K.getConcentration()));
		this.flux_A = 2*I_12;
		this.flux_Na = I_12;
		this.flux_K = I_12;
	}
	public Double getFlux_A() {
		return this.flux_A;
	}
	public Double getFlux_Na() {
		return this.flux_Na;
	}
	public Double getFlux_K() {
		return this.flux_K;
	}

	public Double getPermeability() {
		return permeability;
	}

	public void setPermeability(Double permeability) {
		this.permeability = permeability;
	}
}
