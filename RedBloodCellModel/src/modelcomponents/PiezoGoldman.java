package modelcomponents;

import java.io.Serializable;

public class PiezoGoldman implements Serializable{
	private final Region cell;
	private final Region medium; 
	private Double permeability_Na ;
	private Double permeability_A ;
	private Double permeability_H ;
	private Double permeability_K ;

	private Double flux_Na ;
	private Double flux_A ;
	private Double flux_H ;
	private Double flux_K ;

	private Double Goldman_factor ;
	
	private Double rtoverf;
	private Double foverrt;
	
	public PiezoGoldman(Region cell, Region medium) {
		this.cell = cell;
		this.medium = medium;
		this.setPermeability_Na(0.0);
		this.setPermeability_A(0.0);
		this.setPermeability_H(0.0);
		this.setPermeability_K(0.0);
		this.setFlux_Na(0.0);
		this.setFlux_A(0.0);
		this.setFlux_H(0.0);
		this.setFlux_K(0.0);
		this.Goldman_factor = 0.0;
				
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

	public void compute_flux(Double Em, Double temperature, Double I_18) {
		this.gfactors(Em,temperature);
		this.setFlux_Na(this.fullgflux(this.cell.Na,this.medium.Na,this.getPermeability_Na(),I_18));
		this.setFlux_A(this.fullgflux(this.cell.A,this.medium.A,this.getPermeability_A(),I_18));
		this.setFlux_H(this.fullgflux(this.cell.H,this.medium.H,this.getPermeability_H(),I_18));
		this.setFlux_K(this.fullgflux(this.cell.K,this.medium.K,this.getPermeability_K(),I_18));
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
}

