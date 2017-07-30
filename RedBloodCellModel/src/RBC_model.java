import java.util.HashMap;
import java.util.ArrayList;

public class RBC_model {
	private Region cell;
	private Region medium;
	private JacobsStewart JS;
	private Cotransport cotransport;
	private NaPump napump;
	private CarrierMediated carriermediated;
	private Goldman goldman;
	private A23187 a23;
	private WaterFlux waterflux;
	private PassiveCa passiveca;
	private CaPumpMg2 capump;
	private Boolean debug = true;
	
	private Double A_1;
	private Double A_2;
	private Double A_3;
	// private Double A_4 = 7.2;
	private Double pit0;
	private Double A_5;
	private Double integration_interval_factor; // Integration factor
	private Double A_7;
	private Double A_8;
	// private Double A_9 = 0.0;
	private Double hb_content;
	private Double A_10;
	private Double A_11;
	private Double A_12;

	//# self.B_1 = 0.2
	//# self.B_2 = 18.0
	//# self.B_3 = 0.1
	//# self.B_4 = 8.3
	//# self.B_5 = 0.0
	//# fG is proportion of flux going through G (B_6) - only used in RS
	private Double fG;
	//# B_7 is due to the ratio in the Na pump - for every 3 Na out, 2 K come in (B_7)
	private Double Na_to_K;
	//# self.B_8 = 37.0
	private Double temp_celsius;
	//# self.B_9 = 4.0
	private Double B_10;
	
	private Double delta_H;
	private	Double D_6;
	private Double D_12;
	
	
	private Double total_flux_Na;
	private Double total_flux_K;
	private Double total_flux_A;
	private Double total_flux_H;
	private Double napmaxf;
//	private Double napump.flux_rev = 0.;
	private Double napmaxr;
	
	
	private Double I_3;
	private Double I_6;
	private Double I_9;
	private Double I_11;
	private Double I_12;
	
	
	private Double I_18;
	private Double I_73;
	private Double I_77;
	private Double I_78;
	private Double I_79; 
	
	private Double buffer_conc;
	private Double Q_4;
	
	
	private Double Q_8;
	private Double R;

	private Double sampling_time;
	private Double cycle_count;
	private Integer cycles_per_print;
	private Double duration_experiment;
	private Integer n_its;
	private Double T_6;
	
	private Double Vw;
	private Double VV;
	private Double fraction;
	private Double mchc;
	private Double density;
	private Double Em;
	private Double rA;
	private Double rH;
	private Double fHb;
	private Double nHb;
	
	
	private Double X_6;
	private Double total_flux;
	private Double diff2;
	private Double diff3;
	private Double dedgh;
	private Double ligchoice;
	private Double edghk1;
	private Double edghk2;
	private Double edgcak;
	private Double edgmgk;
	private Double edg4;
	private Double edg3;
	private Double edg2;
	private Double edgmg;
	private Double edgca;
	private Double edgto;
	private Double edgneg;
	private Double edghnew;
	private Double edghold;
	
	
	private Double mgb;
	private Double mgb0;
	private Double mgcao;
	private Double mgcai;
//	private Double cell.Mgt.amount;
	private Double camgi;
	private Double camgo;
	private Double cab;
	private Double cabb;
//	private Double cell.Cat.concentration;
	private Double total_flux_Ca;
	
	
	private Double pka;
	private Double pkhepes;
	private Double benz2;
	private Double benz2k;
	private Double cbenz2;
	private Double b1ca;
	private Double b1cak;
	private Double alpha;
	private Double atp;
	private Double dpgp;
	private String BufferType;
	private Double vlysis;
	// is gca ever used?
	private Double gca;
	private Double ff;
	private Double delta_Mg;
	private Double delta_Ca;
	private Double q0;

	private Double I_74;
	private Double Z;
	private boolean RS_computed;
	private Integer stage;
	
