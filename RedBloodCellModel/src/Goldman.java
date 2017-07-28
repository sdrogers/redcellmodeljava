
public class Goldman {
	private Region cell;
	private Region medium; 
	private Double permeability_Na ;
	private Double permeability_A ;
	private Double permeability_H ;
	private Double permeability_K ;

	private Double flux_Na ;
	private Double flux_A ;
	private Double flux_H ;
	private Double flux_K ;

	private Double Goldman_factor ;
	private Double P_11 ;

	private Double pkm ;
	private Double pkcak ;
	private Double pgkh ;
	
	private Double rtoverf;
	
	public Goldman(Region cell, Region medium) {
		this.cell = cell;
		this.medium = medium;
		this.permeability_Na = 0.0;
		this.permeability_A = 1.2;
		this.permeability_H = 2e-10;
		this.permeability_K = 0.0;
		this.flux_Na = 0.0;
		this.flux_A = 0.0;
		this.flux_H = 0.0;
		this.flux_K = 0.0;
		this.Goldman_factor = 0.0;
		this.P_11 = 0.0;
		this.setPkm(30.0);
		this.setPkcak(1e-2);
	}
	
	private void gfactors(Double Em, Double temperature) {
		this.rtoverf = ((8.6156e-2)*(273+temperature));
		Double foverrt = 1.0/((8.6156e-2)*(273+temperature));
		this.Goldman_factor = Em*foverrt;
	}
	
	private void compute_permeabilities(Double Em, Double temperature) {
		this.gfactors(Em,temperature);
		this.permeability_Na = Math.abs(this.flux_Na/this.gflux(this.cell.Na,this.medium.Na));
		this.permeability_K = Math.abs(this.flux_K/this.gflux(this.cell.K,this.medium.K));
	}
	private Double total_G_permeability_K() {
		Double I_62 = 1.0/(1.0+ Math.pow(this.cell.H.getConcentration(),4.0)/2.5e-30);
		this.P_11 = this.pgkh*I_62;
		Double P_6 = this.permeability_K + this.getPkm()*(Math.pow(this.cell.Caf.getConcentration(),4.0)/(Math.pow(this.getPkcak(),4.0) + Math.pow(this.cell.Caf.getConcentration(),4.0)));
		return P_6 + this.P_11;
	}
	public void compute_flux(Double Em, Double temperature, Double I_18) {
		this.gfactors(Em,temperature);
		this.flux_Na = this.fullgflux(this.cell.Na,this.medium.Na,this.permeability_Na,I_18);
		this.flux_A = this.fullgflux(this.cell.A,this.medium.A,this.permeability_A,I_18);
		this.flux_H = this.fullgflux(this.cell.H,this.medium.H,this.permeability_H,I_18);
		this.flux_K = this.fullgflux(this.cell.K,this.medium.K,this.total_G_permeability_K(),I_18);
	}
	private Double gflux(Species cell_species, Species medium_species) {
		return -cell_species.getZ()*this.Goldman_factor*(medium_species.getConcentration() - cell_species.getConcentration()*Math.exp(cell_species.getZ()*this.Goldman_factor))/(1.0-Math.exp(cell_species.getZ()*this.Goldman_factor));
	}
	
	private Double fullgflux(Species cell_species,Species medium_species,Double permeability, Double I_18) {
		return (permeability/I_18)*this.gflux(cell_species,medium_species);
	}
	public Double getGoldmanFactor() {
		return this.Goldman_factor;
	}

	public Double getPkm() {
		return pkm;
	}

	public void setPkm(Double pkm) {
		this.pkm = pkm;
	}

	public Double getPkcak() {
		return pkcak;
	}

	public void setPkcak(Double pkcak) {
		this.pkcak = pkcak;
	}
}
