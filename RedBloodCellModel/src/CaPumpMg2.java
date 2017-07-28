
public class CaPumpMg2 {
	private Region cell;;
	private Region medium;
	// needs napump for temp factor
	private NaPump napump;
	private Double hik;
	private Double capmgk;
	private Double capmgki;
	private Double fcapm;
	private Double h1;
	private Double capk;
	private Double flux_Ca;
	private Double flux_H;
	private Integer cah;
	
	public CaPumpMg2(Region cell, Region medium, NaPump napump) {
		this.cell = cell;
		this.medium = medium;
		// needs napump for temp factor
		this.napump = napump;
		this.hik = 4e-7;
		this.capmgk = 0.1;
		this.capmgki = 7.0;
		this.fcapm = 12.0;
		this.h1 = 4.0;
		this.capk = 2e-4;
		this.flux_Ca = -0.03;
		this.flux_H = 0.0;
		this.cah = 1;
	}
	public void compute_flux() {
		Double capmg = (this.cell.Mgf.getConcentration()/(this.capmgk+this.cell.Mgf.getConcentration()))*(this.capmgki/(this.capmgki+this.cell.Mgf.getConcentration()));
		Double caphik=(this.hik/(this.hik+this.cell.H.getConcentration()));
		Double fcapglobal=-(this.fcapm/this.napump.getI_17())*capmg*caphik;
		this.flux_Ca = fcapglobal*(Math.pow(this.cell.Caf.getConcentration(),this.h1))/(Math.pow(this.capk,this.h1) + Math.pow(this.cell.Caf.getConcentration(),this.h1));
		if(this.cah == 1) {
			this.flux_H = -this.flux_Ca;
		}
		if(this.cah == 2) {
			this.flux_H = 0.0;
		}
		if(this.cah == 0) {
			this.flux_H = -2.0*this.flux_Ca;
		}
	}
	
}
