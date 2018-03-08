import java.util.HashMap;

import javax.swing.JTextArea;

import java.util.ArrayList;
import java.io.FileWriter;
import java.io.IOException;
public class RBC_model {
	
	private String[] publish_order = {"V/V","Vw","Hct","Em","pHi","pHo","MCHC",
	                                  "Density","QNa","QK","QA","QCa","QMg","CNa","CK","CA","CCa2+","CMg2+",
	                                  "CHb","CX","COs","rA","rH","fHb","nHb","MNa","MK","MA","MB","MCat","MCaf",
	                                  "FNaP","FCaP","FKP","FNa","FK","FA","FH","FW","FNaG","FKG","FAG","FHG"};
	
	private ArrayList<ResultHash> resultList = new ArrayList<ResultHash>();
	
	private Region cell;
	private Region medium;
	private JacobsStewart JS;
	private Cotransport cotransport;
	private NaPump napump;
	private CarrierMediated carriermediated;
	private Goldman goldman;
	private A23187 a23;
	private WaterFlux water;
	private PassiveCa passiveca;
	private CaPumpMg2 capump;
	private Boolean debug = true;
	
	private Double A_1;
	private Double A_2;
	private Double A_3;
	// private Double A_4 = 7.2;
	private Double pit0;
	private Double A_5;
	private boolean compute_delta_time = true; // Whether to compute this, or use a constant
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
	private int cycle_count;
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
	private int Z;
	private boolean RS_computed;
	private Integer stage;

	private Double I_43;

	private double I_72;

	private double I_40;

	private double I_33;

	private double I_45;

	private double I_46;

	private double I_34;

	private double lig_hb;

	private int it_counter;

	private Double I_67;

	private boolean finished;

	private boolean in_progress;

	private double delta_time = 0.1; // Initial value in case it is not computed

	private double delta_Na;

	private double delta_K;

	private double delta_A;

	private double delta_Water;
	
