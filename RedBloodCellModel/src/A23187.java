
public class A23187 {
	private Region cell;
	private Region medium;
	private Double camk ;
	private Double mgmk ;
	private Double caik ;
	private Double mgik ;

	private Double flux_Mg;
	private Double flux_Ca;

	private Double permeability_Mg ;
	private Double permeability_Ca ;
	
	public A23187(Region cell, Region medium) {
		this.cell = cell;
		this.medium = medium;
		this.setCamk(10.0);
		this.setMgmk(10.0);
		this.setCaik(10.0);
		this.setMgik(10.0);

		this.setFlux_Mg(0.0);
		this.setFlux_Ca(0.0);

		this.setPermeability_Mg(0.01);
		this.setPermeability_Ca(0.01);
	}
	public void compute_flux(Double I_18) {
		Double mgcao=(this.medium.Mgf.getConcentration()/(this.getMgmk()+(this.getMgmk()*this.medium.Caf.getConcentration()/this.getCaik())+this.medium.Mgf.getConcentration()));
		Double camgo=(this.medium.Caf.getConcentration()/(this.getCamk()+(this.getCamk()*this.medium.Mgf.getConcentration()/this.getMgik())+this.medium.Caf.getConcentration()));
		Double mgcai=(this.cell.Mgf.getConcentration()/(this.getMgmk()+(this.getMgmk()*this.cell.Caf.getConcentration()/this.getCaik())+this.cell.Mgf.getConcentration()));
		Double camgi=(this.cell.Caf.getConcentration()/(this.getCamk()+(this.getCamk()*this.cell.Mgf.getConcentration()/this.getMgik())+this.cell.Caf.getConcentration()));
//		System.out.println("Stuff: " + this.cell.H.getConcentration() + "," + this.medium.H.getConcentration() + "," + I_18);
		this.setFlux_Mg(this.getPermeability_Mg()/I_18*(mgcao*Math.pow(this.cell.H.getConcentration(),2.0) - mgcai*Math.pow(this.medium.H.getConcentration(),2.0)));
		this.setFlux_Ca(this.getPermeability_Ca()/I_18*(camgo*Math.pow(this.cell.H.getConcentration(),2.0) - camgi*Math.pow(this.medium.H.getConcentration(),2.0)));
//		System.out.println("Fluxes: " + this.getFlux_Mg() + "," + this.getFlux_Ca());
	}
	public Double getFlux_Mg() {
		return flux_Mg;
	}
	public void setFlux_Mg(Double flux_Mg) {
		this.flux_Mg = flux_Mg;
	}
	public Double getFlux_Ca() {
		return flux_Ca;
	}
	public void setFlux_Ca(Double flux_Ca) {
		this.flux_Ca = flux_Ca;
	}
	public Double getPermeability_Mg() {
		return permeability_Mg;
	}
	public void setPermeability_Mg(Double permeability_Mg) {
		this.permeability_Mg = permeability_Mg;
	}
	public Double getCamk() {
		return camk;
	}
	public void setCamk(Double camk) {
		this.camk = camk;
	}
	public Double getMgmk() {
		return mgmk;
	}
	public void setMgmk(Double mgmk) {
		this.mgmk = mgmk;
	}
	public Double getCaik() {
		return caik;
	}
	public void setCaik(Double caik) {
		this.caik = caik;
	}
	public Double getMgik() {
		return mgik;
	}
	public void setMgik(Double mgik) {
		this.mgik = mgik;
	}
	public Double getPermeability_Ca() {
		return permeability_Ca;
	}
	public void setPermeability_Ca(Double permeability_Ca) {
		this.permeability_Ca = permeability_Ca;
	}
}
