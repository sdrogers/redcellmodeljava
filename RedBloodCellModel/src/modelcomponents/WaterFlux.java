package modelcomponents;

public class WaterFlux {
	private final Region cell;
	private final Region medium;
	private Double flux;
	private Double permeability;
	
	public WaterFlux(Region cell, Region medium) {
		this.cell = cell;
		this.medium = medium;
		this.setFlux(0.0);
		this.setPermeability(2.0);
	}
	
	public void compute_flux(Double fHb, Double cbenz2, Double buffer_conc,Double edgto, Double I_18) {
		this.cell.Os.setConcentration(
				this.cell.Na.getConcentration() + 
				this.cell.K.getConcentration() + 
				this.cell.A.getConcentration() + 
				fHb*this.cell.Hb.getConcentration() + 
				this.cell.X.getConcentration() + 
				this.cell.Mgf.getConcentration() + 
				this.cell.Caf.getConcentration() + 
				cbenz2);
		System.out.println("Is this happening? " + this.cell.Os.getConcentration());
		this.medium.Os.setConcentration(this.medium.Na.getConcentration() + this.medium.K.getConcentration() + this.medium.A.getConcentration() + buffer_conc + this.medium.Gluconate.getConcentration() + this.medium.Glucamine.getConcentration() + this.medium.Sucrose.getConcentration() + (this.medium.Mgf.getConcentration()+this.medium.Caf.getConcentration()+edgto));
		Double D_7 = this.cell.Os.getConcentration() - this.medium.Os.getConcentration();
		this.setFlux((this.getPermeability()/I_18)*D_7);
	}

	public Double getFlux() {
		return flux;
	}

	public void setFlux(Double flux) {
		this.flux = flux;
	}

	public Double getPermeability() {
		return permeability;
	}

	public void setPermeability(Double permeability) {
		this.permeability = permeability;
	}
}