	public RBC_model() {
		cell = new Region();
		medium = new Region();
		
		// Define the pumps
		JS = new JacobsStewart(cell,medium);
		cotransport = new Cotransport(cell,medium);
		napump = new NaPump(cell,medium);
		carriermediated = new CarrierMediated(cell,medium);
		goldman = new Goldman(cell,medium);
		a23 = new A23187(cell,medium);
		waterflux = new WaterFlux(cell,medium);
		passiveca = new PassiveCa(cell,medium,goldman);
		capump = new CaPumpMg2(cell,medium,napump);
		
		A_1 = -10.0;
		A_2 = 0.0645;
		A_3 = 0.0258;
		// private Double A_4 = 7.2;
		pit0 = 7.2;
		A_5 = 2.813822508658947e-08;
		integration_interval_factor = 0.01; // Integration factor
		A_7 = 0.0;
		A_8 = 0.0;
		// private Double A_9 = 0.0;
		hb_content = 34.0;
		A_10 = 0.0;
		A_11 = 0.0;
		A_12 = 0.0;

		//# self.B_1 = 0.2
		//# self.B_2 = 18.0
		//# self.B_3 = 0.1
		//# self.B_4 = 8.3
		//# self.B_5 = 0.0
		//# fG is proportion of flux going through G (B_6) - only used in RS
		fG = 0.1;
		//# B_7 is due to the ratio in the Na pump - for every 3 Na out, 2 K come in (B_7)
		Na_to_K = 1.5;
		//# self.B_8 = 37.0
		temp_celsius = 37.0;
		//# self.B_9 = 4.0
		B_10 = 2.0;
		
		
		
		//# self.C_1 = 10.0
		this.cell.Na.setConcentration(10.0);
		//# self.C_2 = 140.0
		this.cell.K.setConcentration(140.0);
		//# self.C_3 = 95.0
		this.cell.A.setConcentration(95.0);
		//# self.C_4 = 0.0
		this.cell.H.setConcentration(0.0);
		//# self.C_5 = 0.0
		this.cell.Hb.setConcentration(0.0);
		//# self.C_6 = 0.0
		this.cell.X.setConcentration(0.0);
		//# self.C_7 = 0.0
		this.cell.Mgt.setConcentration(0.0);
		this.cell.XHbm.setConcentration(0.0);
		//# self.C_9 = 0.0
		//# self.cell.COs.concentration
		//# self.C_10 = 0.0
		//# self.cell.Hbpm.concentration
		
		this.delta_H = 0.0;
		this.D_6 = 0.001;
		this.D_12 =  0.0001;
		
		this.total_flux_Na = 0.0;
		this.total_flux_K = 0.0;
		this.total_flux_A = 0.0;
		this.total_flux_H = 0.0;
		this.napmaxf = 1.0;
		this.napump.setFluxRev(0.0015);
		this.napmaxr = 1.0;
		
		this.I_3 = 0.0;
		this.I_6 = 0.0;
		this.I_9 = 0.0;
		this.I_11 = 0.0;
		this.I_12 = 0.0;
		
		
		this.I_18 = 0.0;
		this.I_73 = 0.0;
		this.I_77 = 7.216;
		this.I_78 = 0.4;
		this.I_79 = 0.75;
		
		this.medium.Na.setConcentration(145.0);
		this.medium.K.setConcentration(5.0);
		this.medium.A.setConcentration(145.0);
		this.medium.H.setConcentration(0.0);
		this.buffer_conc = 10.0;
		this.medium.Hb.setConcentration(0.0);

		this.cell.Na.setAmount(0.0);
		this.cell.K.setAmount(0.0);
		this.cell.A.setAmount(0.0);
		this.Q_4 = 0.0;
		this.cell.Hb.setAmount(0.0);
		this.cell.X.setAmount(0.0);
		this.cell.Mgt.setAmount(0.0);
		
		
		
		this.Q_8 = 0.0;
		this.R = 0.0; // Check the type

		this.sampling_time = 0.0;
		this.cycle_count = 0.0;
		this.cycles_per_print = 777;
		this.duration_experiment = 0.0;
		this.n_its = 0;
		this.T_6 = 0.0;
		
		this.Vw = 0.0;
		this.VV = 0.0;
		this.fraction = 0.0;
		this.mchc = 0.0;
		this.density = 0.0;
		this.Em = 0.0;
		this.rA = 0.0;
		this.rH = 0.0;
		this.fHb = 0.0;
		this.nHb = 0.0;
		
		this.X_6 = 0.0;
		this.total_flux = 0.0;
		this.diff2 = 0.00001;
		this.diff3 = 0.00001;
		this.dedgh = 0.0;
		this.ligchoice = 0.0;
		this.edghk1 = 0.0;
		this.edghk2 = 0.0;
		this.edgcak = 0.0;
		this.edgmgk = 0.0;
		this.edg4 = 0.0;
		this.edg3 = 0.0;
		this.edg2 = 0.0;
		this.edgmg = 0.0;
		this.edgca = 0.0;
		this.edgto = 0.0;
		this.edgneg = 0.0;
		this.edghnew = 0.0;
		this.edghold = 0.0;
		
		
		this.mgb = 0.0;
		this.mgb0 = 0.0;
		this.mgcao = 0.0;
		this.mgcai = 0.0;
		this.cell.Mgt.setAmount(2.5);
		this.camgi = 0.0;
		this.camgo = 0.0;
		this.cab = 0.0;
		this.cabb = 0.0;
		this.cell.Cat.setConcentration(0.0);
		this.total_flux_Ca = 0.0;
		
		
		this.pka = 0.0;
		this.pkhepes = 0.0;
		this.benz2 = 0.0;
		this.benz2k = 0.0;
		this.cbenz2 = 0.0;
		this.b1ca = 0.0;
		this.b1cak = 0.0;
		this.alpha = 0.0;
		this.atp = 1.2;
		this.dpgp = 0.0;
		this.BufferType = "HEPES";
		this.vlysis = 1.45;
		this.gca = 0.0;
		this.ff = 0.0;
		this.delta_Mg = 0.0;
		this.delta_Ca = 0.0;


	}
	
