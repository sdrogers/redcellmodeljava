package modelcomponents;

import java.io.Serializable;

public class NaPump implements Serializable {
	private Double P_1; //V_max for sodium pump
	private Double defaultP_1;
	private Double P_2; //V_max reverse 
	private Double Na_to_K;
	private Region cell;
	private Region medium;
	private Double flux_fwd;
	private Double flux_rev;
	private Double flux_net;
	private Double flux_K;
	private Double total_flux;
	private Double I_17;
	private Double B_1;
	private Double B_2;
	private Double B_3;
	private Double B_4;
	private Double mgnap;
	private Double phnap;
	private Double I_3;
	private Double I_6;
	private Double I_9;
	private Double I_11;
	private Double Q10Active; // Previously known as B_9
	private Double mgnapk;
	private Double mgnapik;
	private Double I_77;
	private Double I_78;
	
	public NaPump(Region cell, Region medium) {
		this.setP_1(0.0);
		this.setP_2(0.0);
		this.Na_to_K = 1.5;
		this.cell = cell;
		this.medium = medium;
		this.flux_fwd = -2.61;
		this.flux_rev = 0.0015;
//		this.setFlux_net(this.flux_fwd + this.flux_rev);
		this.setFlux_net();
		this.setFlux_K();
		this.setTotal_flux(this.getFlux_net() + this.getFlux_K());
		this.I_17 = 0.0;
		this.B_1 = 0.2;
		this.B_2 = 18.0;
		this.B_3 = 0.1;
		this.B_4 = 8.3;
		this.mgnap = 0.0;
		this.phnap = 0.0;
		this.I_3 = 0.0;
		this.I_6 = 0.0;
		this.I_9 = 0.0;
		this.I_11 = 0.0;
		this.Q10Active = 4.0;
		this.mgnapk = 0.05;
		this.mgnapik = 4.0;
		this.I_77 = 7.216; //pH dependence of sodium pump, with I78
		this.I_78 = 0.4;
	}
	public void setQ10Active(Double Q10Active) {
		this.Q10Active = Q10Active;
	}
	private void compute_I(Double temperature) {
		Double I_1 = this.B_1*(1 + (this.cell.K.getConcentration()/this.B_4));
		Double I_2 = this.cell.Na.getConcentration()/(this.cell.Na.getConcentration() + I_1);
		this.I_3 = Math.pow(I_2, 3.0); // Concentration
		Double I_4 = this.B_3 * (1 + this.medium.Na.getConcentration()/this.B_2);
		Double I_5 = this.medium.K.getConcentration()/(this.medium.K.getConcentration() + I_4);
		this.I_6 = Math.pow(I_5, 2.0);
		Double I_7 = this.B_4*(1 + this.cell.Na.getConcentration()/this.B_1);
		Double I_8 = this.cell.K.getConcentration()/(this.cell.K.getConcentration() + I_7);
		this.I_9 = Math.pow(I_8, 2.0);
		Double I_10 = this.medium.Na.getConcentration()/(this.medium.Na.getConcentration() + this.B_2*(1 + this.medium.K.getConcentration()/this.B_3));
		this.I_11 = Math.pow(I_10, 3.0);
		// I_17 is temperature dependent factor for na and ca pumps - possibly re-factor out of a particular pump
		this.I_17 = Math.exp(((37.0-temperature)/10.0)*Math.log(this.Q10Active));
	}
	public void compute_permeabilities(Double temperature) {
		this.compute_I(temperature);
		this.compute_mgnap();
		this.compute_phnap();
		this.setDefaultP_1(Math.abs(this.flux_fwd/(this.mgnap*this.phnap*this.I_3*this.I_6)));
		this.setP_2(Math.abs(this.flux_rev/((this.mgnap*this.phnap)*(this.I_9*this.I_11))));
	}
	private void setDefaultP_1(double P_1) {
		this.defaultP_1 = P_1;
		this.setP_1(P_1);
	}
	public Double getDefaultP_1() {
		return defaultP_1;
	}
	private void compute_mgnap() {
		this.mgnap = (this.cell.Mgf.getConcentration()/(this.mgnapk + this.cell.Mgf.getConcentration()))*(this.mgnapik/(this.mgnapik + this.cell.Mgf.getConcentration()));
	}

	private void compute_phnap() {
		this.phnap = Math.exp(-Math.pow(((this.cell.getpH() - this.I_77)/this.I_78),2.0));
	}
	
	public void compute_flux(Double temperature) {
		this.compute_I(temperature);
		this.compute_mgnap();
		this.compute_phnap();
		this.flux_fwd = -(this.getP_1()/this.I_17)*this.mgnap*this.phnap*this.I_3*this.I_6;
		this.flux_rev = (this.getP_2()/this.I_17)*this.mgnap*this.phnap*this.I_9*this.I_11;
		this.setFlux_net();
		this.setFlux_K();
		this.setTotal_flux(this.getFlux_net() + this.getFlux_K());
	}
	
	public void setFluxFwd(Double f) {
		this.flux_fwd = f;
	}
	public Double getFluxFwd() {
		return this.flux_fwd;
	}
	public void setFluxRev(Double f) {
		this.flux_rev = f;
	}
	public Double getI_17() {
		return this.I_17;
	}
	public Double getFluxRev() {
		return this.flux_rev;
	}
	public Double getFlux_net() {
		return flux_net;
	}
//	public void setFlux_net(Double flux_net) {
	public void setFlux_net() {
//		this.flux_net = flux_net;
		this.flux_net = this.flux_fwd + this.flux_rev;
	}
	public Double getFlux_K() {
		return flux_K;
	}
//	public void setFlux_K(Double flux_K) {
	public void setFlux_K() {
//		this.flux_K = flux_K;
		this.flux_K = -this.getFlux_net()/this.Na_to_K;
	}
	public Double getP_1() {
		return P_1;
	}
	public void setP_1(Double p_1) {
		P_1 = p_1;
	}
	public Double getP_2() {
		return P_2;
	}
	public void setP_2(Double p_2) {
		P_2 = p_2;
	}
	public Double getTotal_flux() {
		return total_flux;
	}
	public void setTotal_flux(Double total_flux) {
		this.total_flux = total_flux;
	}
}
