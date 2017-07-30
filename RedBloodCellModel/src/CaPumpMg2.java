
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
		this.setHik(4e-7);
		this.setCapmgk(0.1);
		this.capmgki = 7.0;
		this.setFcapm(12.0);
		this.setH1(4.0);
		this.capk = 2e-4;
		this.flux_Ca = -0.03;
		this.setFlux_H(0.0);
		this.setCah(1);
	}
	public void compute_flux() {
		Double capmg = (this.cell.Mgf.getConcentration()/(this.getCapmgk()+this.cell.Mgf.getConcentration()))*(this.capmgki/(this.capmgki+this.cell.Mgf.getConcentration()));
		Double caphik=(this.getHik()/(this.getHik()+this.cell.H.getConcentration()));
		Double fcapglobal=-(this.getFcapm()/this.napump.getI_17())*capmg*caphik;
		this.flux_Ca = fcapglobal*(Math.pow(this.cell.Caf.getConcentration(),this.getH1()))/(Math.pow(this.capk,this.getH1()) + Math.pow(this.cell.Caf.getConcentration(),this.getH1()));
		if(this.getCah() == 1) {
			this.setFlux_H(-this.flux_Ca);
		}
		if(this.getCah() == 2) {
			this.setFlux_H(0.0);
		}
		if(this.getCah() == 0) {
			this.setFlux_H(-2.0*this.flux_Ca);
		}
	}
	public Double getFcapm() {
		return fcapm;
	}
	public void setFcapm(Double fcapm) {
		this.fcapm = fcapm;
	}
	public void setCapk(Double capk) {
		this.capk = capk;
	}
	public Double getH1() {
		return h1;
	}
	public void setH1(Double h1) {
		this.h1 = h1;
	}
	public Integer getCah() {
		return cah;
	}
	public void setCah(Integer cah) {
		this.cah = cah;
	}
	public Double getHik() {
		return hik;
	}
	public void setHik(Double hik) {
		this.hik = hik;
	}
	public Double getCapmgk() {
		return capmgk;
	}
	public void setCapmgk(Double capmgk) {
		this.capmgk = capmgk;
	}
	public Double getFlux_H() {
		return flux_H;
	}
	public void setFlux_H(Double flux_H) {
		this.flux_H = flux_H;
	}
}