	public void setup(HashMap<String,String> rsoptions) {
		ArrayList<String> usedoptions = new ArrayList<String>();
		this.naPumpScreenRS(rsoptions,usedoptions);
		this.cellwaterscreenRS(rsoptions, usedoptions);
		this.cellanionprotonscreenRS(rsoptions, usedoptions);
		this.chargeandpiscreenRS(rsoptions, usedoptions);
		
		this.cycles_per_print = 777;
		this.Vw = this.I_79;
		this.fraction = 0.000001;
		this.medium.setpH(7.4);
		this.A_12 = this.medium.getpH();
		this.A_11 = 1.0-this.hb_content/136.0;
		this.R = 1.0; // check type
		this.sampling_time = 0.0;
		
		
		this.setmgdefaults();
		this.setcadefaults();
		
		this.mgbufferscreenRS(rsoptions, usedoptions);
		this.cabufferscreenRS(rsoptions, usedoptions);
		
		this.computeRS();
		
		System.out.println("Used RS options");
		for(String option: usedoptions) {
			System.out.println(option);
		}
		
		System.out.println();
		System.out.println("Unused RS options");
		for(String option: rsoptions.keySet()) {
			if(!usedoptions.contains(option)) {
				System.out.println(option);
			}
		}
	}
	
	public void computeRS() {
		this.medium.A.setConcentration(this.medium.A.getConcentration() + 2*(this.medium.Mgt.getConcentration() + this.medium.Cat.getConcentration()));
		this.computehtRS();
		this.A_7 = this.fraction;
		this.A_8 = this.A_7/(1-this.A_7);
		
		this.mediumconcentrationsRS();
		this.cellphetc();
		
		this.nakamountsmgcaconcRS();

		// Computes Q_X
		this.secureisonoticityRS();
		
		// Computes n_X
		// Non-protonizable charge on X (nX)
		this.A_10 = (this.cell.A.getAmount() + 2*this.benz2 - (this.cell.Na.getAmount() + this.cell.K.getAmount() + 2*this.cell.Mgt.getAmount() + 2*this.cell.Cat.getAmount()+this.nHb*this.cell.Hb.getAmount()))/this.cell.X.getAmount();

		// Net charge on Hb
		this.Q_4 = this.nHb*this.cell.Hb.getAmount();

		this.fluxesRS();
		
		this.R = 0.0;
		this.cycle_count = 0.0;
		this.n_its = 0;
		this.Z = 0.0;
		this.duration_experiment = 0.0;

		this.RS_computed = true;

		// Set stage to zero everytime the RS is computed - stage = 1 means we're about to start DS 1
		this.stage = 1;
	}
	
