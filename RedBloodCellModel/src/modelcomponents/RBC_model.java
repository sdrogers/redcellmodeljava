package modelcomponents;
import java.util.HashMap;

import javax.swing.JOptionPane;
import javax.swing.JTextArea;

import utilities.MileStone;
import utilities.NWRunner;
import utilities.ResultHash;

import java.util.ArrayList;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
public class RBC_model implements Serializable {
	
	private String[] publish_order = {"V/V","Vw",
									  "CVF",
									  "Hct","MCHC",
	                                  "Density","Em",
									  "pHi","pHo",
									  "rH","rA",
									  "QNa","QK",
	                                  "QA","QCa","QMg",
	                                  "CH/nM",
	                                  "CNa",
	                                  "CK","CA",
	                                  "CCa2+",
	                                  "CMg2+",
	                                  "CHb",
	                                  "fHb",
	                                  "nHb",
	                                  "fHb*CHb",
	                                  "nHb*CHb",
	                                  "CX-",
	                                  "nX",
	                                  "nX*CX-",
	                                  "COs","MOs",
	                                  "MNa","MK","MA","MH","MB",
	                                  "MCat","MCa2+","MMgt","MMg2+",
	                                  "MEDGTA",
	                                  "Msucrose",
	                                  "Mgluconate",
	                                  "Mglucamine",
	                                  "FNaP",
	                                  "FKP",
	                                  "FCaP",
	                                  "FHCaP",
	                                  "FKGgardos",
	                                  "FCaG",
	                                  "FNaG",
	                                  "FKG",
	                                  "FAG",
	                                  "FHG",
									  "FMgG",
	                                  "FAJS",
	                                  "FHJS",
	                                  "FClCo",
	                                  "FKCo",
	                                  "FNaCo",
	                                  "FA23Ca","FA23Mg",
	                                  "FNa",
	                                  "FK","FA","FH","FCa",
	                                  "FW",
	                                  "EA","EH","EK","ENa",
	                                  "FzK",
	                                  "FzNa","FzA","FzCa",
	                                  "EN test"};
	private ArrayList<ResultHash> resultList = new ArrayList<ResultHash>();
	
	private boolean verbose = true;
	private boolean doPublish = true;
	
	private final double MIN_BUFFER_CONC = 1e-10;
	
	private boolean isCancelled = false;
	
	// Model components
	Region cell;
	Region medium;
	private JacobsStewart JS;
	private Cotransport cotransport;
	NaPump napump;
	private CarrierMediated carriermediated;
	Goldman goldman;
	private PiezoGoldman piezoGoldman;
	private A23187 a23;
	private WaterFlux water;
	PassiveCa passiveca;
	private PiezoPassiveCa piezoPassiveca;
	CaPumpMg2 capump;
	
	// Messy variables. One day these will be fixed.
	private Double A_1;
	private Double A_2;
	private Double A_3;
	private Double pit0;
	private Double A_5;
	private boolean compute_delta_time = true; // Whether to compute this, or use a constant
	private Double integration_interval_factor; // Integration factor
	private Double A_7;
	private Double A_8;
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
	Double Q10Passive; // was B10, B_10, B[10] // ADD TO TAB FOR PERMEABILITIES
	
	private Double delta_H;
	
	
	private Double total_flux_Na;
	private Double total_flux_K;
	private Double total_flux_A;
	private Double total_flux_H;
	private Double total_flux_Mg;
	
		
	
	private Double I_18;
	private Double I_73;
	private Double I_79; 
	
	private Double buffer_conc;
	private Double netChargeHb;
	

	private Double sampling_time;
	private int cycle_count;
	private Integer cycles_per_print;
	private Double duration_experiment;
	private Integer n_its;
	private Double T_6;
	private int dp = 4; // number of decimal places in the output
	
	private Double Vw;
	private Double VV;
	private Double fraction;
	private Double defaultFraction;
	private Double mchc;
	private Double density;
	private Double Em;
	private Double rA;
	private Double rH;
	private Double fHb;
	private Double nHb;
	
	
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
	private Double cabb;
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
	
	private Double ff;
	private Double delta_Mg;
	private Double delta_Ca;
	

	private Double I_74;
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

	private double delta_time = 0.1; // Initial value in case it is not computed

	private double delta_Na;

	private double delta_K;

	private double delta_A;

	private double delta_Water;

	
	private Piezo piezo;
	
	private Double finalPiezoHct = 0.0;
	private int total_cycle_count;
	private Double finalPiezoCCa = 0.0;
	private Double finalPiezoFK = 0.0;
	private Double finalPiezoFNa = 0.0;
	private Double finalPiezoFA = 0.0;
	private Double finalPiezoFCa = 0.0;
	private ResultHash finalPiezoResult;
	private Double oldJSPermeability;
	private boolean lifespan = false;
	
	private double nXdefOxy;
	private double mgbal = 0.0159;
	
