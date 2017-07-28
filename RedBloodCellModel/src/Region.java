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
	private Species Hbpm;
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
		this.pH = 0.0;
	}
	
	public Double getPh() {
		return this.pH;
	}
	
	
}
