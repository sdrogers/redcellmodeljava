package modelcomponents;

import java.io.Serializable;

public class CaPumpMg2 implements Serializable{
	private final Region cell;;
	private final Region medium;
	private final NaPump napump;
	private Double hik;
	private Double capmgk;
	private Double capmgki;
	private Double fcapm;
	private Double defaultFcapm;
	private Double h1;
	private Double capk;
	private Double flux_Ca;
	private Double flux_H;
	private Integer cah;
	
	public CaPumpMg2(Region cell, Region medium, NaPump napump) {
		this.cell = cell;
		this.medium = medium;
		this.napump = napump;
		this.setHik(4e-7);
		this.setCapmgk(0.1);
		this.capmgki = 7.0;
		this.setDefaultFcapm(12.0);
		this.setH1(4.0);
		this.capk = 2e-4;
		this.setFlux_Ca(-0.03);
		this.setFlux_H(0.0);
		this.setCah(0);
	}
	public void compute_flux() {
		Double capmg = (this.cell.Mgf.getConcentration()/(this.getCapmgk()+this.cell.Mgf.getConcentration()))*(this.capmgki/(this.capmgki+this.cell.Mgf.getConcentration()));
		Double caphik=(this.getHik()/(this.getHik()+this.cell.H.getConcentration()));
		Double fcapglobal=-(this.getFcapm()/this.napump.getI_17())*capmg*caphik;
		this.setFlux_Ca(fcapglobal*(Math.pow(this.cell.Caf.getConcentration(),this.getH1()))/(Math.pow(this.capk,this.getH1()) + Math.pow(this.cell.Caf.getConcentration(),this.getH1())));
		if(this.getCah() == 1) {
			this.setFlux_H(-this.getFlux_Ca());
		}
		if(this.getCah() == 2) { // No protons
			this.setFlux_H(0.0);
		}
		if(this.getCah() == 0) { // 2 protons: this should be default
			this.setFlux_H(-2.0*this.getFlux_Ca());
		}
	}
	public Double getFcapm() {
		return fcapm;
	}
	public Double getDefaultFcapm() {
		return defaultFcapm;
	}
	public void setDefaultFcapm(Double fcapm) {
		this.defaultFcapm = fcapm;
		this.setFcapm(fcapm);
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
	public Double getFlux_Ca() {
		return flux_Ca;
	}
	public void setFlux_Ca(Double flux_Ca) {
		this.flux_Ca = flux_Ca;
	}
}