	public RBC_model() {
		cell = new Region();
		medium = new Region();
		
		// Define the pumps
		JS = new JacobsStewart(cell,medium);
		cotransport = new Cotransport(cell,medium);
		setNapump(new NaPump(cell,medium));
		carriermediated = new CarrierMediated(cell,medium);
		goldman = new Goldman(cell,medium);
		piezoGoldman = new PiezoGoldman(cell,medium);
		a23 = new A23187(cell,medium);
		water = new WaterFlux(cell,medium);
		passiveca = new PassiveCa(cell,medium,goldman);
		piezoPassiveca = new PiezoPassiveCa(cell,medium,piezoGoldman);
		capump = new CaPumpMg2(cell,medium,getNapump());
		
		setA_1(-10.0); // Net charge on haemoglobin
		A_2 = 0.0645; // verial coefficients of osmotic coefficient of haemo (fhb)
		A_3 = 0.0258; // verial coefficients of osmotic coefficient of haemo (fhb)
		// private Double A_4 = 7.2;
		setPit0(7.2); // iso-electric point of haemoglobin - constant
		A_5 = 2.813822508658947e-08; // dissociation constant of protein buffer in medium (KB)
		integration_interval_factor = 0.01; // Integration factor
		A_7 = 0.0; // Initial haematocrit
		A_8 = 0.0; // Haematocrit over 1-haematocrit
		// private Double A_9 = 0.0;
		setHb_content(34.0);
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
		Q10Passive = 2.0; // Q10L. Q10 - defines the number of times by which a dynamic process changes with temperature change of 10deg.
		// Q10L (Q10Passive) the factor for leaks. Q10P is the factor for pumps 
		
		
		setInitialCellConcentrations();
		
		this.delta_H = 0.0;
		
		this.total_flux_Na = 0.0;
		this.total_flux_K = 0.0;
		this.total_flux_A = 0.0;
		this.total_flux_H = 0.0;
		this.total_flux_Mg = 0.0;
		this.getNapump().setFluxRev(0.0015);
		
		
		
		this.I_18 = 0.0;
		this.I_73 = 0.0; // Exchange chloride for gluconate (in the medium)
		this.setI_79(0.75); // Water volume (as proportion of total)
		
		
		setInitialMediumConcentrations();

		this.cell.Na.setAmount(0.0);
		this.cell.K.setAmount(0.0);
		this.cell.A.setAmount(0.0);
		this.netChargeHb = 0.0; // Q(Hb-): Qs are 'per litre of cells' - how much -ve charge does Hb provide per litre cells
		this.cell.Hb.setAmount(0.0);
		this.cell.X.setAmount(0.0);
		this.cell.Mgt.setAmount(0.0);
		
		
		

		this.sampling_time = 0.0;
		this.cycle_count = 0;
		this.total_cycle_count = 0;
		this.cycles_per_print = 777;
		this.duration_experiment = 0.0;
		this.n_its = 0;
		this.T_6 = 0.0;
		
		this.setVw(0.0);
		this.VV = 0.0;
		this.fraction = 0.0;
		this.mchc = 0.0;
		this.density = 0.0;
		this.Em = 0.0;
		this.rA = 0.0;
		this.rH = 0.0;
		this.fHb = 0.0;
		this.nHb = 0.0;
		
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
		this.setMgb0(0.0);
		this.cell.Mgt.setAmount(2.5);
		this.cabb = 0.0;
		this.cell.Cat.setConcentration(0.0);
		this.total_flux_Ca = 0.0;
		
		
		this.pka = 0.0;
		this.pkhepes = 0.0;
		this.setBenz2(0.0);
		this.benz2k = 0.0;
		this.setCbenz2(0.0);
		this.setB1ca(0.0);
		this.setB1cak(0.0);
		this.setAlpha(0.0);
		this.setAtp(1.2);
		this.setDpgp(0.0);
		this.BufferType = "HEPES";
		this.setVlysis(1.45);
		this.ff = 0.0;
		this.delta_Mg = 0.0;
		this.delta_Ca = 0.0;

		this.stage = 0;
		
	}
	private void setInitialMediumConcentrations() {
		this.medium.Na.setConcentration(145.0);
		this.medium.K.setConcentration(5.0);
		this.medium.A.setConcentration(145.0);
		this.medium.H.setConcentration(0.0);
		this.buffer_conc = 10.0;
		this.medium.Hb.setConcentration(0.0);
	}
	private void setInitialCellConcentrations() {		
		this.cell.Na.setConcentration(10.0);
		this.cell.K.setConcentration(140.0);
		this.cell.A.setConcentration(95.0);
		this.cell.H.setConcentration(0.0);
		this.cell.Hb.setConcentration(0.0);
		this.cell.X.setConcentration(0.0);
		this.cell.Mgt.setConcentration(0.0);
		this.cell.XHbm.setConcentration(0.0);

	}
	/*
	 * Used in Piezo to produce slightly different output
	 */
	public void setPublishOrder(String[] publish_order) {
		this.publish_order = publish_order;
	}
	public Double getSamplingTime() {
		return this.sampling_time;
	}
	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}
	public void setPublish(boolean publish) {
		this.doPublish = publish; // Used to stop publishing events
	}
	public void output(String ad,JTextArea ta) {
		if(this.verbose) {
			if(ta == null) {
				System.out.println(ad);
			}else {
				ta.append(ad + '\n');
			}
		}
	}
	public Double getDefaultFraction() {
		return this.defaultFraction;
	}
	private void startPiezo() {
		this.piezo.setOldCycles(this.cycles_per_print);
		this.cycles_per_print = this.piezo.getCycles();
		this.cycle_count = this.cycles_per_print - 1; // forces an output now
		
		
		this.piezoGoldman.setPermeability_K(this.piezo.getPkg());
		this.piezoGoldman.setPermeability_Na(this.piezo.getPnag());
		this.piezoGoldman.setPermeability_A(this.piezo.getPag());
		// this.piezoGoldman.setPermeability_Mg(this.piezo.getPMgg());

		
		this.piezoPassiveca.setFcalm(this.piezo.getPcag());
		

		/* 
		 * Change in following line to save the old PMCA as whatever
		 * the current value is, for replacing after transit period
		 */
		this.piezo.setOldPMCA(this.capump.getFcapm());
		
		Double fac = (100.0 - this.piezo.getPmca())/100.0;
		this.capump.setFcapm(this.capump.getDefaultFcapm() * fac);
		
		this.piezo.setOldIF(this.integration_interval_factor);
		this.integration_interval_factor = this.piezo.getiF();
		
		
		Double jsfactor = this.piezo.getPiezoJS();
		oldJSPermeability = this.JS.getPermeability();
		this.JS.setPermeability(this.JS.getDefaultPermeability() * jsfactor);
		
		this.fraction = this.piezo.getPiezoFraction();
		
		if(this.A_7 != this.fraction) {
			this.A_7 = this.fraction;
			this.A_8 = this.A_7/(1.0 - this.A_7);
		}
		System.err.println("Fraction @ Piezo: " + this.fraction);
	}
	private void stopPiezo() {
		
		// Save the final fluxes
		this.finalPiezoFK = this.piezoGoldman.getFlux_K();
		this.finalPiezoFNa = this.piezoGoldman.getFlux_Na();
		this.finalPiezoFA = this.piezoGoldman.getFlux_A();
		this.finalPiezoFCa = this.capump.getFlux_Ca();

		
		this.cycle_count = this.cycles_per_print - 1; // forces an output now
		this.piezoGoldman.setPermeability_K(0.0);
		this.piezoGoldman.setPermeability_Na(0.0);
		this.piezoGoldman.setPermeability_A(0.0);

		this.finalPiezoHct = this.fraction * 100.0;
		this.finalPiezoCCa = this.cell.Cat.getConcentration();
		System.err.println(this.finalPiezoHct);

		
		this.finalPiezoResult = makeResultHash();
		
		//???
		this.piezoPassiveca.setFcalm(0.0);
		this.capump.setFcapm(this.piezo.getOldPMCA());
		

		this.JS.setPermeability(oldJSPermeability);
		
		
		 
		
		
		
		this.fraction = this.defaultFraction;
		if(this.A_7 != this.fraction) {
			this.A_7 = this.fraction;
			this.A_8 = this.A_7/(1.0 - this.A_7);
		}
		
		if(this.piezo.getRestoreMedium()) {
			System.err.println("RESTORING");
			this.restoreMedium();
			this.publish();
		}
		
		
	}
	public ResultHash getFinalPiezoResult() {
		return finalPiezoResult;
	}
	public Double getFinalPiezoHct() {
		return this.finalPiezoHct;
	}
	public Double getFinalPiezoCCa() {
		return this.finalPiezoCCa;
	}
	private void endPiezo() {
		this.cycles_per_print = piezo.getOldCycles();
		this.cycle_count = this.cycles_per_print - 1; // forces an output now
		this.integration_interval_factor = this.piezo.getOldIF();
	}
	public void setLifespan(boolean state) {
		this.lifespan = state;
	}
	private void restoreMedium() {
		
		/*
		 * The code below (commented out) is the version that makes lifespan behave properly
		 */
		if(this.lifespan) {
			this.buffer_conc = this.piezo.getRestoreHepesNa();
			this.medium.setpH(this.piezo.getRestorepH());
			
			
			this.medium.H.setConcentration(Math.pow(10, -this.medium.getpH()));
			this.medium.Hb.setConcentration(this.buffer_conc*(this.medium.H.getConcentration()/(this.A_5 + this.medium.H.getConcentration())));
			
			if(this.piezo.getRestoreNa() > 0) {
				this.medium.Na.setConcentration(this.piezo.getRestoreNa());
			}
			
			if(this.piezo.getRestoreK() > 0) {
				this.medium.K.setConcentration(this.piezo.getRestoreK());
			}
			
			
			this.medium.Caf.setConcentration(this.piezo.getRestoreCa());
			this.medium.Cat.setConcentration(this.piezo.getRestoreCa());
			
			this.medium.Mgf.setConcentration(this.piezo.getRestoreMg());
			this.medium.Mgt.setConcentration(this.piezo.getRestoreMg());
		}else {
			
		
			/*
			 * The following is an alternative, that makes use of the code to setup the cell
			 * fraction options in the DS...
			 */
			
			HashMap<String,String> tempOptions = new HashMap<String,String>();
			tempOptions.put("MMg", ""+this.piezo.getRestoreMg());
			tempOptions.put("MCa", ""+this.piezo.getRestoreCa());
			tempOptions.put("pHo", ""+this.piezo.getRestorepH());
			tempOptions.put("MB",""+this.piezo.getRestoreHepesNa());
			
			
			
			// Uncomment this code if you want to attempt to restore medium K and Na
			if(this.piezo.getRestoreNa() > 0) {
				Double deltaNa = this.piezo.getRestoreNa() - this.getMediumNaConcentration();
				tempOptions.put("NaCl add/remove",""+deltaNa);
			}
			if(this.piezo.getRestoreK() > 0) {
				Double deltaK = this.piezo.getRestoreK() - this.getMediumKConcentration();
				tempOptions.put("KCl add/remove",""+deltaK);
				
			}
			
			this.set_cell_fraction_options(tempOptions, new ArrayList<String>());
		}	
	}
	public void setIsCancelled(boolean state) {
		this.isCancelled = state;
	}
	public boolean getIsCancelled() {
		return this.isCancelled;
	}
	public void runall(JTextArea ta) {
		
		this.setIsCancelled(false);
		
		this.output("RUNNING DS STAGE " + this.stage, ta);
		this.output("Current Sampling time: " + 60.0*this.sampling_time,ta);
		this.output("Running until: " + this.duration_experiment,ta);
				
		this.cycle_count = 0;
		this.n_its = 0;
		this.output("Publishing at t=" + 60.0*this.sampling_time,ta);
		
		this.publish();
		
		ArrayList<MileStone> mileStones = new ArrayList<MileStone>();
		// Note milestones are always in *hours*
		if(this.piezo != null) {
			// We have a piezo stage
			if(this.piezo.getStartTime() > 0.0) {
				// Add the initial output points
				for(int i=0;i<3;i++) { // Only need to loop 3 times as we always publish at the start
					Double output_time = (0.5 + i*0.5)/60.0 + this.sampling_time;
					mileStones.add(new MileStone(output_time,"PUBLISH"));
				}
			}
			
			Double pStart = this.piezo.getStartTime() + this.sampling_time;
			Double pStop = pStart + this.piezo.getDuration();
			Double pEnd = pStop + this.piezo.getRecovery();
			mileStones.add(new MileStone(pStart,"PIEZO START"));
			mileStones.add(new MileStone(pStop, "PIEZO STOP"));
			
			Double endTime = this.duration_experiment/60.0;
			if(pEnd >= endTime) {
				pEnd = endTime - 1e-6;
			}
			mileStones.add(new MileStone(pEnd, "PIEZO END"));

		}
		// Add the END milestone always
		mileStones.add(new MileStone(this.duration_experiment/60.0,"END"));
		

		// Check the ordering
		for(int i=0;i<mileStones.size();i++) {
			System.err.println(mileStones.get(i).getName() + " " + mileStones.get(i).getTime());
		}
		if(mileStones.size() > 1) {
			for(int i=1;i<mileStones.size();i++) {
				if(mileStones.get(i).getTime() <= mileStones.get(i-1).getTime()) {
					System.err.println("MILESTONES NOT IN ORDER");
					return;
				}
			}
		}
		
		if(this.verbose) {
			System.out.println("Milestones OK!");
		}
		int mileStonePos = 0;
		String mileStoneOperation = null;
		
		
		while(this.sampling_time*60 <= this.duration_experiment) {
			if(mileStoneOperation!= null) {
				this.output(mileStoneOperation, ta);
				if(mileStoneOperation.equals("END")) {
					break;
				}
				if(mileStoneOperation.equals("PUBLISH")) {
					this.cycle_count = this.cycles_per_print - 1; // Force a publish
				}
				if(mileStoneOperation.equals("PIEZO START")) {
					startPiezo();
				}
				if(mileStoneOperation.equals("PIEZO STOP")) {
					stopPiezo();
				}
				if(mileStoneOperation.equals("PIEZO END")) {
					endPiezo();
				}
			}
			
			this.getNapump().compute_flux(this.temp_celsius);
			// Temperature dependence of the passive fluxes, uses Q10L
			this.I_18 = Math.exp(((37.0-this.temp_celsius)/10.0)*Math.log(this.Q10Passive));
			this.carriermediated.compute_flux(this.I_18);
			this.cotransport.compute_flux(this.I_18);
			this.JS.compute_flux(this.I_18);
			
			this.Em = this.newton_raphson(new compute_all_fluxes(), this.Em, 0.001, 0.0001, 100, 0, false);


			this.totalionfluxes();
			this.water.compute_flux(this.fHb, this.getCbenz2(), this.buffer_conc, this.edgto, this.I_18);
			
			MileStone nextMileStone = mileStones.get(mileStonePos);
			if(this.integrationInterval(nextMileStone)) { // cycle_count is increased here
				mileStoneOperation = nextMileStone.getName();
				mileStonePos += 1;
				this.cycle_count = this.cycles_per_print; // Force an output
			}else {
				mileStoneOperation = null;
			}
			this.computeDeltas();
			
			this.updateContents();
			
			this.cell.Cat.setConcentration(this.cell.Cat.getAmount()/this.getVw());
			this.setCbenz2(this.getBenz2()/this.getVw());
			
			this.chbetc();
			
			this.rA = this.medium.A.getConcentration() / this.cell.A.getConcentration();
			this.rH = this.cell.H.getConcentration() / this.medium.H.getConcentration();
			if(this.getVw() > this.getVlysis()) {
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
			

			if(this.cycle_count == this.cycles_per_print) {
				this.output("Publishing at t=" + 60.0*this.sampling_time,ta);
				this.publish();
				this.cycle_count = 0;
			}
			
			this.total_cycle_count += 1;
			// Check to see if we've been cancelled
			if(getIsCancelled()) {
				this.output("CANCELLED", ta);
				break;
			}
		}
		this.output("Publishing at t=" + 60.0*this.sampling_time,ta);
		this.publish();
		this.output("Finished!",ta);
		
	}
	public int getTotalCycleCount() {
		return total_cycle_count;
	}
	private void updateContents() {
		Double Vw_old = this.getVw();
		this.setVw(this.getVw() + this.delta_Water);
		this.cell.Hb.setConcentration(this.cell.Hb.getAmount()/this.getVw());
		this.fHb = 1.0 + this.A_2*this.cell.Hb.getConcentration()+this.A_3*Math.pow(this.cell.Hb.getConcentration(),2);
		this.cell.Na.setAmount(this.cell.Na.getAmount() + this.delta_Na);
		this.cell.K.setAmount(this.cell.K.getAmount() + this.delta_K);
		this.cell.A.setAmount(this.cell.A.getAmount() + this.delta_A);
		this.netChargeHb = this.netChargeHb + this.delta_H;

		this.nHb = this.netChargeHb/this.cell.Hb.getAmount();
		this.cell.Mgt.setAmount(this.cell.Mgt.getAmount()+this.delta_Mg);
		this.cell.Cat.setAmount(this.cell.Cat.getAmount() + this.delta_Ca);

		// Cell pH and cell proton concentration
		this.cell.setpH(this.I_74 + this.nHb/this.getA_1());
		this.cell.H.setConcentration(Math.pow(10,(-this.cell.getpH())));
		this.nHb = this.getA_1()*(this.cell.getpH()-this.I_74);
		this.VV = (1-this.A_11) + this.getVw();
		this.mchc = this.getHb_content()/this.VV;
		this.density = (this.getHb_content()/100 + this.getVw())/this.VV;
		this.fraction = this.A_7*this.VV;

		// External concentrations
		Double I_30 = 1 + (this.getVw()-Vw_old)*this.A_8;
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
			if(this.medium.H.getConcentration() < MIN_BUFFER_CONC) {
				this.medium.H.setConcentration(MIN_BUFFER_CONC);
			}
			this.medium.setpH(-Math.log(this.medium.H.getConcentration())/Math.log(10.0));
			if(Double.isNaN((this.medium.getpH()))) {
				System.out.println("Warning: pH = NaN, Buffer conc = " + this.medium.H.getConcentration());
			}
		}

		// Cell concentrations and external concentrations
		this.cell.Na.setConcentration(this.cell.Na.getAmount()/this.getVw());
		this.cell.K.setConcentration(this.cell.K.getAmount()/this.getVw());
		this.cell.A.setConcentration(this.cell.A.getAmount()/this.getVw());

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
		this.delta_Mg = this.total_flux_Mg*this.delta_time;
		this.delta_Ca = this.total_flux_Ca*this.delta_time;
	}
	
	
	private boolean integrationInterval(MileStone nextMileStone) {
		boolean mileStoneFound = false;
		// 8010 Integration interval
		Double I_23 = 10.0 + 10.0*Math.abs(this.a23.getFlux_Mg()+this.total_flux_Ca) + Math.abs(this.goldman.getFlux_Mg() + this.goldman.getFlux_H() + this.piezoGoldman.getFlux_H()) + Math.abs(this.dedgh) + Math.abs(this.total_flux_Na) + Math.abs(this.total_flux_K) + Math.abs(this.total_flux_A) + Math.abs(this.total_flux_H) + Math.abs(this.water.getFlux()*100.0);
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
		if(nextMileStone.check(this.sampling_time + this.delta_time)) {
			// We've passed the milestone
			this.delta_time = nextMileStone.getTime() - this.sampling_time;
			mileStoneFound = true;
		}
//		System.out.println("DT: " + this.delta_time);
		this.sampling_time = this.sampling_time + this.delta_time;
//		System.out.println("ST: " + this.sampling_time);
		this.cycle_count = this.cycle_count + 1;
		this.n_its = this.n_its + 1;
		return mileStoneFound;
	}
	
	private class compute_all_fluxes implements NWRunner {
		public Double run(Double local_em) {
			getGoldman().compute_flux(local_em, temp_celsius,I_18);
			piezoGoldman.compute_flux(local_em, temp_celsius, I_18);
			a23.compute_flux(I_18);
			passiveca.compute_flux(I_18);
			piezoPassiveca.compute_flux(I_18);
			getCaPump().compute_flux();
			totalCaFlux();
			totalFlux();
			return total_flux;
			
		}
	}
	
	public void totalCaFlux() {
		this.total_flux_Ca = this.a23.getFlux_Ca() + this.passiveca.getFlux() + this.piezoPassiveca.getFlux() + this.capump.getFlux_Ca();
	}
	public void totalFlux() {
		Double goldFlux = this.goldman.getFlux_H() + this.goldman.getFlux_Na() + this.goldman.getFlux_K() - this.goldman.getFlux_A();
		Double pGoldFlux = this.piezoGoldman.getFlux_H() + this.piezoGoldman.getFlux_Na() + this.piezoGoldman.getFlux_K() - this.piezoGoldman.getFlux_A();
		this.total_flux = this.getNapump().getTotal_flux() + goldFlux + pGoldFlux + this.capump.getCah()*this.capump.getFlux_Ca() + 2.0*this.passiveca.getFlux() + 2.0*this.piezoPassiveca.getFlux();
	}
	
	public void setup(HashMap<String,String> rsoptions, ArrayList<String> usedoptions) {
		if(this.stage == 0) {
			
			String temp = rsoptions.get("NaCl");
			if(temp != null) {
				this.cell.Na.setConcentration(Double.parseDouble(temp));
				this.cell.Na.setAmount(Double.parseDouble(temp)*this.getVw());
				usedoptions.add("NaCl");
			}

			temp = rsoptions.get("KCl");
			if(temp != null) {
				this.cell.K.setConcentration(Double.parseDouble(temp));
				this.cell.K.setAmount(Double.parseDouble(temp)*this.getVw());
				usedoptions.add("KCl");
			}
			
			System.out.println("Setting up the RS");
			OptionsParsers.naPumpScreenRS(rsoptions,usedoptions,this);
			OptionsParsers.cellwaterscreenRS(rsoptions, usedoptions, this);
			OptionsParsers.cellanionprotonscreenRS(rsoptions, usedoptions, this);
			OptionsParsers.chargeandpiscreenRS(rsoptions, usedoptions, this);
			
			this.cycles_per_print = 777;
			this.setVw(this.getI_79());
			this.fraction = 1e-4; 
			this.defaultFraction = 1e-4;
			this.medium.setpH(7.4);
			this.A_12 = this.medium.getpH();
			this.A_11 = 1.0-this.getHb_content()/136.0;
			this.sampling_time = 0.0;
			
			

			
			
			this.setmgdefaults();
			this.setcadefaults();
			
			OptionsParsers.mgbufferscreenRS(rsoptions, usedoptions,this);
			OptionsParsers.cabufferscreenRS(rsoptions, usedoptions,this);
			
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
					JOptionPane.showMessageDialog(null,"Didn't recognise " + option + " for RS.");
				}
			}
			
			
		}		
	}
	
	public void setupDS(HashMap<String,String> options,ArrayList<String> usedoptions) {
		/*
		 * todo: finish moving options parsers to their own class
		 */
		OptionsParsers.set_screen_time_factor_options(options,usedoptions,this);
		this.set_cell_fraction_options(options, usedoptions);
		OptionsParsers.set_transport_changes_options(options, usedoptions, this);
		this.set_temp_permeability_options(options, usedoptions);
		this.set_piezo_options(options,usedoptions);
		if(this.verbose) { 
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
					JOptionPane.showMessageDialog(null,"Didn't recognise " + option + " for DS - tell Simon!");
				}
			}
		}
		this.stage += 1;
		System.out.println("Setup DS, stage = " + this.stage);
		//this.publish();
		for(String option:options.keySet()) {
			if(!usedoptions.contains(option)) {
				System.out.println("Not used: " + option);
			}
		}
	}
	private void set_piezo_options(HashMap<String,String> options, ArrayList<String> usedoptions) {
		String temp = options.get("Pz stage no or yes");
		if(temp == null) {
			this.piezo = null;
			return;
		}
		if(temp != null) {
			usedoptions.add("Pz stage no or yes");
			if(!temp.equals("yes")) {
				// piezo is off by default
				this.piezo = null;
			}else {
				this.piezo = new Piezo();
				temp = options.get("piezo_start");
				if(temp != null) {
					usedoptions.add("piezo_start");
					// Convert to hours and add to sampling time
					piezo.setStartTime(Double.parseDouble(temp)/60.0);
				}
				temp = options.get("PzOpen state");
				if(temp != null) {
					usedoptions.add("PzOpen state");
//					Double duration_ms = Double.parseDouble(temp);
					Double duration_s = Double.parseDouble(temp);
//					Double duration_s = duration_ms / 1000.0;
					Double duration_m = duration_s / 60.0;
					Double duration_h = duration_m / 60.0;
					piezo.setDuration(duration_h);
				}
				
				temp = options.get("piezo_recovery");
				if(temp != null) {
					usedoptions.add("piezo_recovery");
					piezo.setRecovery(Double.parseDouble(temp)/60.0);
				}
				
				temp = options.get("Pzcyclesperprint");
				if(temp != null) {
					usedoptions.add("Pzcyclesperprint");
					piezo.setCycles(Integer.parseInt(temp));
				}
				
				temp = options.get("PzK");
				if(temp != null) {
					usedoptions.add("PzK");
					piezo.setPkg(Double.parseDouble(temp));
				}
				
				temp = options.get("PzNa");
				if(temp != null) {
					usedoptions.add("PzNa");
					piezo.setPnag(Double.parseDouble(temp));
				}
				
				temp = options.get("PzCa");
				if(temp != null) {
					usedoptions.add("PzCa");
					piezo.setPcag(Double.parseDouble(temp));
				}

				temp = options.get("PzA");
				if(temp != null) {
					usedoptions.add("PzA");
					piezo.setPag(Double.parseDouble(temp));
				}
				
				temp = options.get("Pz PMCA I/S");
				if(temp != null) {
					usedoptions.add("Pz PMCA I/S");
					piezo.setPmca(Double.parseDouble(temp));
				}

				temp = options.get("PzFrequencyFactor");
				if(temp != null) {
					usedoptions.add("PzFrequencyFactor");
					piezo.setiF(Double.parseDouble(temp));
				}
				
				temp = options.get("Pz transit CVF");
				if(temp != null) {
					usedoptions.add("Pz transit CVF");
					piezo.setPiezoFraction(Double.parseDouble(temp));
				}
				
				temp = options.get("Pz JS I/S");
				if(temp != null) {
					Double jsfactor = Double.parseDouble(temp);
					jsfactor = (100.0 - jsfactor)/100.0;
					piezo.setPiezoJS(jsfactor);
					usedoptions.add("Pz JS I/S");
				}
 				temp = options.get("Restore Medium (no/yes)");
 				if(temp != null) {
 					if(temp.equals("yes")) {
 						piezo.setRestoreMedium(true);
 					}else {
 						piezo.setRestoreMedium(false);
 					}
 					usedoptions.add("Restore Medium (no/yes)");
 				}
 				temp = options.get("Restored Medium HEPES-Na concentration");
 				if(temp != null) {
 					piezo.setRestoreHepesNa(Double.parseDouble(temp));
 					usedoptions.add("Restored Medium HEPES-Na concentration");
 				}
 				temp = options.get("Restored Medium pH");
 				if(temp != null) {
 					piezo.setRestorepH(Double.parseDouble(temp));
 					usedoptions.add("Restored Medium pH");
 				}
 				temp = options.get("Restored Medium Na");
 				if(temp != null) {
 					piezo.setRestoreNa(Double.parseDouble(temp));
 					usedoptions.add("Restored Medium Na");
 				}
 				temp = options.get("Restored Medium K");
 				if(temp != null) {
 					piezo.setRestoreK(Double.parseDouble(temp));
 					usedoptions.add("Restored Medium K");
 				}
 				temp = options.get("Restored Medium Mg");
 				if(temp != null) {
 					piezo.setRestoreMg(Double.parseDouble(temp));
 					usedoptions.add("Restored Medium Mg");
 				}
 				temp = options.get("Restored Medium Ca");
 				if(temp != null) {
 					piezo.setRestoreCa(Double.parseDouble(temp));
 					usedoptions.add("Restored Medium Ca");
 				}

 				
			}
		}
	}
	private void set_temp_permeability_options(HashMap<String,String> options, ArrayList<String> usedoptions) {
		Double defaultTemp = this.temp_celsius;
		String temp = options.get("Temperature");
		if(temp != null) {
			this.temp_celsius = Double.parseDouble(temp);
			usedoptions.add("Temperature");
			Double piold = this.getPit0() - (0.016*defaultTemp);
			Double pinew = this.getPit0() - (0.016*this.temp_celsius);
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
		
		temp = options.get("Pw");
		if(temp != null) {
			this.water.setPermeability(OptionValueGenerator.processRequest(temp));
			usedoptions.add("Pw");
		}
		
		temp = options.get("PK");
		if(temp != null) {
			this.goldman.setPermeability_K(OptionValueGenerator.processRequest(temp));
			usedoptions.add("PK");
		}
		
		temp = options.get("pgkh");
		if(temp != null) {
			this.goldman.setPgkh(OptionValueGenerator.processRequest(temp));
			usedoptions.add("pgkh");
		}
		
		temp = options.get("PNa");
		if(temp != null) {
			this.goldman.setPermeability_Na(OptionValueGenerator.processRequest(temp));
			usedoptions.add("PNa");
		}
		
		temp = options.get("PA");
		if(temp != null) {
			this.goldman.setPermeability_A(OptionValueGenerator.processRequest(temp));
			usedoptions.add("PA");
		}
		
		temp = options.get("PH");
		if(temp != null) {
			this.goldman.setPermeability_H(OptionValueGenerator.processRequest(temp));
			usedoptions.add("PH");
		}
		
		// New option Jan 2018
		temp = options.get("PCa");
		if(temp != null) {
			this.passiveca.setFcalm(OptionValueGenerator.processRequest(temp));
			usedoptions.add("PCa");
		}
		
		
		temp = options.get("PA23CaMg");
		if(temp != null) {
			this.a23.setPermeability_Mg(Double.parseDouble(temp));
			usedoptions.add("PA23CaMg");
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
		
		temp = options.get("Hb Deoxy or Re-Oxy");
		if(temp != null) {
			this.I_67 = this.getPit0(); // Store old value
			usedoptions.add("Hb Deoxy or Re-Oxy");
			if(temp.equals("Deoxy") || temp.equals("Re-Oxy")) {
				if(temp.equals("Re-Oxy")) {
					this.setPit0(7.2);
				}else if(temp.equals("Deoxy")) {
					this.setPit0(7.5);
				}else {
					this.setPit0(7.2);
				}
				this.cell.setpH(this.getPit0() - this.I_67 + this.cell.getpH());
				this.cell.H.setConcentration(Math.pow(10.0, -this.cell.getpH()));
				this.I_74 = this.getPit0() - (0.016*this.temp_celsius);
				
				if(temp.equals("Re-Oxy")) {
					this.setAtp(this.getAtp() * 2.0);
					this.setDpgp(this.getDpgp() * 1.7);	
					this.cell.Mgf.setConcentration(this.newton_raphson(new Eqmg(),0.02,0.0001,0.00001,100,0, false));
					this.A_10 = this.nXdefOxy;
					
				}else if(temp.equals("Deoxy")) {
					this.setAtp(this.getAtp() / 2.0);
					this.setDpgp(this.getDpgp() / 1.7);	
					this.cell.Mgf.setConcentration(this.newton_raphson(new Eqmg(),0.02,0.0001,0.00001,100,0, false));
					this.A_10 = this.nXdefOxy - this.mgbal;
				}
			}			
		}
		
	}
	private void set_cell_fraction_options(HashMap<String,String> options, ArrayList<String> usedoptions) {
		String temp = options.get("CVF");
		if(temp != null) {
			this.fraction = Double.parseDouble(temp);
			this.defaultFraction = this.fraction;
			
			usedoptions.add("CVF");
//		}
			/*
			 * Note: this change to overcome the problems that build up over multiple consecutive dynamic states
			 */
		
			if(this.A_7 != this.fraction) {
				this.A_7 = this.fraction;
				this.A_8 = this.A_7/(1.0 - this.A_7);
			}
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
		
		temp = options.get("MB");
		if(temp != null) {
			this.buffer_conc = Double.parseDouble(temp);
			usedoptions.add("MB");
		}
		
		this.A_12 = this.medium.getpH();
		temp = options.get("pHo");
		if(temp != null) {
			this.medium.setpH(Double.parseDouble(temp));
			usedoptions.add("pHo");
		}
		this.phadjust();
		
		temp = options.get("Na x Glucamine");
		if(temp != null) {
			this.I_72 = Double.parseDouble(temp);
			usedoptions.add("Na x Glucamine");
			this.medium.Glucamine.setConcentration(this.medium.Glucamine.getConcentration() + this.I_72);
			this.medium.Na.setConcentration(this.medium.Na.getConcentration() - this.I_72);
		}
		// Should this one be removed once done?
		temp = options.get("A x Gluconate");
		if(temp != null) {
			this.I_73 = Double.parseDouble(temp);
			usedoptions.add("A x Gluconate");
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
		
		temp = options.get("Replace KCl with NaCl");
		if(temp != null) {
			this.I_40 = Double.parseDouble(temp);
			this.medium.Na.setConcentration(this.medium.Na.getConcentration() + this.I_40);
			this.medium.K.setConcentration(this.medium.K.getConcentration() - this.I_40);
			usedoptions.add("Replace KCl with NaCl");
//			options.remove("NaxK");
		}
		
		temp = options.get("Replace NaCl with KCl");
		if(temp != null) {
			this.I_33 = Double.parseDouble(temp);
			this.medium.Na.setConcentration(this.medium.Na.getConcentration() - this.I_33);
			this.medium.K.setConcentration(this.medium.K.getConcentration() + this.I_33);
			usedoptions.add("Replace NaCl with KCl");
		//	options.remove("KxNa");
		}
		
		
		
		
		
		temp = options.get("NaCl add/remove");
		if(temp != null) {
			this.I_45 = Double.parseDouble(temp);
			this.medium.Na.setConcentration(this.medium.Na.getConcentration() + this.I_45);
			this.medium.A.setConcentration(this.medium.A.getConcentration() + this.I_45);
			usedoptions.add("NaCl add/remove");
		}
		
		temp = options.get("KCl add/remove");
		if(temp != null) {
			this.I_34 = Double.parseDouble(temp);
			this.medium.K.setConcentration(this.medium.K.getConcentration() + this.I_34);
			this.medium.A.setConcentration(this.medium.A.getConcentration() + this.I_34);
			usedoptions.add("KCl add/remove");
		}
		
		temp = options.get("Sucrose add/remove");
		if(temp != null) {
			this.I_46 = Double.parseDouble(temp);
			this.medium.Sucrose.setConcentration(this.medium.Sucrose.getConcentration() + this.I_46);
			usedoptions.add("Sucrose add/remove");
		}
		
		temp = options.get("MMg");
		if(temp != null) {
			Double mgtold = this.medium.Mgt.getConcentration();
			this.medium.Mgt.setConcentration(Double.parseDouble(temp));
			usedoptions.add("MMg");
			if(this.medium.Mgt.getConcentration() != 0) {
				this.medium.A.setConcentration(this.medium.A.getConcentration() + 2.0*(this.medium.Mgt.getConcentration() - mgtold));
			}
		}
		
		temp = options.get("MCa");
		if(temp != null) {
			Double catold = this.medium.Cat.getConcentration();
			this.medium.Cat.setConcentration(Double.parseDouble(temp));
			usedoptions.add("MCa");
			if(this.medium.Cat.getConcentration() != 0) {
				this.medium.A.setConcentration(this.medium.A.getConcentration() + 2.0*(this.medium.Cat.getConcentration() - catold));
			}
		}
		
		temp = options.get("EDGTA 0; G(1); D(2)");
		if(temp!=null) {
			usedoptions.add("EDGTA 0; G(1); D(2)");
			this.ligchoice = Double.parseDouble(temp);
		}
		
		temp = options.get("MEDGTA"); // chelator concentration
		if(temp != null) {
			usedoptions.add("MEDGTA");
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
	
	public Double getMediumNaConcentration() {
		return this.medium.Na.getConcentration();
	}
	public Double getMediumKConcentration() {
		return this.medium.K.getConcentration();
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
		
		// Computes n_X (Non-protonizable charge on X (nX))
		this.A_10 = (this.cell.A.getAmount() + 2*this.getBenz2() - (this.cell.Na.getAmount() + this.cell.K.getAmount() + 2*this.cell.Mgt.getAmount() + 2*this.cell.Cat.getAmount()+this.nHb*this.cell.Hb.getAmount()))/this.cell.X.getAmount();
		this.nXdefOxy = this.A_10;
		// Net charge on Hb
		this.netChargeHb = this.nHb*this.cell.Hb.getAmount();

		this.fluxesRS();
		
		this.cycle_count = 0;
		this.n_its = 0;
		this.duration_experiment = 0.0;

		// Set stage to zero everytime the RS is computed - stage = 1 means we're about to start DS 1
		this.stage = 0;
	}
	
	private void fluxesRS() {
		// Flux-rates and RS fluxes
		this.getNapump().compute_permeabilities(this.temp_celsius);
		this.getNapump().setFlux_net();
		this.getNapump().setFlux_K();
		
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
		// this.cotransportmediatedfluxes()
		this.cotransport.compute_flux(this.I_18);
		this.totalionfluxes();

		// fHb is osmotic coefficient of haemoglobin

		this.chbetc();
	}
	
	private void totalionfluxes() {
		// Total ion fluxes
		// Na flux
		this.total_flux_Na = this.getNapump().getFlux_net() + this.carriermediated.getFlux_Na() + this.goldman.getFlux_Na() + this.piezoGoldman.getFlux_Na() + this.cotransport.getFlux_Na();
		// K flux
		this.total_flux_K = this.getNapump().getFlux_K() + this.carriermediated.getFlux_K() + this.goldman.getFlux_K() + this.piezoGoldman.getFlux_K() + this.cotransport.getFlux_K();
		// Anion flux
//		System.out.println(this.goldman.getFlux_A() + "," + this.cotransport.getFlux_A() + "," + this.JS.getFlux_A() + "," + this.carriermediated.getFlux_Na() + this.carriermediated.getFlux_K());
		this.total_flux_A = this.goldman.getFlux_A() + this.piezoGoldman.getFlux_A() + this.cotransport.getFlux_A() + this.JS.getFlux_A() + this.carriermediated.getFlux_Na() + this.carriermediated.getFlux_K();
		// Net proton flux, includes H-flux through Ca pump
//		System.out.println(this.goldman.getFlux_H() + "," + this.JS.getFlux_H() + "," + this.a23.getFlux_Mg() + "," + this.a23.getFlux_Ca() + "," + this.capump.getFlux_H());
		this.total_flux_H = this.JS.getFlux_H() + this.goldman.getFlux_H() + this.piezoGoldman.getFlux_H() - 2*this.a23.getFlux_Mg()-2*this.a23.getFlux_Ca()+this.capump.getFlux_H();
//		System.out.println(this.a23.getPermeability_Ca() + "," + this.a23.getPermeability_Mg());
		this.total_flux_Mg = this.a23.getFlux_Mg() + this.goldman.getFlux_Mg();
	}
	
	private void chbetc() {
		this.cell.Hb.setConcentration(this.cell.Hb.getAmount()/this.getVw());
		this.cell.Mgt.setConcentration(this.cell.Mgt.getAmount()/this.getVw());
		this.cell.X.setConcentration(this.cell.X.getAmount()/this.getVw());
		this.cell.XHbm.setAmount(this.netChargeHb + this.A_10*this.cell.X.getAmount() - 2*this.getBenz2());
		this.cell.XHbm.setConcentration(this.cell.XHbm.getAmount()/this.getVw());
		this.cell.COs.setConcentration(this.fHb*this.cell.Hb.getAmount()/this.getVw());
		// Concentration of charge on Hb
		this.cell.Hbpm.setConcentration(this.nHb*this.cell.Hb.getAmount()/this.getVw());

		// This line doesn't seem to ever be used?
		// this.I_12 = this.cell.Na.getConcentration() + this.cell.K.getConcentration() + this.cell.A.getConcentration() + this.fHb*this.cell.Hb.getConcentration() + this.cell.X.getConcentration() + this.mgf + this.caf + this.cbenz2

		// Sum M
		this.medium.Os.setConcentration(this.medium.Na.getConcentration()+ this.medium.K.getConcentration() + this.medium.A.getConcentration() + this.buffer_conc + this.medium.Gluconate.getConcentration() + this.medium.Glucamine.getConcentration() + this.medium.Sucrose.getConcentration() + (this.medium.Mgf.getConcentration() + this.medium.Caf.getConcentration() + this.edgto));
		
		/*
		 * Does anything use the Os amount?
		 */
		this.cell.Os.setAmount(
				this.cell.Na.getAmount() + 
				this.cell.K.getAmount() + 
				this.cell.A.getAmount() + 
				this.fHb*this.cell.Hb.getAmount() + 
				this.cell.X.getAmount() + 
				this.cell.Mgt.getAmount() + 
				(this.cell.Mgf.getConcentration()+this.cell.Caf.getConcentration())*this.getVw()
				+ this.getBenz2());
		
		/*
		 * Added this code here to stop it having zero as the initial value
		 * 
		 */
		this.cell.Os.setConcentration(
				this.cell.Na.getConcentration() + 
				this.cell.K.getConcentration() + 
				this.cell.A.getConcentration() + 
				this.fHb*this.cell.Hb.getConcentration() + 
				this.cell.X.getConcentration() + 
				this.cell.Mgf.getConcentration() + 
				this.cell.Caf.getConcentration() + 
				this.getCbenz2());
	}
	
	private void secureisonoticityRS() {
		// Secures initial isotonicity and electroneutrality; it computes the
		// QX and nX required for initial osmotic and charge balance.  Since the Mg and
		// Ca buffers are within X, only the unbound forms of Mg and Ca participate in
		// osmotic equilibria within the cell.
		Double summ = this.medium.Na.getConcentration() + this.medium.K.getConcentration() + this.medium.A.getConcentration() + this.buffer_conc + this.medium.Gluconate.getConcentration() + this.medium.Glucamine.getConcentration() + this.medium.Sucrose.getConcentration() + this.medium.Mgt.getConcentration() + this.medium.Cat.getConcentration();
		Double sumq = this.cell.Na.getAmount() + this.cell.K.getAmount() + this.cell.A.getAmount() + (this.cell.Mgf.getConcentration()+this.cell.Caf.getConcentration())*this.getVw() + this.fHb*this.cell.Hb.getAmount() + this.getBenz2();
		this.cell.X.setAmount(this.getVw()*summ - sumq);
			
	}
	
	private void nakamountsmgcaconcRS() {
		// Cell amounts of Na,K,and A, and concentrations of Mg and Ca
		this.cell.Na.setAmount(this.cell.Na.getConcentration()*this.getVw());
		this.cell.K.setAmount(this.cell.K.getConcentration()*this.getVw());
		this.cell.A.setAmount(this.cell.A.getConcentration()*this.getVw());
		this.cell.Mgt.setConcentration(this.cell.Mgt.getAmount()/this.getVw());
		this.cell.Cat.setConcentration(this.cell.Cat.getAmount()/this.getVw());
	}
	
	private void cellphetc() {
		this.rA = this.medium.A.getConcentration()/this.cell.A.getConcentration();
		this.rH = this.rA;
		this.cell.H.setConcentration(this.rH*this.medium.H.getConcentration());
		this.cell.setpH(-Math.log10(this.cell.H.getConcentration()));
		this.Em = -(8.615600000000004e-02)*(273 + this.temp_celsius)*Math.log(this.rA);
		
		// Osmotic coeficient of Hb
		this.fHb = 1 + this.A_2*this.cell.Hb.getAmount()/this.getVw() + this.A_3*Math.pow(this.cell.Hb.getAmount()/this.getVw(),2.0);
		// physiological pI at 37oC;
		this.I_74 = this.getPit0() - (0.016*37);
		// net charge on Hb (Eq/mole)
		this.nHb = this.getA_1()*(this.cell.getpH() - this.I_74);
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
		this.VV = (1-this.A_11) + this.getVw();
		this.mchc = this.getHb_content()/this.VV;
		this.density = (this.getHb_content()/100.0 + this.getVw())/this.VV;
	}
	
	
	
	public void setmgdefaults() {
		this.cell.Mgt.setAmount(2.5);
		this.medium.Mgt.setConcentration(0.2);
		this.setAtp(1.2);
		this.setDpgp(15.0);
		this.VV = (1.0 - this.A_11) + this.getVw();
		Double conc = this.newton_raphson(new Eqmg(), 0.02, 0.0001, 0.00001,100,0, false);
		this.cell.Mgf.setConcentration(conc);
		
		
	}
	
	public void setcadefaults() {
		this.cell.Cat.setAmount(0.000580);
		this.cell.Cat.setConcentration(this.cell.Cat.getAmount()/this.getVw());
		this.medium.Cat.setConcentration(1.0);
		this.setAlpha(0.30);
		this.setB1ca(0.026);
		this.setB1cak(0.014);
		this.setBenz2(0.0);
		this.benz2k = 5e-5;
		this.cell.Caf.setConcentration(1.12e-4);
		
		this.edgto = 0.0;
		this.medium.Mgf.setConcentration(this.medium.Mgt.getConcentration());
		this.medium.Caf.setConcentration(this.medium.Cat.getConcentration());
		
		
	}
	
	
	
	void canr() {
		this.cell.Caf.setConcentration(1000.0*this.cell.Caf.getConcentration());
		this.cell.Cat.setAmount(1000.0*this.cell.Cat.getAmount());
		this.setB1ca(this.getB1ca() * 1000.0);
		this.setB1cak(this.getB1cak() * 1000.0);
		this.setBenz2(this.getBenz2() * 1000.0);
		this.benz2k = this.benz2k * 1000.0;
		Double conc = this.newton_raphson(new Eqca(),this.cell.Caf.getConcentration(),0.000001, 0.000001,100,0, false);
		this.cell.Caf.setConcentration(conc);
		
		this.cell.Caf.setConcentration(this.cell.Caf.getConcentration()/1000.0);
		this.cell.Cat.setAmount(this.cell.Cat.getAmount()/1000.0);
		this.setB1ca(this.getB1ca()/1000.0);
		this.setB1cak(this.getB1cak()/1000.0);
		this.setBenz2(this.getBenz2()/1000.0);
		this.benz2k=this.benz2k/1000.0;
	}
	
	private class Eqca implements NWRunner {
		public Double run(Double local_x6) {
			cabb = local_x6*(Math.pow(getAlpha(),-1.0))+((getB1ca()/VV)*local_x6/(getB1cak()+local_x6))+((getBenz2()/VV)*local_x6/(benz2k+local_x6));
			Double y = cell.Cat.getAmount() - cabb;
			return y;
		}
	}
	
	public class Eqmg implements NWRunner {
		public Double run(Double local_mgf) {
			mgb = getMgb0() + ((getAtp()/VV)*local_mgf/(0.08+local_mgf)) + ((getDpgp()/VV)*local_mgf/(3.6+local_mgf));
			Double y = cell.Mgt.getAmount() - local_mgf*(getVw()/(getVw()+getHb_content()/136.0)) - mgb;
			return y;
		}
	}
	Double newton_raphson(NWRunner r, Double initial, Double step, Double stop,Integer max_its, Integer initial_its, boolean verbose) {
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
				System.out.println("It: " + no_its + ", X_3: " + X_3 + ", S: " + S + ", Y/S: " + Y_1/S);
			}
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
		return X_3;
	}
	
	public ResultHash makeResultHash() {
		ResultHash new_result = new ResultHash(this.sampling_time*60.0); // convert to minutes for publishing
		new_result.setItem("Vw",this.getVw());
		new_result.setItem("V/V",this.VV);
		new_result.setItem("CVF",this.fraction);
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
		new_result.setItem("CH/nM", 1e9*Math.pow(10.0,-this.cell.getpH()));
		new_result.setItem("CCa2+",this.cell.Caf.getConcentration());
		new_result.setItem("CMg2+",this.cell.Mgf.getConcentration());
		new_result.setItem("CHb",this.cell.Hb.getConcentration());
		new_result.setItem("CX-",this.cell.X.getConcentration());
		new_result.setItem("nX", this.A_10);
		new_result.setItem("nX*CX-", this.A_10 * this.cell.X.getConcentration());
		new_result.setItem("COs",this.cell.Os.getConcentration());
		new_result.setItem("MOs",this.medium.Os.getConcentration());
		new_result.setItem("rA",this.rA);
		new_result.setItem("rH",this.rH);
		new_result.setItem("fHb",this.fHb);
		new_result.setItem("nHb",this.nHb);
		new_result.setItem("MNa",this.medium.Na.getConcentration());
		new_result.setItem("MK",this.medium.K.getConcentration());
		new_result.setItem("MA",this.medium.A.getConcentration());
		new_result.setItem("MH", 1e9*Math.pow(10.0, -this.medium.getpH()));
		new_result.setItem("MB", this.buffer_conc);
		new_result.setItem("MCat", this.medium.Cat.getConcentration());
		new_result.setItem("MCa2+", this.medium.Caf.getConcentration());
		new_result.setItem("MMgt", this.medium.Mgt.getConcentration());
		new_result.setItem("MMg2+", this.medium.Mgf.getConcentration());
		new_result.setItem("FNaP",this.getNapump().getFlux_net());
		new_result.setItem("FCaP",this.capump.getFlux_Ca());
		new_result.setItem("FHCaP", -2.0*this.capump.getFlux_Ca());
		new_result.setItem("FKP",this.getNapump().getFlux_K());
		new_result.setItem("FNa",this.total_flux_Na);
		new_result.setItem("FK",this.total_flux_K);
		new_result.setItem("FA",this.total_flux_A);
		new_result.setItem("FH",this.total_flux_H);
		new_result.setItem("FCa",this.total_flux_Ca);
		new_result.setItem("FW",this.water.getFlux());
		new_result.setItem("FNaG",this.goldman.getFlux_Na());
		new_result.setItem("FKG",this.goldman.getFlux_K());
		new_result.setItem("FAG",this.goldman.getFlux_A());
		new_result.setItem("FHG",this.goldman.getFlux_H());	
		new_result.setItem("FMgG", this.goldman.getFlux_Mg());
		new_result.setItem("FCaG", this.passiveca.getFlux());
		new_result.setItem("FAJS", this.JS.getFlux_A());
		new_result.setItem("FHJS", this.JS.getFlux_H());
		new_result.setItem("FA23Ca", this.a23.getFlux_Ca());
		new_result.setItem("FA23Mg", this.a23.getFlux_Mg());
		Double V_14 = -this.goldman.getRtoverf()*Math.log(this.medium.A.getConcentration()/this.cell.A.getConcentration());
		new_result.setItem("EA", V_14);
		Double V_13 = this.goldman.getRtoverf()*Math.log(this.medium.H.getConcentration()/this.cell.H.getConcentration());
		new_result.setItem("EH", V_13);
		Double V_15 = this.goldman.getRtoverf() * Math.log(this.medium.K.getConcentration()/this.cell.K.getConcentration());
		new_result.setItem("EK", V_15);
		Double V_16 = this.goldman.getRtoverf()*Math.log(this.medium.Na.getConcentration()/this.cell.Na.getConcentration());
		new_result.setItem("ENa", V_16);
		new_result.setItem("FKGgardos", this.goldman.computeFKGardos(I_18));
		new_result.setItem("FzK", this.piezoGoldman.getFlux_K());
		new_result.setItem("FzNa", this.piezoGoldman.getFlux_Na());
		new_result.setItem("FzA", this.piezoGoldman.getFlux_A());
		new_result.setItem("FzCa", this.piezoPassiveca.getFlux());
		
		new_result.setItem("MEDGTA",this.edgto);
		new_result.setItem("FClCo", this.cotransport.getFlux_A());
		new_result.setItem("FKCo", this.cotransport.getFlux_K());
		new_result.setItem("FNaCo", this.cotransport.getFlux_Na());
		
		
		new_result.setItem("fHb*CHb", this.fHb*this.cell.Hb.getConcentration());
		new_result.setItem("nHb*CHb", this.nHb*this.cell.Hb.getConcentration());
		new_result.setItem("Msucrose", this.medium.Sucrose.getConcentration());
		new_result.setItem("Mgluconate", this.medium.Gluconate.getConcentration());
		new_result.setItem("Mglucamine", this.medium.Glucamine.getConcentration());
		
		
		new_result.setItem("TransitHct", this.finalPiezoHct);
		
		
		new_result.setItem("FzKGTransit", this.finalPiezoFK);
		new_result.setItem("FzNaGTransit", this.finalPiezoFNa);
		new_result.setItem("FzAGTransit", this.finalPiezoFA);
		new_result.setItem("FzCaGTransit", this.finalPiezoFCa);
		
		double enFluxTest = this.total_flux_Na
				+ this.total_flux_K
				+ this.total_flux_H
				- this.total_flux_A
				// + 2.0*(this.total_flux_Ca+this.a23.getFlux_Mg()); //TODO: what happens here?
				// Guess:
				+ 2.0*(this.total_flux_Ca + this.total_flux_K);
		new_result.setItem("EN test", enFluxTest);
		
		return new_result;
	}
	
	public void publish() {
		if(!this.doPublish) {
			return;
		}
		
		System.out.println("Publishing at time: " + this.sampling_time);
		ResultHash new_result = makeResultHash();
		

		this.resultList.add(new_result);
	}
	public void clearResults() {
		/*
		 * Method used to wipe the arraylist to avoid
		 * clogging up memory with enormous quantities of dirt.
		 */
		this.resultList = new ArrayList<ResultHash>(); 
	}
	public ArrayList<ResultHash> getResults() {
		return this.resultList;
	}
	public ResultHash getLastResult() {
		return this.resultList.get(this.resultList.size()-1);
	}
	public void setResults(ArrayList<ResultHash> resultList) {
		this.resultList = resultList;
	}
	public String[] getPublishOrder() {
		return this.publish_order;
	}
	
	public void writeCsv(String name, ArrayList<ResultHash> resultList) {
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
			String numFormat = "%7." + this.dp + "f";
			for(ResultHash r: resultList) {
				resultString = String.format(numFormat,r.getTime());
				for(int i=0;i<this.publish_order.length;i++) {
					resultString += ',' + String.format(numFormat, r.getItem(this.publish_order[i]));
				}
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
	public void writeCsv(String name) {
		writeCsv(name,this.resultList);
	}

	
	/*
	 * Getters and setters from here
	 */
	public NaPump getNapump() {
		return napump;
	}

	public void setNapump(NaPump napump) {
		this.napump = napump;
	}

	public int getStage() {
		return this.stage;
	}
	public Double getDuration_experiment() {
		return duration_experiment;
	}
	public void setDuration_experiment(Double duration_experiment) {
		this.duration_experiment = duration_experiment;
	}
	public int getDp() {
		return dp;
	}
	public void setDp(int dp) {
		this.dp = dp;
	}
	public Double getI_43() {
		return I_43;
	}
	public void setI_43(Double i_43) {
		I_43 = i_43;
	}
	public Double getIntegration_interval_factor() {
		return integration_interval_factor;
	}
	public void setIntegration_interval_factor(Double integration_interval_factor) {
		this.integration_interval_factor = integration_interval_factor;
	}
	public boolean isCompute_delta_time() {
		return compute_delta_time;
	}
	public void setCompute_delta_time(boolean compute_delta_time) {
		this.compute_delta_time = compute_delta_time;
	}
	public double getDelta_time() {
		return delta_time;
	}
	public void setDelta_time(double delta_time) {
		this.delta_time = delta_time;
	}
	public Integer getCycles_per_print() {
		return cycles_per_print;
	}
	public void setCycles_per_print(Integer cycles_per_print) {
		this.cycles_per_print = cycles_per_print;
	}
	public CarrierMediated getCarriermediated() {
		return carriermediated;
	}
	public void setCarriermediated(CarrierMediated carriermediated) {
		this.carriermediated = carriermediated;
	}
	public Cotransport getCotransport() {
		return cotransport;
	}
	public void setCotransport(Cotransport cotransport) {
		this.cotransport = cotransport;
	}
	public JacobsStewart getJS() {
		return JS;
	}
	public void setJS(JacobsStewart jS) {
		JS = jS;
	}
	public CaPumpMg2 getCaPump() {
		return capump;
	}
	public void setCapump(CaPumpMg2 capump) {
		this.capump = capump;
	}
	public Goldman getGoldman() {
		return goldman;
	}
	public void setGoldman(Goldman goldman) {
		this.goldman = goldman;
	}
	public Double getHb_content() {
		return hb_content;
	}
	public void setHb_content(Double hb_content) {
		this.hb_content = hb_content;
	}
	public Double getI_79() {
		return I_79;
	}
	public void setI_79(Double i_79) {
		I_79 = i_79;
	}
	public Double getVlysis() {
		return vlysis;
	}
	public void setVlysis(Double vlysis) {
		this.vlysis = vlysis;
	}
	public Double getA_1() {
		return A_1;
	}
	public void setA_1(Double a_1) {
		A_1 = a_1;
	}
	public Double getPit0() {
		return pit0;
	}
	public void setPit0(Double pit0) {
		this.pit0 = pit0;
	}
	public Double getMgb0() {
		return mgb0;
	}
	public void setMgb0(Double mgb0) {
		this.mgb0 = mgb0;
	}
	public Double getAtp() {
		return atp;
	}
	public void setAtp(Double atp) {
		this.atp = atp;
	}
	public Double getDpgp() {
		return dpgp;
	}
	public void setDpgp(Double dpgp) {
		this.dpgp = dpgp;
	}
	public Double getB1ca() {
		return b1ca;
	}
	public void setB1ca(Double b1ca) {
		this.b1ca = b1ca;
	}
	public Double getB1cak() {
		return b1cak;
	}
	public void setB1cak(Double b1cak) {
		this.b1cak = b1cak;
	}
	public Double getAlpha() {
		return alpha;
	}
	public void setAlpha(Double alpha) {
		this.alpha = alpha;
	}
	public Double getBenz2() {
		return benz2;
	}
	public void setBenz2(Double benz2) {
		this.benz2 = benz2;
	}
	public Double getCbenz2() {
		return cbenz2;
	}
	public void setCbenz2(Double cbenz2) {
		this.cbenz2 = cbenz2;
	}
	public Double getVw() {
		return Vw;
	}
	public void setVw(Double vw) {
		Vw = vw;
	}
}
