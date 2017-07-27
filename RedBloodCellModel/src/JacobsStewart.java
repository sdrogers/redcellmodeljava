
public class JacobsStewart {
	private Region cell;
	private Region medium;
	private Double permeability;
	private Double flux_A;
	private Double flux_H;
	
	public JacobsStewart(Region cell,Region medium) {
		this.cell = cell;
		this.medium = medium;
		this.permeability = 2.5e8;
		this.flux_A = 0.0;
		this.flux_H = 0.0;
	}
	public void compute_flux(Double I_18) {
		Double I_13 = -(this.permeability/I_18)*
				(this.cell.A.getConcentration()*this.cell.H.getConcentration() - 
						(this.medium.A.getConcentration()*this.medium.H.getConcentration()));
		this.flux_A = I_13;
		this.flux_H = I_13;
	}
	public Double getFlux_A() {
		return this.flux_A;
	}
	public Double getFlux_H() {
		return this.flux_H;
	}
}