	public RBC_model() {
		cell = new Region();
		medium = new Region();
		
		// Define the pumps
		JS = new JacobsStewart(cell,medium);
		cotransport = new Cotransport(cell,medium);
		setNapump(new NaPump(cell,medium));
		carriermediated = new CarrierMediated(cell,medium);
		goldman = new Goldman(cell,medium);
		a23 = new A23187(cell,medium);
		water = new WaterFlux(cell,medium);
		passiveca = new PassiveCa(cell,medium,goldman);
		capump = new CaPumpMg2(cell,medium,getNapump());
		
		A_1 = -10.0; // Net charge on haemoglobin
		A_2 = 0.0645; // verial coefficients of osmotic coefficient of haemo (fhb)
		A_3 = 0.0258; // verial coefficients of osmotic coefficient of haemo (fhb)
		// private Double A_4 = 7.2;
		pit0 = 7.2; // iso-electric point of haemoglobin - constant
		A_5 = 2.813822508658947e-08; // dissociation constant of protein buffer in medium (KB)
		integration_interval_factor = 0.01; // Integration factor
		A_7 = 0.0; // Initial haematocrit
		A_8 = 0.0; // Haematocrit over 1-haematocrit
		// private Double A_9 = 0.0;
		hb_content = 34.0;
		A_10 = 0.0; // nX net charge on non-diffusible anion
		A_11 = 0.0; // Vwo initial water content of cell
		A_12 = 0.0; // pH in the medium at time 0

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
		B_10 = 2.0; // Q10L. Q10 - defines the number of times by which a dynamic process changes with temperature change of 10deg.
		// Q10L (B_10) the factor for leaks. Q10P is the factor for pumps 
		
		
		
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
		this.D_6 = 0.001; // Cord - for the NR
		this.D_12 =  0.0001; // NR delta
		
		this.total_flux_Na = 0.0;
		this.total_flux_K = 0.0;
		this.total_flux_A = 0.0;
		this.total_flux_H = 0.0;
		this.napmaxf = 1.0;
		this.getNapump().setFluxRev(0.0015);
		this.napmaxr = 1.0;
		
		// These are napump things
		this.I_3 = 0.0;
		this.I_6 = 0.0;
		this.I_9 = 0.0;
		this.I_11 = 0.0;
		// This is a co-transport thing
		this.I_12 = 0.0;
		
		
		this.I_18 = 0.0;
		this.I_73 = 0.0; // Exchange chloride for gluconate (in the medium)
		this.I_77 = 7.216; // Just in NaPump - remove
		this.I_78 = 0.4; // Just in NaPump - remove
		this.I_79 = 0.75; // Water volume (as proportion of total)
		
		this.medium.Na.setConcentration(145.0);
		this.medium.K.setConcentration(5.0);
		this.medium.A.setConcentration(145.0);
		this.medium.H.setConcentration(0.0);
		this.buffer_conc = 10.0;
		this.medium.Hb.setConcentration(0.0);

		this.cell.Na.setAmount(0.0);
		this.cell.K.setAmount(0.0);
		this.cell.A.setAmount(0.0);
		this.Q_4 = 0.0; // Q(Hb-): Qs are 'per litre of cells' - how much -ve charge does Hb provide per litre cells
		this.cell.Hb.setAmount(0.0);
		this.cell.X.setAmount(0.0);
		this.cell.Mgt.setAmount(0.0);
		
		
		
		this.Q_8 = 0.0; // Osmotic coefficient of haemoglobin - only used in the reference state
		this.R = 0.0; // Check the type

		this.sampling_time = 0.0;
		this.cycle_count = 0;
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
		this.edghk1 = 0.0; // chelators either edga or edta. edta bounds calcium & mg, edga bounds ca pref to mg
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
		
		
		this.mgb = 0.0; // mg buffering
		this.mgb0 = 0.0;
		this.mgcao = 0.0;
		this.mgcai = 0.0;
		this.cell.Mgt.setAmount(2.5);
		this.camgi = 0.0;
		this.camgo = 0.0;
		this.cab = 0.0; // ca buffering
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

		this.stage = 0;
	}
	public void output(String ad,JTextArea ta) {
		if(ta == null) {
			System.out.println(ad);
		}else {
			ta.append(ad + '\n');
		}
	}
	public void runall(JTextArea ta) {
		this.output("RUNNING DS STAGE " + this.stage, ta);
		this.output("Current Sampling time: " + 60.0*this.sampling_time,ta);
		this.output("Running until: " + this.duration_experiment,ta);
		
		this.finished = false;
		this.in_progress = true;
		
		this.cycle_count = 0;
		this.n_its = 0;
		this.Z = 0;
		this.output("Publishing at t=" + 60.0*this.sampling_time,ta);
		
		this.publish();
		while(this.sampling_time*60 <= this.duration_experiment) {
			this.getNapump().compute_flux(this.temp_celsius);
			// Temperature dependence of the passive fluxes, uses Q10L
			this.I_18 = Math.exp(((37.0-this.temp_celsius)/10.0)*Math.log(this.B_10));
			this.carriermediated.compute_flux(this.I_18);
			this.cotransport.compute_flux(this.I_18);
			this.JS.compute_flux(this.I_18);
			
			this.Em = this.newton_raphson(new compute_all_fluxes(), this.Em, 0.001, 0.0001, 100, 0, false);
			
			this.totalionfluxes();
			this.water.compute_flux(this.fHb, this.cbenz2, this.buffer_conc, this.edgto, this.I_18);
			this.integrationInterval(); // cycle_count is increased here
			this.computeDeltas();
			
			this.updateContents();
			
			this.cell.Cat.setConcentration(this.cell.Cat.getAmount()/this.Vw);
			this.cbenz2 = this.benz2/this.Vw;
			
			this.chbetc();
			
			this.rA = this.medium.A.getConcentration() / this.cell.A.getConcentration();
			this.rH = this.cell.H.getConcentration() / this.medium.H.getConcentration();
			if(this.Vw > this.vlysis) {
				// Cells have been lysed
				break;
			}
			
			if(this.I_73 > 0 && this.T_6 > 0) {
				if(this.sampling_time*60 > this.T_6) {
					this.medium.Gluconate.setConcentration(this.medium.Gluconate.getConcentration() - this.I_73);
					this.medium.A.setConcentration(this.medium.A.getConcentration() + this.I_73);
					this.I_73 = 0.0;
				}
			}
			
//			if(this.n_its == 2) {
//				this.Z += 1;
//				System.out.println("Publishing at t=" + 60.0*this.sampling_time);
//				this.publish();
//			}
			
			if(this.cycle_count == this.cycles_per_print) {
				this.Z += 1;
				this.output("Publishing at t=" + 60.0*this.sampling_time,ta);
				this.publish();
				this.cycle_count = 0;
			}
			
		
		}
		this.publish();
		this.output("Finished!",ta);
		
	}
	private void updateContents() {
		Double Vw_old = this.Vw;
		this.Vw = this.Vw + this.delta_Water;
		this.cell.Hb.setConcentration(this.cell.Hb.getAmount()/this.Vw);
		this.fHb = 1.0 + this.A_2*this.cell.Hb.getConcentration()+this.A_3*Math.pow(this.cell.Hb.getConcentration(),2);
		Double I_28 = this.fHb*this.cell.Hb.getAmount();
		this.cell.Na.setAmount(this.cell.Na.getAmount() + this.delta_Na);
		this.cell.K.setAmount(this.cell.K.getAmount() + this.delta_K);
		this.cell.A.setAmount(this.cell.A.getAmount() + this.delta_A);
		this.Q_4 = this.Q_4 + this.delta_H;

		this.nHb = this.Q_4/this.cell.Hb.getAmount();
		this.cell.Mgt.setAmount(this.cell.Mgt.getAmount()+this.delta_Mg);
		this.cell.Cat.setAmount(this.cell.Cat.getAmount() + this.delta_Ca);
		this.gca = this.cell.Cat.getAmount();

		// Cell pH and cell proton concentration
		this.cell.setpH(this.I_74 + this.nHb/this.A_1);
		this.cell.H.setConcentration(Math.pow(10,(-this.cell.getpH())));
		this.nHb = this.A_1*(this.cell.getpH()-this.I_74);
		this.Q_8 = I_28;
		this.VV = (1-this.A_11) + this.Vw;
		this.mchc = this.hb_content/this.VV;
		this.density = (this.hb_content/100 + this.Vw)/this.VV;
		this.fraction = this.A_7*this.VV;

		// External concentrations
		Double I_30 = 1 + (this.Vw-Vw_old)*this.A_8;
		this.medium.Na.setConcentration(this.medium.Na.getConcentration()*I_30 - this.delta_Na*this.A_8);
		this.medium.K.setConcentration(this.medium.K.getConcentration()*I_30 - this.delta_K*this.A_8);
		this.medium.A.setConcentration(this.medium.A.getConcentration()*I_30 - this.delta_A*this.A_8);
		this.medium.Gluconate.setConcentration(this.medium.Gluconate.getConcentration()*I_30);
		this.medium.Glucamine.setConcentration(this.medium.Glucamine.getConcentration()*I_30);
		this.medium.Sucrose.setConcentration(this.medium.Sucrose.getConcentration()*I_30);
		this.buffer_conc = this.buffer_conc*I_30;
		this.medium.Hb.setConcentration(this.medium.Hb.getConcentration()*I_30);

		// Medium proton, Ca2+, Mg2+, free and bound buffer and ligand concentrations
		this.medium.Mgt.setConcentration(this.medium.Mgt.getConcentration()*I_30 - this.delta_Mg*this.A_8);
		this.medium.Cat.setConcentration(this.medium.Cat.getConcentration()*I_30 - this.delta_Ca*this.A_8);
		this.edgto = this.edgto*I_30;
		if(this.edgto == 0) {
			this.medium.Mgf.setConcentration(this.medium.Mgt.getConcentration());
			this.medium.Caf.setConcentration(this.medium.Cat.getConcentration());
		}
		if(this.ligchoice != 0) {
			this.edgta();
		}else {
			this.medium.Hb.setConcentration(this.medium.Hb.getConcentration()*I_30 - this.delta_H*this.A_8);
			this.medium.H.setConcentration(this.A_5*(this.medium.Hb.getConcentration()/(this.buffer_conc-this.medium.Hb.getConcentration())));

			this.medium.setpH(-Math.log(this.medium.H.getConcentration())/Math.log(10.0));
		}

		// Cell concentrations and external concentrations
		this.cell.Na.setConcentration(this.cell.Na.getAmount()/this.Vw);
		this.cell.K.setConcentration(this.cell.K.getAmount()/this.Vw);
		this.cell.A.setConcentration(this.cell.A.getAmount()/this.Vw);

		// compute mgf
		this.cell.Mgf.setConcentration(this.newton_raphson(new Eqmg(),0.02,0.0001,0.00001,100,0, false));
		// compute caf
		this.canr();
	}
	
