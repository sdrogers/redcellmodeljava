
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
		this.camk = 10.0;
		this.mgmk = 10.0;
		this.caik = 10.0;
		this.mgik = 10.0;

		this.setFlux_Mg(0.0);
		this.setFlux_Ca(0.0);

		this.permeability_Mg = 0.01;
		this.permeability_Ca = 0.01;
	}
	public void compute_flux(Double I_18) {
		Double mgcao=(this.medium.Mgf.getConcentration()/(this.mgmk+(this.mgmk*this.medium.Caf.getConcentration()/this.caik)+this.medium.Mgf.getConcentration()));
		Double camgo=(this.medium.Caf.getConcentration()/(this.camk+(this.camk*this.medium.Mgf.getConcentration()/this.mgik)+this.medium.Caf.getConcentration()));
		Double mgcai=(this.cell.Mgf.getConcentration()/(this.mgmk+(this.mgmk*this.cell.Caf.getConcentration()/this.caik)+this.cell.Mgf.getConcentration()));
		Double camgi=(this.cell.Caf.getConcentration()/(this.camk+(this.camk*this.cell.Mgf.getConcentration()/this.mgik)+this.cell.Caf.getConcentration()));
		this.setFlux_Mg(this.permeability_Mg/I_18*(mgcao*Math.pow(this.cell.H.getConcentration(),2.0) - mgcai*Math.pow(this.medium.H.getConcentration(),2.0)));
		this.setFlux_Ca(this.permeability_Ca/I_18*(camgo*Math.pow(this.cell.H.getConcentration(),2.0) - camgi*Math.pow(this.medium.H.getConcentration(),2.0)));
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
}
