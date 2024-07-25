package modelcomponents;

import java.io.Serializable;

public class Goldman implements Serializable{
	private final Region cell;
	private final Region medium; 
	private Double permeability_Na ;
	private Double permeability_A ;
	private Double permeability_H ;
	private Double permeability_K ;
	private Double permeability_Mg ;

	private Double flux_Na ;
	private Double flux_A ;
	private Double flux_H ;
	private Double flux_K ;
	private Double flux_Mg ;

	private Double Goldman_factor ;
	private Double P_11 ;

	private Double pkm ;
	private Double defaultPkm;
	private Double pkcak ;
	private Double pgkh ;
	
	private Double rtoverf;
	private Double foverrt;
	
	public Goldman(Region cell, Region medium) {
		this.cell = cell;
		this.medium = medium;
		this.setPermeability_Na(0.0);
		this.setPermeability_A(1.2);
		this.setPermeability_H(2e-10);
		this.setPermeability_K(0.0);
		this.setPermeability_Mg(0.05);
		this.setFlux_Na(0.0);
		this.setFlux_A(0.0);
		this.setFlux_H(0.0);
		this.setFlux_K(0.0);
		this.setFlux_Mg(0.0);

		this.Goldman_factor = 0.0;
		this.P_11 = 0.0;
		this.setDefaultPkm(30.0);
		this.setPkcak(1e-2);
		
		this.pgkh = 0.0;
	}
	
	private void gfactors(Double Em, Double temperature) {
		this.rtoverf = ((8.6156e-2)*(273+temperature));
		this.foverrt = 1.0/((8.6156e-2)*(273+temperature));
		this.Goldman_factor = Em*foverrt;
	}
	public Double getRtoverf() {
		return this.rtoverf;
	}
	public Double getFoverrt() {
		return this.foverrt;
	}
	public void compute_permeabilities(Double Em, Double temperature) {
		this.gfactors(Em,temperature);
		this.setPermeability_Na(Math.abs(this.getFlux_Na()/this.gflux(this.cell.Na,this.medium.Na)));
		this.setPermeability_K(Math.abs(this.getFlux_K()/this.gflux(this.cell.K,this.medium.K)));
		//this.setPermeability_Mg(Math.abs(this.getFlux_Mg()/this.gflux(this.cell.Mgf, this.medium.Mgf)));
	}
	private void computeP_11() {
		Double I_62 = 1.0/(1.0+ Math.pow(this.cell.H.getConcentration(),4.0)/2.5e-30);
		this.P_11 = this.getPgkh()*I_62;
	}
	private Double computeP_6() {
		Double P_6 = this.getPermeability_K() + this.getPkm()*(Math.pow(this.cell.Caf.getConcentration(),4.0)/(Math.pow(this.getPkcak(),4.0) + Math.pow(this.cell.Caf.getConcentration(),4.0)));
		return P_6;
	}
	private Double total_G_permeability_K() {
		this.computeP_11();
		return this.computeP_6() + this.P_11;
	}
//	// These methods might now be obsolete
//	public Double computePKGPiezo(Double I_18) {
//		return this.gflux(this.cell.K,this.medium.K)*(this.getPermeability_K())/I_18;
//	}
	public Double computeFKGardos(Double I_18) {
		return this.gflux(this.cell.K,this.medium.K)*(((this.computeP_6() - this.getPermeability_K()))/I_18);
	}
	
	public void compute_flux(Double Em, Double temperature, Double I_18) {
		this.gfactors(Em,temperature);
		this.setFlux_Na(this.fullgflux(this.cell.Na,this.medium.Na,this.getPermeability_Na(),I_18));
		this.setFlux_A(this.fullgflux(this.cell.A,this.medium.A,this.getPermeability_A(),I_18));
		this.setFlux_H(this.fullgflux(this.cell.H,this.medium.H,this.getPermeability_H(),I_18));
		this.setFlux_K(this.fullgflux(this.cell.K,this.medium.K,this.total_G_permeability_K(),I_18));
		this.setFlux_Mg(this.fullgflux(this.cell.Mgf, this.medium.Mgf,this.getPermeability_Mg(),I_18));
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
	public Double getDefaultPkm() {
		return this.defaultPkm;
	}
	public void setDefaultPkm(Double pkm) {
		this.defaultPkm = pkm;
		this.setPkm(pkm);
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

	public Double getFlux_A() {
		return flux_A;
	}

	public void setFlux_A(Double flux_A) {
		this.flux_A = flux_A;
	}

	public Double getFlux_Na() {
		return flux_Na;
	}

	public void setFlux_Na(Double flux_Na) {
		this.flux_Na = flux_Na;
	}

	public void setFlux_Mg(Double flux_Mg) {
		this.flux_Mg = flux_Mg;
	}

	public Double getFlux_Mg() {
		return this.flux_Mg;
	}

	public Double getFlux_K() {
		return flux_K;
	}

	public void setFlux_K(Double flux_K) {
		this.flux_K = flux_K;
	}

	public Double getFlux_H() {
		return flux_H;
	}

	public void setFlux_H(Double flux_H) {
		this.flux_H = flux_H;
	}

	public Double getPermeability_K() {
		return permeability_K;
	}

	public void setPermeability_K(Double permeability_K) {
		this.permeability_K = permeability_K;
	}

	public Double getPgkh() {
		return pgkh;
	}

	public void setPgkh(Double pgkh) {
		this.pgkh = pgkh;
	}

	public Double getPermeability_Na() {
		return permeability_Na;
	}

	public void setPermeability_Na(Double permeability_Na) {
		this.permeability_Na = permeability_Na;
	}

	public Double getPermeability_A() {
		return permeability_A;
	}

	public void setPermeability_A(Double permeability_A) {
		this.permeability_A = permeability_A;
	}

	public Double getPermeability_H() {
		return permeability_H;
	}

	public void setPermeability_H(Double permeability_H) {
		this.permeability_H = permeability_H;
	}

	public void setPermeability_Mg(Double permeability_Mg) {
		this.permeability_Mg = permeability_Mg;
	}

	public Double getPermeability_Mg() {
		return this.permeability_Na;
	}
}
