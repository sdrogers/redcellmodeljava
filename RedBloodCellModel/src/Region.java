// Class to represent a cell or a membrane
public class Region {
	public Species Na;
	public Species K;
	public Species A;
	public Species H;
	public Species Hb;
	public Species X;
	public Species Os;
	public Species Gluconate;
	public Species Glucamine;
	public Species Sucrose;
	public Species Caf;
	public Species Mgf;
	public Species Cat;
	public Species Mgt;
	public Species XHbm;
	public Species COs;
	
	// concentration of charge on Hb
	public Species Hbpm;
	private Double pH;

	public Region() {
		this.Na = new Species();
		this.K = new Species();
		this.A = new Species();
		this.A.set_z(-1);
		this.H = new Species();
		this.Hb = new Species();
		this.X = new Species();
		this.Os = new Species();
		this.Gluconate = new Species();
		this.Glucamine = new Species();
		this.Sucrose = new Species();
		this.Caf = new Species();
		this.Mgf = new Species();
		this.Cat = new Species();
		this.Mgt = new Species();
		this.XHbm = new Species();
		this.COs = new Species();
		this.Hbpm = new Species();
		this.setpH(0.0);
	}
	

	public Double getpH() {
		return pH;
	}

	public void setpH(Double pH) {
		this.pH = pH;
	}
	
	public Double getAmountFromString(String species) {
		switch(species) {
			case "Na": return this.Na.getAmount();
			case "K": return this.K.getAmount();
			case "A": return this.A.getAmount(); 
			case "H": return this.H.getAmount();
			case "Hb": return this.Hb.getAmount();
			case "X": return this.X.getAmount();
			case "Os": return this.Os.getAmount();
			case "Gluconate": return this.Gluconate.getAmount();
			case "Glucamine": return this.Glucamine.getAmount();
			case "Sucrose": return this.Sucrose.getAmount();
			case "Caf": return this.Caf.getAmount();
			case "Mgf": return this.Mgf.getAmount();
			case "Cat": return this.Cat.getAmount();
			case "Mgt": return this.Mgt.getAmount();
			case "XHbm": return this.XHbm.getAmount();
			case "COs": return this.COs.getAmount();
			case "Hbpm": return this.Hbpm.getAmount();
		}
		return -1.0;
	}
	
	public Double getConcentrationFromString(String species) {
		switch(species) {
			case "Na": return this.Na.getConcentration();
			case "K": return this.K.getConcentration();
			case "A": return this.A.getConcentration(); 
			case "H": return this.H.getConcentration();
			case "Hb": return this.Hb.getConcentration();
			case "X": return this.X.getConcentration();
			case "Os": return this.Os.getConcentration();
			case "Gluconate": return this.Gluconate.getConcentration();
			case "Glucamine": return this.Glucamine.getConcentration();
			case "Sucrose": return this.Sucrose.getConcentration();
			case "Caf": return this.Caf.getConcentration();
			case "Mgf": return this.Mgf.getConcentration();
			case "Cat": return this.Cat.getConcentration();
			case "Mgt": return this.Mgt.getConcentration();
			case "XHbm": return this.XHbm.getConcentration();
			case "COs": return this.COs.getConcentration();
			case "Hbpm": return this.Hbpm.getConcentration();
		}
		return -1.0;
	}
}
