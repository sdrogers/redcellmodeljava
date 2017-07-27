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
	public Species Glucomine;
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
		Na = new Species();
		K = new Species();
		A = new Species();
		A.set_z(-1);
		H = new Species();
		Hb = new Species();
		X = new Species();
		Os = new Species();
		Gluconate = new Species();
		Glucomine = new Species();
		Sucrose = new Species();
		Caf = new Species();
		Mgf = new Species();
		Cat = new Species();
		Mgt = new Species();
		XHbm = new Species();
		COs = new Species();
		Hbpm = new Species();
		pH = 0.0;
	}
	
	
}