	private void fluxesRS() {
		// Flux-rates and RS fluxes
		this.napump.compute_permeabilities(this.temp_celsius);

		this.carriermediated.setFlux_Na(-(1-this.fG)*this.napump.getFlux_net());
		this.carriermediated.setFlux_K(-this.carriermediated.getFlux_Na()/this.Na_to_K);
		this.carriermediated.compute_permeabilities();

		// A fluxes through Na:Cl and K:Cl
		Double fal = this.carriermediated.getFlux_Na() + this.carriermediated.getFlux_K();
		// G-flux of A required to balance fal
		this.goldman.setFlux_A(-fal);
		// G-rates and RS fluxes
		this.goldman.setFlux_Na(-this.fG*this.napump.getFlux_net());
		this.goldman.setFlux_K(-this.goldman.getFlux_Na()/this.Na_to_K);
		this.I_18 = 1.0;
		this.goldman.compute_permeabilities(this.Em,this.temp_celsius);

		// Zero-factor for cotransport
		this.cotransport.compute_zero_factor();
		this.X_6 = this.Vw;
		// this.cotransportmediatedfluxes()
		this.cotransport.compute_flux(this.I_18);
		this.totalionfluxes();

		this.Q_8 = this.fHb*this.cell.Hb.getAmount();

		this.chbetc();
	}
	
	private void totalionfluxes() {
		// Total ion fluxes
		// Na flux
		this.total_flux_Na = this.napump.getFlux_net() + this.carriermediated.getFlux_Na() + this.goldman.getFlux_Na() + this.cotransport.getFlux_Na();
		// K flux
		this.total_flux_K = this.napump.getFlux_K() + this.carriermediated.getFlux_K() + this.goldman.getFlux_K() + this.cotransport.getFlux_K();
		// Anion flux
		this.total_flux_A = this.goldman.getFlux_A() + this.cotransport.getFlux_A() + this.JS.getFlux_A() + this.carriermediated.getFlux_Na() + this.carriermediated.getFlux_K();
		// Net proton flux, includes H-flux through Ca pump
		this.total_flux_H = this.JS.getFlux_H() + this.goldman.getFlux_H() - 2*this.a23.getFlux_Mg()-2*this.a23.getFlux_Ca()+this.capump.getFlux_H();
	}
	
	private void chbetc() {
		this.cell.Hb.setConcentration(this.cell.Hb.getAmount()/this.Vw);
		this.cell.Mgt.setConcentration(this.cell.Mgt.getAmount()/this.Vw);
		this.cell.X.setConcentration(this.cell.X.getAmount()/this.Vw);
		this.cell.XHbm.setAmount(this.Q_4 + this.A_10*this.cell.X.getAmount() - 2*this.benz2);
		this.cell.XHbm.setConcentration(this.cell.XHbm.getAmount()/this.Vw);
		this.cell.COs.setConcentration(this.fHb*this.cell.Hb.getAmount()/this.Vw);
		// Concentration of charge on Hb
		this.cell.Hbpm.setConcentration(this.nHb*this.cell.Hb.getAmount()/this.Vw);

		// This line doesn't seem to ever be used?
		// this.I_12 = this.cell.Na.getConcentration() + this.cell.K.getConcentration() + this.cell.A.getConcentration() + this.fHb*this.cell.Hb.getConcentration() + this.cell.X.getConcentration() + this.mgf + this.caf + this.cbenz2

		// Sum M
		this.medium.Os.setConcentration(this.medium.Na.getConcentration()+ this.medium.K.getConcentration() + this.medium.A.getConcentration() + this.buffer_conc + this.medium.Gluconate.getConcentration() + this.medium.Glucamine.getConcentration() + this.medium.Sucrose.getConcentration() + (this.medium.Mgf.getConcentration() + this.medium.Caf.getConcentration() + this.edgto));
		this.cell.Os.setAmount(this.cell.Na.getAmount() + this.cell.K.getAmount() + this.cell.A.getAmount() + this.fHb*this.cell.Hb.getAmount() + this.cell.X.getAmount() + this.cell.Mgt.getAmount() + (this.cell.Mgf.getConcentration()+this.cell.Caf.getConcentration())*this.Vw + this.benz2);
	}
	