	private void computeDeltas() {
		this.delta_Na = this.total_flux_Na*this.delta_time;
		this.delta_K = this.total_flux_K*this.delta_time;
		this.delta_A = this.total_flux_A*this.delta_time;
		this.delta_H = this.total_flux_H*this.delta_time;
		this.delta_Water = this.water.getFlux()*this.delta_time;
		this.delta_Mg = this.a23.getFlux_Mg()*this.delta_time;
		this.delta_Ca = this.total_flux_Ca*this.delta_time;
	}
	
	
	private void integrationInterval() {
		// 8010 Integration interval
		Double I_23 = 10.0 + 10.0*Math.abs(this.a23.getFlux_Mg()+this.total_flux_Ca) + Math.abs(this.goldman.getFlux_H()) + Math.abs(this.dedgh) + Math.abs(this.total_flux_Na) + Math.abs(this.total_flux_K) + Math.abs(this.total_flux_A) + Math.abs(this.total_flux_H) + Math.abs(this.water.getFlux()*100.0);
		if(this.compute_delta_time) {
			this.delta_time = this.integration_interval_factor/I_23;
		}else {
			// delta_time is a constant, but check if it's much bigger than calculated value
			// and issue a warning if it is
			double temp = this.integration_interval_factor/I_23;
			if(temp < this.delta_time) {
				System.out.println("WARNING: fixed delta_time is greater than computed value");
			}
		}
//		System.out.println("DT: " + this.delta_time);
		this.sampling_time = this.sampling_time + this.delta_time;
//		System.out.println("ST: " + this.sampling_time);
		this.cycle_count = this.cycle_count + 1;
		this.n_its = this.n_its + 1;
	}
	
	private class compute_all_fluxes implements NWRunner {
		public Double run(Double local_em) {
			goldman.compute_flux(local_em, temp_celsius,I_18);
			a23.compute_flux(I_18);
			passiveca.compute_flux(I_18);
			capump.compute_flux();
			totalCaFlux();
			totalFlux();
			return total_flux;
			
		}
	}
	
	public void totalCaFlux() {
		this.total_flux_Ca = this.a23.getFlux_Ca() + this.passiveca.getFlux() + this.capump.getFlux_Ca();
	}
	public void totalFlux() {
		this.total_flux = this.getNapump().getTotal_flux() + this.goldman.getFlux_H() + this.goldman.getFlux_Na() + this.goldman.getFlux_K() - this.goldman.getFlux_A() + this.capump.getCah()*this.capump.getFlux_Ca() + 2.0*this.passiveca.getFlux();
	}
	