	private void secureisonoticityRS() {
		// ecures initial isotonicity and electroneutrality; it computes the
		// QX and nX required for initial osmotic and charge balance.  Since the Mg and
		// Ca buffers are within X, only the unbound forms of Mg and Ca participate in
		// osmotic equilibria within the cell.
		Double summ = this.medium.Na.getConcentration() + this.medium.K.getConcentration() + this.medium.A.getConcentration() + this.buffer_conc + this.medium.Gluconate.getConcentration() + this.medium.Glucamine.getConcentration() + this.medium.Sucrose.getConcentration() + this.medium.Mgt.getConcentration() + this.medium.Cat.getConcentration();
		Double sumq = this.cell.Na.getAmount() + this.cell.K.getAmount() + this.cell.A.getAmount() + (this.cell.Mgf.getConcentration()+this.cell.Caf.getConcentration())*this.Vw + this.fHb*this.cell.Hb.getAmount() + this.benz2;
		this.cell.X.setAmount(this.Vw*summ - sumq);

	}
	
	private void nakamountsmgcaconcRS() {
		// Cell amounts of Na,K,and A, and concentrations of Mg and Ca
		this.cell.Na.setAmount(this.cell.Na.getConcentration()*this.Vw);
		this.cell.K.setAmount(this.cell.K.getConcentration()*this.Vw);
		this.cell.A.setAmount(this.cell.A.getConcentration()*this.Vw);
		this.cell.Mgt.setConcentration(this.cell.Mgt.getAmount()/this.Vw);
		this.cell.Cat.setConcentration(this.cell.Cat.getAmount()/this.Vw);
	}
	
	private void cellphetc() {
		this.rA = this.medium.A.getConcentration()/this.cell.A.getConcentration();
		this.rH = this.rA;
		this.cell.H.setConcentration(this.rH*this.medium.H.getConcentration());
		this.cell.setpH(-Math.log10(this.cell.H.getConcentration()));
		this.Em = -(8.615600000000004e-02)*(273 + this.temp_celsius)*Math.log(this.rA);

		// Osmotic coeficient of Hb
		this.fHb = 1 + Math.pow(this.A_2*this.cell.Hb.getAmount()/this.Vw + this.A_3*(this.cell.Hb.getAmount()/this.Vw),2.0);
		// physiological pI at 37oC;
		this.I_74 = this.pit0 - (0.016*37);
		// net charge on Hb (Eq/mole)
		this.nHb = this.A_1*(this.cell.getpH() - this.I_74);
	}
	
	
	private void mediumconcentrationsRS() {
		this.BufferType = "HEPES";
		this.medium.H.setConcentration(Math.pow(10.0,-this.medium.getpH()));
		this.pkhepes = 7.83 - 0.014*this.temp_celsius;
		this.A_5 = Math.pow(10.0,-this.pkhepes);
		this.medium.Hb.setConcentration(this.buffer_conc*(this.medium.H.getConcentration()/(this.A_5+this.medium.H.getConcentration())));
		// Medium Na,K,or A concentration
		if(this.medium.getpH() >= this.A_12) {
			this.medium.Na.setConcentration(this.medium.A.getConcentration() + this.edgneg + this.medium.Gluconate.getConcentration() + (this.buffer_conc - this.medium.Hb.getConcentration()) - this.medium.Glucamine.getConcentration() - this.medium.K.getConcentration() - 2*this.medium.Mgf.getConcentration() - 2*this.medium.Caf.getConcentration());
		}else {
			this.medium.K.setConcentration(this.medium.A.getConcentration() + this.edgneg + this.medium.Gluconate.getConcentration() + (this.buffer_conc - this.medium.Hb.getConcentration()) - this.medium.Glucamine.getConcentration() - this.medium.Na.getConcentration() - 2*this.medium.Mgf.getConcentration() - 2*this.medium.Caf.getConcentration());
		}

	}
	
	public void computehtRS() {
		this.VV = (1-this.A_11) + this.Vw;
		this.mchc = this.hb_content/this.VV;
		this.density = (this.hb_content/100.0 + this.Vw)/this.VV;
	}
	
	public void naPumpScreenRS(HashMap<String,String> rsoptions,ArrayList<String> usedoptions) {
		String na_efflux_fwd = rsoptions.get("na-efflux-fwd");
		if(na_efflux_fwd != null) {
			this.napump.setFluxFwd(Double.parseDouble(na_efflux_fwd));
			usedoptions.add("na-efflux-fwd");
		}
		
		if(this.napump.getFluxFwd() == -2.61) {
			this.napmaxf = 1.0;
		}else {
			this.napmaxf = 0.0;
		}
		
		String na_efflux_rev = rsoptions.get("na-efflux-rev");
		if(na_efflux_rev != null) {
			this.napump.setFluxRev(Double.parseDouble(na_efflux_rev));
			usedoptions.add("na-efflux-rev");
		}
		
		if(this.napump.getFluxRev() == 0.0015) {
			this.napmaxr = 1.0;
		}else {
			this.napmaxr = 0.0;
		}
		
		// Other code to be added here...
	}
	public void cellwaterscreenRS(HashMap<String,String> rsoptions,ArrayList<String> usedoptions) {
		String hb_content_str = rsoptions.get("hb-content");
		if(hb_content_str != null) {
			usedoptions.add("hb-content");
			this.hb_content = Double.parseDouble(hb_content_str);
			this.cell.Hb.setAmount(this.hb_content * 10.0/64.5);
			this.I_79 = 1.0 - this.hb_content/136.0;
			this.vlysis = 1.45;
			if(this.hb_content == 34.0) {
				String temp = rsoptions.get("cell-water");
				if(temp != null) {
					this.I_79 = Double.parseDouble(temp);
					usedoptions.add("cell-water");
				}
				temp = rsoptions.get("lytic-cell-water");
				if(temp != null) {
					this.vlysis = Double.parseDouble(temp);
					usedoptions.add("lytic-cell-water");
				}
				
			}
			
		}
	}
	
	public void cellanionprotonscreenRS(HashMap<String,String> rsoptions,ArrayList<String> usedoptions) {
		String temp = rsoptions.get("cla-conc");
		if(temp != null) {
			this.cell.A.setConcentration(Double.parseDouble(temp));
			usedoptions.add("cla=conc");
		}
	}
	
	public void chargeandpiscreenRS(HashMap<String,String> rsoptions,ArrayList<String> usedoptions) {
		String temp = rsoptions.get("a");
		if(temp != null) {
			this.A_1 = Double.parseDouble(temp);
			usedoptions.add("a");
		}
		temp = rsoptions.get("pi");
		if(temp != null) {
			this.pit0 = Double.parseDouble(temp);
			usedoptions.add("pi");
		}
	}
	
	public void setmgdefaults() {
		this.cell.Mgt.setAmount(2.5);
		this.medium.Mgt.setConcentration(0.2);
		
		this.q0 = 0.05;
		this.atp = 1.2;
		this.dpgp = 15.0;
		this.VV = (1.0 - this.A_11) + this.Vw;
		
		Double conc = this.newton_raphson(new Eqmg(), 0.02, 0.0001, 0.00001);
//		System.out.println(conc);
		this.cell.Mgf.setConcentration(conc);
		
		
	}
	