	public void setup(HashMap<String,String> rsoptions, ArrayList<String> usedoptions) {
		if(this.stage == 0) {
			System.out.println("Setting up the RS");
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
			this.R = 1.0; // check type - flag to determine that we're computing reference state
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
		// this.publish();
	}
	
	public void setupDS(HashMap<String,String> options,ArrayList<String> usedoptions) {
		
		this.set_screen_time_factor_options(options, usedoptions);
		this.set_cell_fraction_options(options, usedoptions);
		this.set_transport_changes_options(options, usedoptions);
		this.set_temp_permeability_options(options, usedoptions);
		
		System.out.println();
		System.out.println("Used DS options");
		for(String option: usedoptions) {
			System.out.println(option);
		}
		
		System.out.println();
		System.out.println("Unused DS options");
		for(String option: options.keySet()) {
			if(!usedoptions.contains(option)) {
				System.out.println(option);
			}
		}
		this.stage += 1;
		System.out.println("Setup DS, stage = " + this.stage);
		//this.publish();
	}
	
	private void set_temp_permeability_options(HashMap<String,String> options, ArrayList<String> usedoptions) {
		Double defaultTemp = this.temp_celsius;
		String temp = options.get("temperature");
		if(temp != null) {
			this.temp_celsius = Double.parseDouble(temp);
			usedoptions.add("temperature");
			Double piold = this.pit0 - (0.016*defaultTemp);
			Double pinew = this.pit0 - (0.016*this.temp_celsius);
			this.I_74 = pinew;
			Double newphc = pinew - piold + this.cell.getpH();
			this.cell.setpH(newphc);
			this.cell.H.setConcentration(Math.pow(10, -this.cell.getpH()));
			if(this.BufferType == "HEPES") {
				this.pkhepes = 7.83 - 0.014*this.temp_celsius;
				Double a5old = this.A_5;
				Double m4old = this.medium.H.getConcentration();
				this.A_5 = Math.pow(10.0, -this.pkhepes);
				this.medium.H.setConcentration(this.A_5*m4old/a5old);

				this.medium.setpH(-Math.log(this.medium.H.getConcentration())/Math.log(10.0));
				this.medium.Hb.setConcentration(this.buffer_conc*(this.medium.H.getConcentration()/(this.A_5 + this.medium.H.getConcentration())));
				
			}else {
				this.A_5 = Math.pow(10.0, -this.pka);
				//Something funny happens here - these variables are used witout being set..
				Double m4old = 0.0;
				Double a5old = 0.0;
				this.medium.H.setConcentration(this.A_5*m4old/a5old);

				this.medium.setpH(-Math.log(this.medium.H.getConcentration())/Math.log(10.0));
				this.medium.Hb.setConcentration(this.buffer_conc*(this.medium.H.getConcentration()/(this.A_5 + this.medium.H.getConcentration())));
			}
		}
		
		temp = options.get("water-perm");
		if(temp != null) {
			this.water.setPermeability(Double.parseDouble(temp));
			usedoptions.add("water-perm");
		}
		
		temp = options.get("pgk");
		if(temp != null) {
			this.goldman.setPermeability_K(Double.parseDouble(temp));
			usedoptions.add("pgk");
		}
		
		temp = options.get("pgkh");
		if(temp != null) {
			this.goldman.setPgkh(Double.parseDouble(temp));
			usedoptions.add("pgkh");
		}
		
		temp = options.get("pgna");
		if(temp != null) {
			this.goldman.setPermeability_Na(Double.parseDouble(temp));
			usedoptions.add("pgna");
		}
		
		temp = options.get("pga");
		if(temp != null) {
			this.goldman.setPermeability_A(Double.parseDouble(temp));
			usedoptions.add("pga");
		}
		
		temp = options.get("pgh");
		if(temp != null) {
			this.goldman.setPermeability_H(Double.parseDouble(temp));
			usedoptions.add("pgh");
		}
		
		// New option Jan 2018
		temp = options.get("pgca");
		if(temp != null) {
			this.passiveca.setFcalm(Double.parseDouble(temp));
			usedoptions.add("pgca");
		}
		
		
		temp = options.get("pmg");
		if(temp != null) {
			this.a23.setPermeability_Mg(Double.parseDouble(temp));
			usedoptions.add("pmg");
			temp = options.get("a23cam");
			if(temp != null) {
				this.a23.setCamk(Double.parseDouble(temp));
				usedoptions.add("a23cam");
			} else {
				this.a23.setCamk(10.0);
			}
			temp = options.get("a23mgm");
			if(temp != null) {
				this.a23.setMgmk(Double.parseDouble(temp));
				usedoptions.add("a23mgm");
			} else {
				this.a23.setMgmk(10.0);
			}
			temp = options.get("a23cai");
			if(temp != null) {
				this.a23.setCaik(Double.parseDouble(temp));
				usedoptions.add("a23cai");
			} else {
				this.a23.setCaik(10.0);
			}
			temp = options.get("a23mgi");
			if(temp != null) {
				this.a23.setMgik(Double.parseDouble(temp));
				usedoptions.add("a23mgi");
			} else {
				this.a23.setMgik(10.0);
			}
		}
		
		this.a23.setPermeability_Ca(this.a23.getPermeability_Mg());
		
		temp = options.get("pit0");
		if(temp != null) {
			this.I_67 = this.pit0; // Store old value
			this.pit0 = Double.parseDouble(temp);
			usedoptions.add("pit0");	
			this.cell.setpH(this.pit0 - this.I_67 + this.cell.getpH());
			this.cell.H.setConcentration(Math.pow(10.0, -this.cell.getpH()));
			this.I_74 = this.pit0 - (0.016*this.temp_celsius);
			temp = options.get("deoxy");
			if(temp == "Y") {
				this.atp = this.atp / 2.0;
				this.dpgp = this.dpgp / 1.7;
			}
			
					
		}
		
	}
	
	private void set_transport_changes_options(HashMap<String,String> options, ArrayList<String> usedoptions) {
		String temp = options.get("na-pump-flux-change");
		if(temp != null) {
			this.getNapump().setP_1(Double.parseDouble(temp));
			usedoptions.add("na-pump-flux-change");
		}
		
		temp = options.get("na-pump-reverse-flux-change");
		if(temp != null) {
			this.getNapump().setP_2(Double.parseDouble(temp));
			usedoptions.add("na-pump-reverse-flux-change");
		}
		temp = options.get("naa-change");
		if(temp != null) {
			this.carriermediated.setPermeability_Na(
					Double.parseDouble(temp)*this.carriermediated.getPermeability_Na());
			usedoptions.add("naa-change");
		}
		temp = options.get("ka-change");
		if(temp != null) {
			this.carriermediated.setPermeability_K(Double.parseDouble(temp) *
					this.carriermediated.getPermeability_K());
			usedoptions.add("ka-change");
		}
		temp = options.get("cotransport-activation");
		if(temp != null) {
			Double co_f = Double.parseDouble(temp);
			this.cotransport.setPermeability(0.0002 * co_f / 100.0);
			usedoptions.add("cotransport-activation");
		}
		temp = options.get("js-stimulation-inhibition");
		if(temp != null) {
			Double jsfactor = Double.parseDouble(temp);
			this.JS.setPermeability(this.JS.getPermeability() * jsfactor);
			usedoptions.add("js-stimulation-inhibition");
		}
		temp = options.get("vmax-pump-change");
		if(temp != null) {
			this.capump.setFcapm(this.capump.getFcapm() * Double.parseDouble(temp));
			usedoptions.add("vmax-pump-change");
		}
		
//		temp = options.get("vmax-leak-change");
//		if(temp != null) {
//			this.passiveca.setFcalm(this.passiveca.getFcalm()*Double.parseDouble(temp));
//			usedoptions.add("vmax-leak-change");
//		}
		
		temp = options.get("percentage-inhibition");
		if(temp != null) {
			this.goldman.setPkm(this.goldman.getPkm() * 
					(100.0 - Double.parseDouble(temp))/100.0);
			usedoptions.add("percentage-inhibition");
		}
	}
	
	private void set_cell_fraction_options(HashMap<String,String> options, ArrayList<String> usedoptions) {
		String temp = options.get("fraction");
		if(temp != null) {
			this.fraction = Double.parseDouble(temp);
			usedoptions.add("fraction");
		}
		
		if(this.A_7 != this.fraction) {
			this.A_7 = this.fraction;
			this.A_8 = this.A_7/(1.0 - this.A_7);
		}
		
		temp = options.get("buffer-name");
		if(temp != null) {
			int buffer_number = Integer.parseInt(temp);
			switch(buffer_number) {
				case 0: this.BufferType = "HEPES";
				case 1: this.BufferType = "A";
				case 2: this.BufferType = "C";
			}
			usedoptions.add("buffer-name");
			temp = options.get("pka");
			if(temp != null) {
				this.pka = Double.parseDouble(temp);
				usedoptions.add("pka");
			}	
		}
		
		temp = options.get("bufferconc");
		if(temp != null) {
			this.buffer_conc = Double.parseDouble(temp);
			usedoptions.add("bufferconc");
		}
		
		this.A_12 = this.medium.getpH();
		temp = options.get("extph");
		if(temp != null) {
			this.medium.setpH(Double.parseDouble(temp));
			usedoptions.add("extph");
		}
		this.phadjust();
		
		temp = options.get("nag");
		if(temp != null) {
			this.I_72 = Double.parseDouble(temp);
			usedoptions.add("nag");
			this.medium.Glucamine.setConcentration(this.medium.Glucamine.getConcentration() + this.I_72);
			this.medium.Na.setConcentration(this.medium.Na.getConcentration() - this.I_72);
		}
		// Should this one be removed once done?
		temp = options.get("acl");
		if(temp != null) {
			this.I_73 = Double.parseDouble(temp);
			usedoptions.add("acl");
			this.medium.Gluconate.setConcentration(this.medium.Gluconate.getConcentration() + this.I_73);
			this.medium.A.setConcentration(this.medium.A.getConcentration() - this.I_73);
			if(this.I_73 != 0) {
				String temp2 = options.get("clxdur");
				if(temp2 != null) {
					this.T_6 = Double.parseDouble(temp2);
					usedoptions.add("clxdur");
				}
			}
		}
		
		temp = options.get("NaxK");
		if(temp != null) {
			this.I_40 = Double.parseDouble(options.get("NaxK"));
			this.medium.Na.setConcentration(this.medium.Na.getConcentration() + this.I_40);
			this.medium.K.setConcentration(this.medium.K.getConcentration() - this.I_40);
			usedoptions.add("NaxK");
			options.remove("NaxK");
		}
		
		temp = options.get("KxNa");
		if(temp != null) {
			this.I_33 = Double.parseDouble(options.get("KxNa"));
			this.medium.Na.setConcentration(this.medium.Na.getConcentration() - this.I_33);
			this.medium.K.setConcentration(this.medium.K.getConcentration() + this.I_33);
			usedoptions.add("KxNa");
			options.remove("KxNa");
		}
		
		temp = options.get("change-nacl");
		if(temp != null) {
			this.I_45 = Double.parseDouble(options.get("change-nacl"));
			this.medium.Na.setConcentration(this.medium.Na.getConcentration() + this.I_45);
			this.medium.A.setConcentration(this.medium.A.getConcentration() + this.I_45);
			usedoptions.add("change-nacl");
		}
		
		temp = options.get("change-kcl");
		if(temp != null) {
			this.I_34 = Double.parseDouble(options.get("change-kcl"));
			this.medium.K.setConcentration(this.medium.K.getConcentration() + this.I_34);
			this.medium.A.setConcentration(this.medium.A.getConcentration() + this.I_34);
			usedoptions.add("change-kcl");
		}
		
		temp = options.get("add-sucrose");
		if(temp != null) {
			this.I_46 = Double.parseDouble(options.get("add-sucrose"));
			this.medium.Sucrose.setConcentration(this.medium.Sucrose.getConcentration() + this.I_46);
			usedoptions.add("add-sucrose");
		}
		
		temp = options.get("mgot");
		if(temp != null) {
			Double mgtold = this.medium.Mgt.getConcentration();
			this.medium.Mgt.setConcentration(Double.parseDouble(temp));
			usedoptions.add("mgot");
			if(this.medium.Mgt.getConcentration() != 0) {
				this.medium.A.setConcentration(this.medium.A.getConcentration() + 2.0*(this.medium.Mgt.getConcentration() - mgtold));
			}
		}
		
		temp = options.get("cato");
		if(temp != null) {
			Double catold = this.medium.Cat.getConcentration();
			this.medium.Cat.setConcentration(Double.parseDouble(temp));
			usedoptions.add("cato");
			if(this.medium.Cat.getConcentration() != 0) {
				this.medium.A.setConcentration(this.medium.A.getConcentration() + 2.0*(this.medium.Cat.getConcentration() - catold));
			}
		}
		
		temp = options.get("chelator");
		if(temp!=null) {
			usedoptions.add("chelator");
			this.ligchoice = Double.parseDouble(temp);
		}
		
		temp = options.get("edgto"); // chelator concentration
		if(temp != null) {
			usedoptions.add("edgto");
			this.edgto = Double.parseDouble(temp);
		}
		
		if(this.ligchoice == 1) { // EGTA
			this.edghk1 = Math.pow(10,(-9.22));
			this.edghk2 = Math.pow(10,(-8.65));
			this.edgcak = Math.pow(10,(-10.34));
			this.edgmgk = Math.pow(10,(-5.10));
			if (this.medium.Cat.getConcentration()<this.edgto) {
				this.medium.Caf.setConcentration(this.medium.Caf.getConcentration()/100000.0);
				
			} else if (this.medium.Cat.getConcentration() == this.edgto) {
				this.medium.Caf.setConcentration(this.medium.Cat.getConcentration()/100.0);
				
			} else if (this.medium.Cat.getConcentration()>this.edgto) {
				this.medium.Caf.setConcentration(Math.abs(this.medium.Cat.getConcentration()-this.edgto));

			}
			if (this.medium.Mgt.getConcentration() < (this.edgto - this.medium.Cat.getConcentration())) {
				this.medium.Mgf.setConcentration(this.medium.Mgt.getConcentration()/5.0);
			} else {
				this.medium.Mgf.setConcentration(this.medium.Mgt.getConcentration());
			}

		} else if(this.ligchoice == 2) { // EDTA
			this.edghk1 = Math.pow(10,(-9.84));
			this.edghk2 = Math.pow(10,(-5.92));
			this.edgcak = Math.pow(10,(-9.95));
			this.edgmgk = Math.pow(10,(-8.46));
			// Initial cafo/mgfo values for iterative solution
			Double camgratio=this.medium.Cat.getConcentration()/(this.medium.Cat.getConcentration()+this.medium.Mgt.getConcentration());
			if (this.edgto < (this.medium.Cat.getConcentration() + this.medium.Mgt.getConcentration())){
				this.medium.Caf.setConcentration(this.medium.Cat.getConcentration() - this.edgto*camgratio);
				this.medium.Mgf.setConcentration(this.medium.Mgt.getConcentration() - this.edgto*(1.0-camgratio));
			} else if (this.edgto > (this.medium.Cat.getConcentration() + this.medium.Mgt.getConcentration())) {
				this.medium.Caf.setConcentration(this.medium.Cat.getConcentration()/100000.0);
				this.medium.Mgf.setConcentration(this.medium.Mgt.getConcentration()/1000.0);
			} else {
				this.medium.Caf.setConcentration(this.medium.Cat.getConcentration()/1000.0);
				this.medium.Mgf.setConcentration(this.medium.Mgt.getConcentration()/10.0);
			}
			
			
		} else if(this.ligchoice == 0) {
			this.edgto = 0.0;
			this.medium.Mgf.setConcentration(this.medium.Mgt.getConcentration());
			this.medium.Caf.setConcentration(this.medium.Cat.getConcentration());
			return;
		}
		
		

		
		this.chelator();
		this.oldedgta();
	}
	
	private void oldedgta() {
		// The following computes initial "dedgh"=proton release to medium on chelation
		Double fh = 1 + this.medium.H.getConcentration()/this.edghk1 + Math.pow(this.medium.H.getConcentration(),2)/(this.edghk1*this.edghk2);
		Double edg4old = this.edgto/(1000*fh);
		Double edg3old = edg4old*this.medium.H.getConcentration()/this.edghk1;
		Double edg2old = edg4old*Math.pow(this.medium.H.getConcentration(),2)/(this.edghk1*this.edghk2);
		this.edghold = edg3old+2*edg2old;
		this.edghold = 1000*this.edghold;
		this.edgtainitial();
		this.edgta();

		this.edgneg=2*this.edg2+3*this.edg3+4*this.edg4+2*(this.edgca+this.edgmg);

		if(this.BufferType == "c" || this.BufferType=="C") {
			this.medium.A.setConcentration(this.medium.Na.getConcentration()+ this.medium.Hb.getConcentration() + this.medium.Glucamine.getConcentration() + 2*this.medium.Mgf.getConcentration() + 2*this.medium.Caf.getConcentration() - this.edgneg - this.medium.Gluconate.getConcentration());
		} else {
			this.medium.Na.setConcentration(this.medium.A.getConcentration() + this.edgneg + this.medium.Gluconate.getConcentration() + (this.buffer_conc - this.medium.Hb.getConcentration()) - this.medium.Glucamine.getConcentration() - this.medium.K.getConcentration() - 2*this.medium.Mgf.getConcentration() - 2*this.medium.Caf.getConcentration());
		}
	}
	
	private void edgtainitial() {
		// Convert ligand, Ca and Mg concentrations to Molar units for ligroots sub
		this.edgto=this.edgto/1000.0;
		this.medium.Caf.setConcentration(this.medium.Caf.getConcentration()/1000.0);
		this.medium.Mgf.setConcentration(this.medium.Mgf.getConcentration()/1000.0);
		this.medium.Cat.setConcentration(this.medium.Cat.getConcentration()/1000.0);
		this.medium.Mgt.setConcentration(this.medium.Mgt.getConcentration()/1000.0);
		this.buffer_conc=this.buffer_conc/1000.0;
		this.delta_H=this.delta_H/1000.0;
		this.dedgh=this.dedgh/1000.0;
		
		Double fff=1+this.medium.H.getConcentration()/this.edghk1+Math.pow(this.medium.H.getConcentration(),2)/(this.edghk1*this.edghk2)+this.medium.Caf.getConcentration()/this.edgcak+this.medium.Mgf.getConcentration()/this.edgmgk;
		// This parameter was made a class parameter to avoid 
		// extra variables being passed to newton_raphson. 
		// It's only used by ligeq1
		this.lig_hb=this.medium.H.getConcentration()*(this.buffer_conc/(this.medium.H.getConcentration()+this.A_5)+this.edgto/(fff*this.edghk1)+this.medium.H.getConcentration()*this.edgto/(fff*this.edghk1*this.edghk2));
		int nn = 0;
		boolean finished = false;
		while(!finished) {
			int bbb = 0;
			int rr = 1;
			Double buff = this.medium.H.getConcentration();
			Double hhold = buff;
			this.it_counter = 0;
			Double diff1 = 0.0001*this.medium.H.getConcentration();
			Double X_3 = this.newton_raphson(new ligeq1(), buff, diff1, this.diff2, 100, bbb, false);
			bbb += this.it_counter;
			if(X_3 < 0) {
				X_3 = hhold;
			}
			this.medium.H.setConcentration(X_3);

			
			rr = 2;
			buff = this.medium.Caf.getConcentration();
			Double cafold = buff;
			diff1 = 0.0001 * cafold;
			this.it_counter = 0;
			X_3 = this.newton_raphson(new ligeq2(), buff, diff1, this.diff2, 100, bbb, false);
			bbb += this.it_counter;
			if(X_3 < 0) {
				X_3 = cafold / 2.0;
			}
			this.medium.Caf.setConcentration(X_3);
			
			rr = 3;
			buff = this.medium.Mgf.getConcentration();
			Double mgfold = buff;
			diff1 = 0.0001 * mgfold;
			this.it_counter = 0;
			X_3 = this.newton_raphson(new ligeq3(), buff, diff1, this.diff2, 100, bbb, false);
			bbb += this.it_counter;
			if(X_3 < 0) {
				X_3 = mgfold / 2.0;
			}
			this.medium.Mgf.setConcentration(X_3);
			nn += 1;
			if(nn > 100) {
				finished = true;
			}
			if ((Math.abs(this.medium.H.getConcentration() - hhold)<=this.diff3*hhold) && (Math.abs(cafold-this.medium.Caf.getConcentration())<=this.diff3*cafold) && (Math.abs(mgfold-this.medium.Mgf.getConcentration())<=this.diff3*mgfold)) {
				finished = true;
			}
		}
		
		this.medium.setpH(-Math.log(this.medium.H.getConcentration())/Math.log(10.0));
		this.buffer_conc = this.buffer_conc*1000.0;
		this.delta_H = this.delta_H*1000.0;
		this.medium.Hb.setConcentration(this.medium.H.getConcentration()*this.buffer_conc/(this.medium.H.getConcentration() + this.A_5));
		this.edg4 = this.edgto/this.ff;
		this.edg3 = this.edg4*this.medium.H.getConcentration()/this.edghk1;
		this.edg2 = this.edg4*Math.pow(this.medium.H.getConcentration(),2)/(this.edghk1*this.edghk2);
		this.edgca = this.edg4*this.medium.Caf.getConcentration()/this.edgcak;
		this.edgmg = this.edg4*this.medium.Mgf.getConcentration()/this.edgmgk;
		this.edgneg=2*this.edg2+3*this.edg3+4*this.edg4+2*(this.edgca+this.edgmg);
		this.edghnew=this.edg3+2*this.edg2;

		// Convert ligand, Ca and Mg concentrations back to mM units
		this.edgto=this.edgto*1000;
		this.medium.Caf.setConcentration(this.medium.Caf.getConcentration()*1000);
		this.medium.Mgf.setConcentration(this.medium.Mgf.getConcentration()*1000);
		this.medium.Cat.setConcentration(this.medium.Cat.getConcentration()*1000);
		this.medium.Mgt.setConcentration(this.medium.Mgt.getConcentration()*1000);
		this.edg4=this.edg4*1000;
		this.edg3=this.edg3*1000;
		this.edg2=this.edg2*1000;
		this.edgca=this.edgca*1000;
		this.edgmg=this.edgmg*1000;
		this.edgneg=this.edgneg*1000;
		this.edghnew=this.edghnew*1000;
		// computes d[H]o due to chelation.
		this.dedgh=this.edghnew-this.edghold;
		
	}
	
	private class ligeq1 implements NWRunner {
		public Double run(Double X_3) {
			ff = 1 + X_3/edghk1 + Math.pow(X_3,2)/(edghk1*edghk2) + medium.Caf.getConcentration()/edgcak + medium.Mgf.getConcentration()/edgmgk;
			return X_3*(buffer_conc/(X_3 + A_5) + edgto/(ff*edghk1) + X_3*edgto/(ff*edghk1*edghk2))-(lig_hb-A_8*delta_H-dedgh);
		}
	}
	private class ligeq2 implements NWRunner {
		public Double run(Double X_3) {
			ff = 1 + medium.H.getConcentration()/edghk1 + Math.pow(medium.H.getConcentration(),2)/(edghk1*edghk2) + X_3/edgcak + medium.Mgf.getConcentration()/edgmgk;
			return medium.Cat.getConcentration() - X_3*(1+edgto/(ff*edgcak));
		}	
	}	

	private class ligeq3 implements NWRunner {
		public Double run(Double X_3) {
			ff = 1 + medium.H.getConcentration()/edghk1 + Math.pow(medium.H.getConcentration(),2)/(edghk1*edghk2) + medium.Caf.getConcentration()/edgcak + X_3/edgmgk;
			return medium.Mgt.getConcentration() - X_3*(1+edgto/(ff*edgmgk));
		}
	}

	
	private void edgta() {
		this.edghold = this.edg3 + 2*this.edg2;
		this.edgtainitial();
	}
	
	private void chelator() {
		this.medium.Na.setConcentration(this.medium.Na.getConcentration() + 2*this.edgto);
	}
	
	private void phadjust() {
		// 	phadjust:
		// called from screen 2 entries after 4350
		this.medium.H.setConcentration(Math.pow(10.0,(-this.medium.getpH())));
		// Protonized buffer concentration;
		if (this.BufferType == "HEPES") {
			this.pkhepes = 7.83 - 0.014*this.temp_celsius;
			this.A_5 = Math.pow(10.0,(-this.pkhepes));
			this.medium.Hb.setConcentration(this.buffer_conc*(this.medium.H.getConcentration()/(this.A_5+this.medium.H.getConcentration())));
		} else {
			this.A_5 = Math.pow(10.0,(-this.pka));
			this.medium.Hb.setConcentration(this.buffer_conc*(this.medium.H.getConcentration()/(this.A_5+this.medium.H.getConcentration())));
		}

		if (this.BufferType == "C") {
			if(this.medium.getpH() >= this.A_12) {
				this.medium.Na.setConcentration(this.medium.A.getConcentration()+this.edgneg+this.medium.Gluconate.getConcentration()-this.medium.Hb.getConcentration()-this.medium.Glucamine.getConcentration()-this.medium.K.getConcentration()-2*this.medium.Mgf.getConcentration()-2*this.medium.Caf.getConcentration());
			} else {
				this.medium.A.setConcentration(this.medium.Na.getConcentration() + this.medium.K.getConcentration()+this.medium.Glucamine.getConcentration()+this.medium.Hb.getConcentration()-this.medium.Gluconate.getConcentration()-this.edgneg+2*this.medium.Mgf.getConcentration()+2*this.medium.Caf.getConcentration());
			}
		} else {
			if(this.medium.getpH() >= this.A_12) {
				this.medium.Na.setConcentration(this.medium.A.getConcentration()+this.edgneg+this.medium.Gluconate.getConcentration()+(this.buffer_conc-this.medium.Hb.getConcentration())-this.medium.Glucamine.getConcentration()-this.medium.K.getConcentration()-2*this.medium.Mgf.getConcentration()-2*this.medium.Caf.getConcentration());
			} else {
				this.medium.A.setConcentration(this.medium.Na.getConcentration() + this.medium.K.getConcentration()+this.medium.Glucamine.getConcentration()-(this.buffer_conc-this.medium.Hb.getConcentration())-this.medium.Gluconate.getConcentration()-this.edgneg+2*this.medium.Mgf.getConcentration()+2*this.medium.Caf.getConcentration());
			}
		}
	}
	
	private void set_screen_time_factor_options(HashMap<String,String> options, ArrayList<String> usedoptions) {
		String temp = options.get("time");
		if(temp != null) {
			this.duration_experiment += Double.parseDouble(temp);
			usedoptions.add("time");
		}
		
		this.I_43 = this.integration_interval_factor;
		temp = options.get("integrationfactor");
		if(temp != null) {
			this.integration_interval_factor = Double.parseDouble(temp);
			usedoptions.add("integrationfactor");
		}
		
		temp = options.get("compute_delta_time");
		if(temp != null) {
			if(temp.equals("no")) {
				this.compute_delta_time = false;
				usedoptions.add("compute_delta_time");
			}else if(temp.equals("yes")) {
				this.compute_delta_time = true;
				usedoptions.add("compute_delta_time");
			}else {
				System.out.println("Invalud value for field compute_delta_time");
			}
		}
		temp = options.get("delta_time");
		if(temp != null) {
			this.delta_time = Double.parseDouble(temp);
			usedoptions.add("delta_time");
		}
		temp = options.get("cyclesperprint");
		if(temp != null) {
			this.cycles_per_print = Integer.parseInt(temp);
			usedoptions.add("cyclesperprint");
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
		this.cycle_count = 0;
		this.n_its = 0;
		this.Z = 0;
		this.duration_experiment = 0.0;

		this.RS_computed = true;

		// Set stage to zero everytime the RS is computed - stage = 1 means we're about to start DS 1
		this.stage = 0;
	}
	
	private void fluxesRS() {
		// Flux-rates and RS fluxes
		this.getNapump().compute_permeabilities(this.temp_celsius);

		this.carriermediated.setFlux_Na(-(1-this.fG)*this.getNapump().getFlux_net());
		this.carriermediated.setFlux_K(-this.carriermediated.getFlux_Na()/this.Na_to_K);
		this.carriermediated.compute_permeabilities();

		// A fluxes through Na:Cl and K:Cl
		Double fal = this.carriermediated.getFlux_Na() + this.carriermediated.getFlux_K();
		// G-flux of A required to balance fal
		this.goldman.setFlux_A(-fal);
		// G-rates and RS fluxes
		this.goldman.setFlux_Na(-this.fG*this.getNapump().getFlux_net());
		this.goldman.setFlux_K(-this.goldman.getFlux_Na()/this.Na_to_K);
		this.I_18 = 1.0;
		this.goldman.compute_permeabilities(this.Em,this.temp_celsius);

		// Zero-factor for cotransport
		this.cotransport.compute_zero_factor();
		this.X_6 = this.Vw;
		// this.cotransportmediatedfluxes()
		this.cotransport.compute_flux(this.I_18);
		this.totalionfluxes();

		this.Q_8 = this.fHb*this.cell.Hb.getAmount(); // How many solute equivalents (cell.Hb.getAmount() = QHb)
		// fHb is osmotic coefficient of haemoglobin

		this.chbetc();
	}
	
	private void totalionfluxes() {
		// Total ion fluxes
		// Na flux
		this.total_flux_Na = this.getNapump().getFlux_net() + this.carriermediated.getFlux_Na() + this.goldman.getFlux_Na() + this.cotransport.getFlux_Na();
		// K flux
		this.total_flux_K = this.getNapump().getFlux_K() + this.carriermediated.getFlux_K() + this.goldman.getFlux_K() + this.cotransport.getFlux_K();
		// Anion flux
//		System.out.println(this.goldman.getFlux_A() + "," + this.cotransport.getFlux_A() + "," + this.JS.getFlux_A() + "," + this.carriermediated.getFlux_Na() + this.carriermediated.getFlux_K());
		this.total_flux_A = this.goldman.getFlux_A() + this.cotransport.getFlux_A() + this.JS.getFlux_A() + this.carriermediated.getFlux_Na() + this.carriermediated.getFlux_K();
		// Net proton flux, includes H-flux through Ca pump
//		System.out.println(this.goldman.getFlux_H() + "," + this.JS.getFlux_H() + "," + this.a23.getFlux_Mg() + "," + this.a23.getFlux_Ca() + "," + this.capump.getFlux_H());
		this.total_flux_H = this.JS.getFlux_H() + this.goldman.getFlux_H() - 2*this.a23.getFlux_Mg()-2*this.a23.getFlux_Ca()+this.capump.getFlux_H();
//		System.out.println(this.a23.getPermeability_Ca() + "," + this.a23.getPermeability_Mg());
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
		this.fHb = 1 + this.A_2*this.cell.Hb.getAmount()/this.Vw + this.A_3*Math.pow(this.cell.Hb.getAmount()/this.Vw,2.0);
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
			this.getNapump().setFluxFwd(Double.parseDouble(na_efflux_fwd));
			usedoptions.add("na-efflux-fwd");
		}
		
		if(this.getNapump().getFluxFwd() == -2.61) {
			this.napmaxf = 1.0;
		}else {
			this.napmaxf = 0.0;
		}
		
		String na_efflux_rev = rsoptions.get("na-efflux-rev");
		if(na_efflux_rev != null) {
			this.getNapump().setFluxRev(Double.parseDouble(na_efflux_rev));
			usedoptions.add("na-efflux-rev");
		}
		
		if(this.getNapump().getFluxRev() == 0.0015) {
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
		}
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
	
	public void cellanionprotonscreenRS(HashMap<String,String> rsoptions,ArrayList<String> usedoptions) {
		String temp = rsoptions.get("cla-conc");
		if(temp != null) {
			this.cell.A.setConcentration(Double.parseDouble(temp));
			usedoptions.add("cla-conc");
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
		
		Double conc = this.newton_raphson(new Eqmg(), 0.02, 0.0001, 0.00001,100,0, false);
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
		
		Double conc = this.newton_raphson(new Eqmg(), 0.02, 0.0001, 0.00001,100,0, false);
//		System.out.println(conc);
		this.cell.Mgf.setConcentration(conc);
		
	}
	
	private void cabufferscreenRS(HashMap<String,String> rsoptions, ArrayList<String> usedoptions) {
		String temp = rsoptions.get("cato-medium");
		if(temp != null) {
			this.medium.Cat.setConcentration(Double.parseDouble(temp));
			usedoptions.add("cato-medium");
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
			this.benz2 = 0.0;
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
			this.goldman.setPkm(30.0);
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
		
		Double conc = this.newton_raphson(new Eqca(),this.cell.Caf.getConcentration(),0.000001, 0.000001,100,0, false);
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
	private Double newton_raphson(NWRunner r, Double initial, Double step, Double stop,Integer max_its, Integer initial_its, boolean verbose) {
//		int max_its = 100;
//		Double step = 0.001;
//		Double stop = 0.0001;
//		int initial_its = 0;
		Double X_3 = initial;
		int no_its = initial_its;
		Boolean finished = false;
		while(!finished) {
			Double X_1 = X_3 - step;
			Double Y_1 = r.run(X_1);
			if(verbose) {
				System.out.println("X_1: " + X_1 + ", Y_1: " + Y_1);
			}
			Double X_2 = X_3 + step;
			Double Y_2 = r.run(X_2);
			Double S = (Y_2 - Y_1) / (X_2 - X_1);
			X_3 = X_1 - (Y_1/S);
			if(verbose) {
				System.out.println("X_3: " + X_3 + ", S: " + S + ", Y/S: " + Y_1/S);
			}
//			System.out.println(Math.abs(Y_2));
			// Note the bit here that isn't copied...
			Double Y_3;
			if(r instanceof ligeq3 || r instanceof ligeq1) {
				Y_3 = Y_2;
			} else {
				Y_3 = r.run(X_3);
			}
			if(verbose) {
				System.out.println("Y_3: " + Y_3);
				System.out.println();
			}
			no_its++;
			if(no_its > max_its) {
				finished = true;
			}
			if(Math.abs(Y_3) < stop) {
				finished = true;
			}
			it_counter += 1;
		}
//		System.out.println(no_its);
		return X_3;
	}
	
	public void publish() {
//		System.out.println("Publishing at time: " + this.sampling_time);
		ResultHash new_result = new ResultHash(this.sampling_time);
		
		new_result.setItem("Vw",this.Vw);
		new_result.setItem("V/V",this.VV);
		new_result.setItem("MCHC",this.mchc);
		new_result.setItem("Density",this.density);
		new_result.setItem("pHi",this.cell.getpH());
		new_result.setItem("pHo",this.medium.getpH());
		new_result.setItem("Hct",this.fraction*100.0);
		new_result.setItem("Em",this.Em);
		new_result.setItem("QNa",this.cell.Na.getAmount());
		new_result.setItem("QK",this.cell.K.getAmount());
		new_result.setItem("QA",this.cell.A.getAmount());
		new_result.setItem("QCa",this.cell.Cat.getAmount());
		new_result.setItem("QMg",this.cell.Mgt.getAmount());
		new_result.setItem("CNa",this.cell.Na.getConcentration());
		new_result.setItem("CK",this.cell.K.getConcentration());
		new_result.setItem("CA",this.cell.A.getConcentration());
		new_result.setItem("CCa2+",this.cell.Caf.getConcentration());
		new_result.setItem("CMg2+",this.cell.Mgf.getConcentration());
		new_result.setItem("CHb",this.cell.Hb.getConcentration());
		new_result.setItem("CX",this.cell.X.getConcentration());
		new_result.setItem("COs",this.cell.COs.getConcentration());
		new_result.setItem("rA",this.rA);
		new_result.setItem("rH",this.rH);
		new_result.setItem("fHb",this.fHb);
		new_result.setItem("nHb",this.nHb);
		new_result.setItem("MNa",this.medium.Na.getConcentration());
		new_result.setItem("MK",this.medium.K.getConcentration());
		new_result.setItem("MA",this.medium.A.getConcentration());
		new_result.setItem("MB", this.buffer_conc);
		new_result.setItem("MCat", this.medium.Cat.getConcentration());
		new_result.setItem("MCaf", this.medium.Caf.getConcentration());
		new_result.setItem("FNaP",this.getNapump().getFlux_net());
		new_result.setItem("FCaP",this.capump.getFlux_Ca());
		new_result.setItem("FKP",this.getNapump().getFlux_K());
		new_result.setItem("FNa",this.total_flux_Na);
		new_result.setItem("FK",this.total_flux_K);
		new_result.setItem("FA",this.total_flux_A);
		new_result.setItem("FH",this.total_flux_H);
		new_result.setItem("FW",this.water.getFlux());
		new_result.setItem("FNaG",this.goldman.getFlux_Na());
		new_result.setItem("FKG",this.goldman.getFlux_K());
		new_result.setItem("FAG",this.goldman.getFlux_A());
		new_result.setItem("FHG",this.goldman.getFlux_H());		
		
		this.resultList.add(new_result);
	}
	
	public void writeCsv(String name) {
		FileWriter filewriter = null;
		try {
			filewriter = new FileWriter(name);
			String headString = "Time";
			for(int i=0;i<this.publish_order.length;i++) {
				headString += ',' + this.publish_order[i];
			}
			headString += '\n';
			filewriter.append(headString);
			String resultString;
			for(ResultHash r: this.resultList) {
				resultString = String.format("%7.4f",60.0*r.getTime());
				for(int i=0;i<this.publish_order.length;i++) {
					resultString += ',' + String.format("%7.4f", r.getItem(this.publish_order[i]));
				}
//				System.out.println(resultString);
				resultString += '\n';
				filewriter.append(resultString);
			}
		}catch (Exception e) {
			
		}finally {
			try {
				filewriter.flush();
				filewriter.close();
			}catch(IOException e) {
				
			}
		}
	}

	public NaPump getNapump() {
		return napump;
	}

	public void setNapump(NaPump napump) {
		this.napump = napump;
	}

	public int getStage() {
		return this.stage;
	}
}