	public void setcadefaults() {
		this.cell.Cat.setAmount(0.000580);
		this.cell.Cat.setConcentration(this.cell.Cat.getAmount()/this.Vw);
		this.medium.Cat.setConcentration(1.0);
		this.alpha = 0.30;
		this.b1ca = 0.026;
		this.b1cak = 0.014;
		this.benz2 = 0.0;
		this.benz2k = 5e-5;
		this.cell.Caf.setConcentration(1.12e-4);
		
		this.edgto = 0.0;
		this.medium.Mgf.setConcentration(this.medium.Mgt.getConcentration());
		this.medium.Caf.setConcentration(this.medium.Cat.getConcentration());
	}
	
	
	private void mgbufferscreenRS(HashMap<String,String> rsoptions, ArrayList<String> usedoptions) {
		String temp = rsoptions.get("mgot-medium");
		if(temp != null) {
			this.medium.Mgt.setConcentration(Double.parseDouble(temp));
			usedoptions.add("mgot-medium");
		} else {
			this.medium.Mgt.setConcentration(0.2);
		}
		
		
		temp = rsoptions.get("mgit");
		if(temp != null) {
			this.cell.Mgt.setAmount(Double.parseDouble(temp));
			usedoptions.add("mgit");
		} else {
			this.cell.Mgt.setAmount(2.5);
		}
		
		
		temp = rsoptions.get("hab");
		if(temp != null) {
			this.mgb0 = Double.parseDouble(temp);
			usedoptions.add("hab");
		} else {
			this.mgb0 = 0.05;
		}
		
		
		temp = rsoptions.get("atpp");
		if(temp != null) {
			this.atp = Double.parseDouble(temp);
			usedoptions.add("atpp");
		} else {
			this.atp = 1.2;
		}
		
		temp = rsoptions.get("23dpg");
		if(temp != null) {
			this.dpgp = Double.parseDouble(temp);
			usedoptions.add("23dpg");
		} else {
			this.dpgp = 15.0;
		}
		
		Double conc = this.newton_raphson(new Eqmg(), 0.02, 0.0001, 0.00001);
//		System.out.println(conc);
		this.cell.Mgf.setConcentration(conc);
		
	}
	
	private void cabufferscreenRS(HashMap<String,String> rsoptions, ArrayList<String> usedoptions) {
		String temp = rsoptions.get("caot-medium");
		if(temp != null) {
			this.medium.Cat.setConcentration(Double.parseDouble(temp));
			usedoptions.add("caot-medium");
		} else {
			this.medium.Cat.setConcentration(1.0);
		}
		
		temp = rsoptions.get("add-ca-buffer");
		if(temp != null) {
			this.b1ca = Double.parseDouble(temp);
			usedoptions.add("add-ca-buffer");
		} else {
			this.b1ca = 0.026;
		}
		
		temp = rsoptions.get("kd-of-ca-buffer");
		if(temp != null) {
			this.b1cak = Double.parseDouble(temp);
			usedoptions.add("kd-of-ca-buffer");
		} else {
			this.b1cak = 0.014;
		}

		temp = rsoptions.get("alpha");
		if(temp != null) {
			this.alpha = Double.parseDouble(temp);
			usedoptions.add("alpha");
		} else {
			this.alpha = 0.3;
		}

		temp = rsoptions.get("benz2loaded");
		if(temp != null) {
			this.benz2 = Double.parseDouble(temp);
			usedoptions.add("benz2loaded");
		} else {
			this.benz2 = 0.3;
		}
		this.cbenz2 = this.benz2/this.Vw;

		temp = rsoptions.get("vmax-cap");
		if(temp != null) {
			this.capump.setFcapm(Double.parseDouble(temp));
			usedoptions.add("vmax-cap");
		} else {
			this.capump.setFcapm(12.0);
		}

		temp = rsoptions.get("k1/2");
		if(temp != null) {
			this.capump.setCapk(Double.parseDouble(temp));
			usedoptions.add("k1/2");
		} else {
			this.capump.setCapk(2e-4);
		}
		
		temp = rsoptions.get("hills");
		if(temp != null) {
			this.capump.setH1(Double.parseDouble(temp));
			usedoptions.add("hills");
		} else {
			this.capump.setH1(4.0);
		}
		
		temp = rsoptions.get("pump-electro");
		Integer capstoich;
		if(temp != null) {
			capstoich = Integer.parseInt(temp);
			usedoptions.add("pump-electro");
		} else {
			capstoich = 0;
		}
		this.capump.setCah(2-capstoich);
		
		temp = rsoptions.get("h+ki");
		if(temp != null) {
			this.capump.setHik(Double.parseDouble(temp));
			usedoptions.add("h+ki");
		} else {
			this.capump.setHik(4e-7);
		}
		
		temp = rsoptions.get("Mg2+K1/2");
		if(temp != null) {
			this.capump.setCapmgk(Double.parseDouble(temp));
			usedoptions.add("Mg2+K1/2");
		} else {
			this.capump.setCapmgk(0.1);
		}
		
		temp = rsoptions.get("pmax-pcag");
		if(temp != null) {
			this.passiveca.setFcalm(Double.parseDouble(temp));
			usedoptions.add("pmax-pcag");
		} else {
			this.passiveca.setFcalm(0.05);
		}
		
		
		temp = rsoptions.get("ca2+-pkmax");
		if(temp != null) {
			this.goldman.setPkm(Double.parseDouble(temp));
			usedoptions.add("ca2+-pkmax");
		} else {
			this.goldman.setPkm(1e-2);
		}
		
		
		temp = rsoptions.get("ca2+-pkca");
		if(temp != null) {
			this.goldman.setPkcak(Double.parseDouble(temp));
			usedoptions.add("ca2+-pkca");
		} else {
			this.goldman.setPkcak(1e-2);
		}
		
		if(this.benz2 != 0) {
			this.cell.Caf.setConcentration(1e-8);
			this.canr();
		}
		
		
	}
	
	private void canr() {
		this.cell.Caf.setConcentration(1000.0*this.cell.Caf.getConcentration());
		this.cell.Cat.setAmount(1000.0*this.cell.Cat.getAmount());
		this.b1ca = this.b1ca * 1000.0;
		this.b1cak = this.b1cak * 1000.0;
		this.benz2 = this.benz2 * 1000.0;
		this.benz2k = this.benz2k * 1000.0;
		
		Double conc = this.newton_raphson(new Eqca(),this.cell.Caf.getConcentration(),0.000001, 0.000001);
		this.cell.Caf.setConcentration(conc);
		
		this.cell.Caf.setConcentration(this.cell.Caf.getConcentration()/1000.0);
		this.cell.Cat.setAmount(this.cell.Cat.getAmount()/1000.0);
		this.b1ca=this.b1ca/1000.0;
		this.b1cak=this.b1cak/1000.0;
		this.benz2=this.benz2/1000.0;
		this.benz2k=this.benz2k/1000.0;
	}
	
	private class Eqca implements NWRunner {
		public Double run(Double local_x6) {
			cabb = local_x6*(Math.pow(alpha,-1.0))+((b1ca/VV)*local_x6/(b1cak+local_x6))+((benz2/VV)*local_x6/(benz2k+local_x6));
			Double y = cell.Cat.getAmount() - cabb;
			return y;
		}
	}
	
	private class Eqmg implements NWRunner {
		public Double run(Double local_mgf) {
			mgb = mgb0 + ((atp/VV)*local_mgf/(0.08+local_mgf)) + ((dpgp/VV)*local_mgf/(3.6+local_mgf));
			Double y = cell.Mgt.getAmount() - local_mgf*(Vw/(Vw+hb_content/136.0)) - mgb;
			return y;
		}
	}
	private Double newton_raphson(NWRunner r, Double initial, Double step, Double stop) {
		int max_its = 100;
//		Double step = 0.001;
//		Double stop = 0.0001;
		int initial_its = 0;
		Double X_3 = initial;
		int no_its = initial_its;
		Boolean finished = false;
		while(!finished) {
			Double X_1 = X_3 - step;
			Double Y_1 = r.run(X_1);
			Double X_2 = X_3 + step;
			Double Y_2 = r.run(X_2);
			Double S = (Y_2 - Y_1) / (X_2 - X_1);
			X_3 = X_1 - (Y_1/S);
//			System.out.println(Math.abs(Y_2));
			// Note the bit here that isn't copied...
			Double Y_3 = Y_2;
			no_its++;
			if(no_its > max_its) {
				finished = true;
			}
			if(Math.abs(Y_3) < stop) {
				finished = true;
			}
		}
//		System.out.println(no_its);
		return X_3;
	}
}
