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
	
	private String[] publish_order = {"V/V","Vw","Hct","Em","pHi","pHo","MCHC",
	                                  "Density","QNa","QK","QA","QCa","QMg","CNa","CK","CA","CH/nM","CCa2+","CMg2+",
	                                  "CX","CHb","fHb","COs","MOs","rA","rH","nHb","MNa","MK","MA","MH/nM","MB","MCat","MCaf",
	                                  "MMgt","MMgf","FNaP","FACo","FKCo","FNaCo","FCaP","FKP","FNa","FKGgardos","FKG","FK",
	                                  "FA","FH","FCa","FW","FNaG","FAG","FHG","FCaG","FAJS","FHJS","FA23Ca","FA23Mg",
	                                  "EA","EH","EK","ENa","FzKG","FzNaG","FzAG","FzCaG","fHb*CHb","nX","Msucr","Mgluc-",
	                                  "Mgluc+","EN test"};
	private ArrayList<ResultHash> resultList = new ArrayList<ResultHash>();
	
	private boolean verbose = true;
	private boolean doPublish = true;
	
	private final double MIN_BUFFER_CONC = 1e-10;
	
	// Model components
	private Region cell;
	private Region medium;
	private JacobsStewart JS;
	private Cotransport cotransport;
	private NaPump napump;
	private CarrierMediated carriermediated;
	private Goldman goldman;
	private PiezoGoldman piezoGoldman;
	private A23187 a23;
	private WaterFlux water;
	private PassiveCa passiveca;
	private PiezoPassiveCa piezoPassiveca;
	private CaPumpMg2 capump;
	
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
	private Double Q10Passive; // was B10, B_10, B[10] // ADD TO TAB FOR PERMEABILITIES
	
	private Double delta_H;
	
	
	private Double total_flux_Na;
	private Double total_flux_K;
	private Double total_flux_A;
	private Double total_flux_H;
	
		
	
	private Double I_18;
	private Double I_73;
	private Double I_79; 
	
	private Double buffer_conc;
	private Double Q_4;
	

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
		Q10Passive = 2.0; // Q10L. Q10 - defines the number of times by which a dynamic process changes with temperature change of 10deg.
		// Q10L (Q10Passive) the factor for leaks. Q10P is the factor for pumps 
		
		
		setInitialCellConcentrations();
		
		this.delta_H = 0.0;
		
		this.total_flux_Na = 0.0;
		this.total_flux_K = 0.0;
		this.total_flux_A = 0.0;
		this.total_flux_H = 0.0;
		this.getNapump().setFluxRev(0.0015);
		
		
		
		this.I_18 = 0.0;
		this.I_73 = 0.0; // Exchange chloride for gluconate (in the medium)
		this.I_79 = 0.75; // Water volume (as proportion of total)
		
		
		setInitialMediumConcentrations();

		this.cell.Na.setAmount(0.0);
		this.cell.K.setAmount(0.0);
		this.cell.A.setAmount(0.0);
		this.Q_4 = 0.0; // Q(Hb-): Qs are 'per litre of cells' - how much -ve charge does Hb provide per litre cells
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
		this.cell.Mgt.setAmount(2.5);
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
		
		
//		this.piezo.setOldPKG(this.goldman.getPermeability_K());
		this.piezoGoldman.setPermeability_K(this.piezo.getPkg());
//		this.piezo.setOldPNaG(this.goldman.getPermeability_Na());
		this.piezoGoldman.setPermeability_Na(this.piezo.getPnag());
//		this.piezo.setOldPAG(this.goldman.getPermeability_A());
		this.piezoGoldman.setPermeability_A(this.piezo.getPag());

		
		// Do we need another of these???
		// Yes - swap for piezoPassiveCa...
//		this.piezo.setOldPCaG(this.passiveca.getFcalm());
		this.piezoPassiveca.setFcalm(this.piezo.getPcag());
		

		// This stays the same?
		this.piezo.setOldPMCA(this.capump.getDefaultFcapm());
		Double fac = (100.0 - this.piezo.getPmca())/100.0;
		this.capump.setFcapm(this.capump.getDefaultFcapm() * fac);
		
		this.piezo.setOldIF(this.integration_interval_factor);
		this.integration_interval_factor = this.piezo.getiF();
		
		
		Double jsfactor = this.piezo.getPiezoJS();
		oldJSPermeability = this.JS.getPermeability();
		this.JS.setPermeability(this.JS.getDefaultPermeability() * jsfactor);
			
		
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
		
		
//		this.JS.setPermeability(this.JS.getDefaultPermeability());
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
	
	private void restoreMedium() {
		
		/*
		 * The code below (commented out) is the version that makes lifespan behave properly
		 */
//		this.buffer_conc = this.piezo.getRestoreHepesNa();
//		this.medium.setpH(this.piezo.getRestorepH());
//		
//		
//		this.medium.H.setConcentration(Math.pow(10, -this.medium.getpH()));
//		this.medium.Hb.setConcentration(this.buffer_conc*(this.medium.H.getConcentration()/(this.A_5 + this.medium.H.getConcentration())));
//		
//		if(this.piezo.getRestoreNa() > 0) {
//			this.medium.Na.setConcentration(this.piezo.getRestoreNa());
//		}
//		
//		if(this.piezo.getRestoreK() > 0) {
//			this.medium.K.setConcentration(this.piezo.getRestoreK());
//		}
//		
//		
//		this.medium.Caf.setConcentration(this.piezo.getRestoreCa());
//		this.medium.Cat.setConcentration(this.piezo.getRestoreCa());
//		
//		this.medium.Mgf.setConcentration(this.piezo.getRestoreMg());
//		this.medium.Mgt.setConcentration(this.piezo.getRestoreMg());
//		
		
		/*
		 * The following is an alternative, that makes use of the code to setup the cell
		 * fraction options in the DS...
		 */
		
		HashMap<String,String> tempOptions = new HashMap<String,String>();
		tempOptions.put("Mg concentration", ""+this.piezo.getRestoreMg());
		tempOptions.put("Ca concentration", ""+this.piezo.getRestoreCa());
		tempOptions.put("Medium pH", ""+this.piezo.getRestorepH());
		tempOptions.put("HEPES-Na concentration",""+this.piezo.getRestoreHepesNa());
		
		
		
		// Uncomment this code if you want to attempt to restore medium K and Na
		if(this.piezo.getRestoreNa() > 0) {
			Double deltaNa = this.piezo.getRestoreNa() - this.getMediumNaConcentration();
			tempOptions.put("Add or remove NaCl",""+deltaNa);
		}
		if(this.piezo.getRestoreK() > 0) {
			Double deltaK = this.piezo.getRestoreK() - this.getMediumKConcentration();
			tempOptions.put("Add or remove KCl",""+ deltaK);
			
		}
		
		this.set_cell_fraction_options(tempOptions, new ArrayList<String>());
		
	}
	public void runall(JTextArea ta) {
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
			this.water.compute_flux(this.fHb, this.cbenz2, this.buffer_conc, this.edgto, this.I_18);
			
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
				this.output("Publishing at t=" + 60.0*this.sampling_time,ta);
				this.publish();
				this.cycle_count = 0;
			}
			
			this.total_cycle_count += 1;
		}
		this.output("Publishing at t=" + 60.0*this.sampling_time,ta);
		this.publish();
		this.output("Finished!",ta);
		
	}
	public int getTotalCycleCount() {
		return total_cycle_count;
	}
	private void updateContents() {
		Double Vw_old = this.Vw;
		this.Vw = this.Vw + this.delta_Water;
		this.cell.Hb.setConcentration(this.cell.Hb.getAmount()/this.Vw);
		this.fHb = 1.0 + this.A_2*this.cell.Hb.getConcentration()+this.A_3*Math.pow(this.cell.Hb.getConcentration(),2);
		this.cell.Na.setAmount(this.cell.Na.getAmount() + this.delta_Na);
		this.cell.K.setAmount(this.cell.K.getAmount() + this.delta_K);
		this.cell.A.setAmount(this.cell.A.getAmount() + this.delta_A);
		this.Q_4 = this.Q_4 + this.delta_H;

		this.nHb = this.Q_4/this.cell.Hb.getAmount();
		this.cell.Mgt.setAmount(this.cell.Mgt.getAmount()+this.delta_Mg);
		this.cell.Cat.setAmount(this.cell.Cat.getAmount() + this.delta_Ca);

		// Cell pH and cell proton concentration
		this.cell.setpH(this.I_74 + this.nHb/this.A_1);
		this.cell.H.setConcentration(Math.pow(10,(-this.cell.getpH())));
		this.nHb = this.A_1*(this.cell.getpH()-this.I_74);
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
			if(this.medium.H.getConcentration() < MIN_BUFFER_CONC) {
				this.medium.H.setConcentration(MIN_BUFFER_CONC);
			}
			this.medium.setpH(-Math.log(this.medium.H.getConcentration())/Math.log(10.0));
			if(Double.isNaN((this.medium.getpH()))) {
				System.out.println("Warning: pH = NaN, Buffer conc = " + this.medium.H.getConcentration());
			}
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
	
	
	private boolean integrationInterval(MileStone nextMileStone) {
		boolean mileStoneFound = false;
		// 8010 Integration interval
		Double I_23 = 10.0 + 10.0*Math.abs(this.a23.getFlux_Mg()+this.total_flux_Ca) + Math.abs(this.goldman.getFlux_H() + this.piezoGoldman.getFlux_H()) + Math.abs(this.dedgh) + Math.abs(this.total_flux_Na) + Math.abs(this.total_flux_K) + Math.abs(this.total_flux_A) + Math.abs(this.total_flux_H) + Math.abs(this.water.getFlux()*100.0);
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
				this.cell.Na.setAmount(Double.parseDouble(temp)*this.Vw);
				usedoptions.add("NaCl");
			}

			temp = rsoptions.get("KCl");
			if(temp != null) {
				this.cell.K.setConcentration(Double.parseDouble(temp));
				this.cell.K.setAmount(Double.parseDouble(temp)*this.Vw);
				usedoptions.add("KCl");
			}
			
			System.out.println("Setting up the RS");
			this.naPumpScreenRS(rsoptions,usedoptions);
			this.cellwaterscreenRS(rsoptions, usedoptions);
			this.cellanionprotonscreenRS(rsoptions, usedoptions);
			this.chargeandpiscreenRS(rsoptions, usedoptions);
			
			this.cycles_per_print = 777;
			this.Vw = this.I_79;
			this.fraction = 1e-4; 
			this.defaultFraction = 1e-4;
			this.medium.setpH(7.4);
			this.A_12 = this.medium.getpH();
			this.A_11 = 1.0-this.hb_content/136.0;
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
					JOptionPane.showMessageDialog(null,"Didn't recognise " + option + " for RS - tell Simon!");
				}
			}
			
			
		}		
		// this.publish();
	}
	
	public void setupDS(HashMap<String,String> options,ArrayList<String> usedoptions) {
		/*
		 * todo: finish moving options parsers to their own class
		 */
//		this.set_screen_time_factor_options(options, usedoptions);
		OptionsParsers.set_screen_time_factor_options(options,usedoptions,this);
		this.set_cell_fraction_options(options, usedoptions);
//		this.set_transport_changes_options(options, usedoptions);
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
	}
	private void set_piezo_options(HashMap<String,String> options, ArrayList<String> usedoptions) {
		String temp = options.get("Incorporate PIEZO stage");
		if(temp == null) {
			this.piezo = null;
			return;
		}
		if(temp != null) {
			usedoptions.add("Incorporate PIEZO stage");
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
				temp = options.get("Open state");
				if(temp != null) {
					usedoptions.add("Open state");
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
				
				temp = options.get("Piezo Cycles per print");
				if(temp != null) {
					usedoptions.add("Piezo Cycles per print");
					piezo.setCycles(Integer.parseInt(temp));
				}
				
				temp = options.get("PzKG");
				if(temp != null) {
					usedoptions.add("PzKG");
					piezo.setPkg(Double.parseDouble(temp));
				}
				
				temp = options.get("PzNaG");
				if(temp != null) {
					usedoptions.add("PzNaG");
					piezo.setPnag(Double.parseDouble(temp));
				}
				
				temp = options.get("PzCaG");
				if(temp != null) {
					usedoptions.add("PzCaG");
					piezo.setPcag(Double.parseDouble(temp));
				}

				temp = options.get("PzAG");
				if(temp != null) {
					usedoptions.add("PzAG");
					piezo.setPag(Double.parseDouble(temp));
				}
				
				temp = options.get("PMCA inhibition");
				if(temp != null) {
					usedoptions.add("PMCA inhibition");
					piezo.setPmca(Double.parseDouble(temp));
				}

				temp = options.get("Piezo Frequency factor");
				if(temp != null) {
					usedoptions.add("Piezo Frequency factor");
					piezo.setiF(Double.parseDouble(temp));
				}
				
				temp = options.get("Transit cell volume fraction");
				if(temp != null) {
					usedoptions.add("Transit cell volume fraction");
					piezo.setPiezoFraction(Double.parseDouble(temp));
				}
				
				temp = options.get("Piezo JS Inhibition/Stimulation");
				if(temp != null) {
					Double jsfactor = Double.parseDouble(temp);
					jsfactor = (100.0 - jsfactor)/100.0;
					piezo.setPiezoJS(jsfactor);
					usedoptions.add("Piezo JS Inhibition/Stimulation");
				}
 				temp = options.get("Restore Medium");
 				if(temp != null) {
 					if(temp.equals("yes")) {
 						piezo.setRestoreMedium(true);
 					}else {
 						piezo.setRestoreMedium(false);
 					}
 					usedoptions.add("Restore Medium");
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
 				temp = options.get("Resotred Medium Ca");
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
		
		temp = options.get("Water permeability");
		if(temp != null) {
			this.water.setPermeability(Double.parseDouble(temp));
			usedoptions.add("Water permeability");
		}
		
		temp = options.get("PKG");
		if(temp != null) {
			this.goldman.setPermeability_K(Double.parseDouble(temp));
			usedoptions.add("PKG");
		}
		
		temp = options.get("pgkh");
		if(temp != null) {
			this.goldman.setPgkh(Double.parseDouble(temp));
			usedoptions.add("pgkh");
		}
		
		temp = options.get("PNaG");
		if(temp != null) {
			this.goldman.setPermeability_Na(Double.parseDouble(temp));
			usedoptions.add("PNaG");
		}
		
		temp = options.get("PAG");
		if(temp != null) {
			this.goldman.setPermeability_A(Double.parseDouble(temp));
			usedoptions.add("PAG");
		}
		
		temp = options.get("PHG");
		if(temp != null) {
			this.goldman.setPermeability_H(Double.parseDouble(temp));
			usedoptions.add("PHG");
		}
		
		// New option Jan 2018
		temp = options.get("PCaG");
		if(temp != null) {
			this.passiveca.setFcalm(Double.parseDouble(temp));
			usedoptions.add("PCaG");
		}
		
		
		temp = options.get("PA23187CaMg");
		if(temp != null) {
			this.a23.setPermeability_Mg(Double.parseDouble(temp));
			usedoptions.add("PA23187CaMg");
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
		
		temp = options.get("Hb pI(0oC) 7.2(Oxy) or 7.5(Deoxy)");
		if(temp != null) {
			this.I_67 = this.pit0; // Store old value
			usedoptions.add("Hb pI(0oC) 7.2(Oxy) or 7.5(Deoxy)");
			if(temp.equals("Oxy")) {
				this.pit0 = 7.2;
			}else if(temp.equals("Deoxy")) {
				this.pit0 = 7.5;
			}else {
				this.pit0 = 7.2;
			}
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
	// Obsolete method - remove me
	private void set_transport_changes_options(HashMap<String,String> options, ArrayList<String> usedoptions) {
		String temp = options.get("na-pump-flux-change");
		if(temp != null) {
			Double inhibFac = (100.0-Double.parseDouble(temp))/100.0;
			this.getNapump().setP_1(this.getNapump().getDefaultP_1()*inhibFac);
			usedoptions.add("na-pump-flux-change");
		}
		
		temp = options.get("na-pump-reverse-flux-change");
		if(temp != null) {
			this.getNapump().setP_2(Double.parseDouble(temp));
			usedoptions.add("na-pump-reverse-flux-change");
		}
		temp = options.get("naa-change");
		if(temp != null) {
			this.carriermediated.setPermeability_Na(this.carriermediated.getDefaultPermability_Na()*Double.parseDouble(temp)/100.0);
			usedoptions.add("naa-change");
		}
		temp = options.get("ka-change");
		if(temp != null) {
			this.carriermediated.setPermeability_K(this.carriermediated.getDefaultPermeability_K() * Double.parseDouble(temp)/100.0);
			usedoptions.add("ka-change");
		}
		// Check this one with Arieh
		// Removed from GUI at the moment....
		temp = options.get("cotransport-activation");
		if(temp != null) {
			Double co_f = Double.parseDouble(temp);
			this.cotransport.setPermeability(0.0002 * co_f / 100.0);
			usedoptions.add("cotransport-activation");
		}
		temp = options.get("js-stimulation-inhibition");
		if(temp != null) {
			Double jsfactor = Double.parseDouble(temp);
			jsfactor = (100.0 - jsfactor)/100.0;
			this.JS.setPermeability(this.JS.getDefaultPermeability() * jsfactor);
			usedoptions.add("js-stimulation-inhibition");
		}
		temp = options.get("ca-pump-vmax-change");
		if(temp != null) {
			Double fc = (100.0 - Double.parseDouble(temp))/100.0;
			this.capump.setFcapm(this.capump.getDefaultFcapm() * fc);
			usedoptions.add("ca-pump-vmax-change");
		}
		
//		temp = options.get("vmax-leak-change");
//		if(temp != null) {
//			this.passiveca.setFcalm(this.passiveca.getFcalm()*Double.parseDouble(temp));
//			usedoptions.add("vmax-leak-change");
//		}
		
		temp = options.get("percentage-inhibition");
		if(temp != null) {
			Double gc = (100.0 - Double.parseDouble(temp))/100.0;
			this.goldman.setPkm(this.goldman.getDefaultPkm() * gc);
			usedoptions.add("percentage-inhibition");
		}
	}
	
	private void set_cell_fraction_options(HashMap<String,String> options, ArrayList<String> usedoptions) {
		String temp = options.get("Cell volume fraction");
		if(temp != null) {
			this.fraction = Double.parseDouble(temp);
			this.defaultFraction = this.fraction;
			
			usedoptions.add("Cell volume fraction");
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
		
		temp = options.get("HEPES-Na concentration");
		if(temp != null) {
			this.buffer_conc = Double.parseDouble(temp);
			usedoptions.add("HEPES-Na concentration");
		}
		
		this.A_12 = this.medium.getpH();
		temp = options.get("Medium pH");
		if(temp != null) {
			this.medium.setpH(Double.parseDouble(temp));
			usedoptions.add("Medium pH");
		}
		this.phadjust();
		
		temp = options.get("Exchange Na for Glucamine");
		if(temp != null) {
			this.I_72 = Double.parseDouble(temp);
			usedoptions.add("Exchange Na for Glucamine");
			this.medium.Glucamine.setConcentration(this.medium.Glucamine.getConcentration() + this.I_72);
			this.medium.Na.setConcentration(this.medium.Na.getConcentration() - this.I_72);
		}
		// Should this one be removed once done?
		temp = options.get("Exchange Cl(A) for gluconate");
		if(temp != null) {
			this.I_73 = Double.parseDouble(temp);
			usedoptions.add("Exchange Cl(A) for gluconate");
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
		
		
		
		
		
		temp = options.get("Add or remove NaCl");
		if(temp != null) {
			this.I_45 = Double.parseDouble(temp);
			this.medium.Na.setConcentration(this.medium.Na.getConcentration() + this.I_45);
			this.medium.A.setConcentration(this.medium.A.getConcentration() + this.I_45);
			usedoptions.add("Add or remove NaCl");
		}
		
		temp = options.get("Add or remove KCl");
		if(temp != null) {
			this.I_34 = Double.parseDouble(temp);
			this.medium.K.setConcentration(this.medium.K.getConcentration() + this.I_34);
			this.medium.A.setConcentration(this.medium.A.getConcentration() + this.I_34);
			usedoptions.add("Add or remove KCl");
		}
		
		temp = options.get("Add or remove sucrose");
		if(temp != null) {
			this.I_46 = Double.parseDouble(temp);
			this.medium.Sucrose.setConcentration(this.medium.Sucrose.getConcentration() + this.I_46);
			usedoptions.add("Add or remove sucrose");
		}
		
		temp = options.get("Mg concentration");
		if(temp != null) {
			Double mgtold = this.medium.Mgt.getConcentration();
			this.medium.Mgt.setConcentration(Double.parseDouble(temp));
			usedoptions.add("Mg concentration");
			if(this.medium.Mgt.getConcentration() != 0) {
				this.medium.A.setConcentration(this.medium.A.getConcentration() + 2.0*(this.medium.Mgt.getConcentration() - mgtold));
			}
		}
		
		temp = options.get("Ca concentration");
		if(temp != null) {
			Double catold = this.medium.Cat.getConcentration();
			this.medium.Cat.setConcentration(Double.parseDouble(temp));
			usedoptions.add("Ca concentration");
			if(this.medium.Cat.getConcentration() != 0) {
				this.medium.A.setConcentration(this.medium.A.getConcentration() + 2.0*(this.medium.Cat.getConcentration() - catold));
			}
		}
		
		temp = options.get("Add EGTA(1) or EDTA(2) (0 for no chelator)");
		if(temp!=null) {
			usedoptions.add("Add EGTA(1) or EDTA(2) (0 for no chelator)");
			this.ligchoice = Double.parseDouble(temp);
		}
		
		temp = options.get("Chelator concentration"); // chelator concentration
		if(temp != null) {
			usedoptions.add("Chelator concentration");
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
	// THIS METHOD IS OBSOLETE...
	private void set_screen_time_factor_options(HashMap<String,String> options, ArrayList<String> usedoptions) {
		String temp = options.get("Time");
		if(temp != null) {
			this.duration_experiment += Double.parseDouble(temp);
			usedoptions.add("Time");
		}
		
		temp = options.get("Output Accuracy");
		if(temp != null) {
			this.dp = Integer.parseInt(temp);
			usedoptions.add("Output Accuracy");
		}
		
		this.I_43 = this.integration_interval_factor;
		temp = options.get("Frequency Factor");
		if(temp != null) {
			this.integration_interval_factor = Double.parseDouble(temp);
			usedoptions.add("Frequency Factor");
		}
		
		temp = options.get("Regular dt");
		if(temp != null) {
			if(temp.equals("no")) {
				this.compute_delta_time = false;
				usedoptions.add("Regular dt");
			}else if(temp.equals("yes")) {
				this.compute_delta_time = true;
				usedoptions.add("Regular dt");
			}else {
				System.out.println("Invalud value for field compute_delta_time");
			}
		}
		temp = options.get("Delta Time");
		if(temp != null) {
			this.delta_time = Double.parseDouble(temp);
			usedoptions.add("Delta Time");
		}
		temp = options.get("Cycles per print");
		if(temp != null) {
			this.cycles_per_print = Integer.parseInt(temp);
			usedoptions.add("Cycles per print");
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
				(this.cell.Mgf.getConcentration()+this.cell.Caf.getConcentration())*this.Vw
				+ this.benz2);
		
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
				this.cbenz2);
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
		String na_efflux_fwd = rsoptions.get("Na/K pump Na efflux");
		if(na_efflux_fwd != null) {
			this.getNapump().setFluxFwd(Double.parseDouble(na_efflux_fwd));
			usedoptions.add("Na/K pump Na efflux");
		}
		
		
		String na_efflux_rev = rsoptions.get("na-efflux-rev");
		if(na_efflux_rev != null) {
			this.getNapump().setFluxRev(Double.parseDouble(na_efflux_rev));
			usedoptions.add("na-efflux-rev");
		}
		
		
		// Other code to be added here...
		// New ones added for reduced RS in March 18
		String temp = rsoptions.get("[Na]i");
		if(temp != null) {
			this.cell.Na.setConcentration(Double.parseDouble(temp));
			usedoptions.add("[Na]i");
		}
		temp = rsoptions.get("[K]i");
		if(temp != null) {
			this.cell.K.setConcentration(Double.parseDouble(temp));
			usedoptions.add("[K]i");
		}
		
		temp = rsoptions.get("Q10 passive");
		if(temp != null) {
			this.Q10Passive = Double.parseDouble(temp);
			usedoptions.add("Q10 passive");
		}
		/*
		 *  The following sets the active Q10 in the sodium pump
		 *  which is also used by the Ca-Mg transporter 
		 */
		temp = rsoptions.get("Q10 active");
		if(temp != null) {
			this.napump.setQ10Active(Double.parseDouble(temp));
			usedoptions.add("Q10 active");
		}
//		temp = rsoptions.get("mchc");
//		if(temp != null) {
//			this.mchc = Double.parseDouble(temp);
//			usedoptions.add("mchc");
//		}
//		temp = rsoptions.get("vw");
//		if(temp != null) {
//			this.Vw = Double.parseDouble(temp);
//			usedoptions.add("vw");
//		}

	}
	public void cellwaterscreenRS(HashMap<String,String> rsoptions,ArrayList<String> usedoptions) {
		String hb_content_str = rsoptions.get("MCHC");
		if(hb_content_str != null) {
			usedoptions.add("MCHC");
			this.hb_content = Double.parseDouble(hb_content_str);
		}
		this.cell.Hb.setAmount(this.hb_content * 10.0/64.5);
		this.I_79 = 1.0 - this.hb_content/136.0;
		this.vlysis = 1.45;
		if(this.hb_content == 34.0) {
			String temp = rsoptions.get("Cell water content");
			if(temp != null) {
				this.I_79 = Double.parseDouble(temp);
				usedoptions.add("Cell water content");
			}
			temp = rsoptions.get("lytic-cell-water");
			if(temp != null) {
				this.vlysis = Double.parseDouble(temp);
				usedoptions.add("lytic-cell-water");
			}
			
		}
	}
	
	public void cellanionprotonscreenRS(HashMap<String,String> rsoptions,ArrayList<String> usedoptions) {
		String temp = rsoptions.get("[A]i");
		if(temp != null) {
			this.cell.A.setConcentration(Double.parseDouble(temp));
			usedoptions.add("[A]i");
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
		
		// New option added for reduced RS, March 18
//		temp = rsoptions.get("hb-choice");
		temp = rsoptions.get("Haemoglobin (A or S)");
		if(temp != null) {
			if(temp == "A") {
				this.A_1 = -1.0;
				this.pit0 = 7.2;
			}else if(temp == "S") {
				this.A_1 = -8.0;
				this.pit0 = 7.4;
			}
			usedoptions.add("Haemoglobin (A or S)");
		}
	}
	
	public void setmgdefaults() {
		this.cell.Mgt.setAmount(2.5);
		this.medium.Mgt.setConcentration(0.2);
		this.atp = 1.2;
		this.dpgp = 15.0;
		this.VV = (1.0 - this.A_11) + this.Vw;
		Double conc = this.newton_raphson(new Eqmg(), 0.02, 0.0001, 0.00001,100,0, false);
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

		temp = rsoptions.get("PMCA Fmax");
		if(temp != null) {
			this.capump.setDefaultFcapm(Double.parseDouble(temp));
			usedoptions.add("PMCA Fmax");
		} 
//		else {
//			this.capump.setFcapm(12.0);
//		}

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
			this.capump.setCah(2-capstoich);
			usedoptions.add("pump-electro");
		} else {
//			capstoich = 2; // Default sets cah to 0 which is 2 protons per Ca
			// This is now set in the constructor of the Calcium pump
		}
		
		
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
		
		temp = rsoptions.get("PCaG");
		if(temp != null) {
			this.passiveca.setFcalm(Double.parseDouble(temp));
			usedoptions.add("PCaG");
		} 
//		else {
//			this.passiveca.setFcalm(0.05);
//		}
		
		
		temp = rsoptions.get("PKGardosMax");
		if(temp != null) {
			this.goldman.setDefaultPkm(Double.parseDouble(temp));
			usedoptions.add("PKGardosMax");
		} 
//		else {
//			this.goldman.setPkm(30.0);
//		}
		
		
		temp = rsoptions.get("KCa(Gardos channel)");
		if(temp != null) {
			this.goldman.setPkcak(Double.parseDouble(temp));
			usedoptions.add("KCa(Gardos channel)");
		}
//		else {
//			this.goldman.setPkcak(1e-2);
//		}
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
//		System.out.println();
//		System.out.println();
//		System.out.println("Initial: " + this.cell.Caf.getConcentration());
		Double conc = this.newton_raphson(new Eqca(),this.cell.Caf.getConcentration(),0.000001, 0.000001,100,0, false);
//		Double conc = this.newton_raphson(new Eqca(),0.12735271872550802,0.000001, 0.000001,100,0, true);
//		Double conc2 = this.newton_raphson(new Eqca(),0.06236444130115539,0.000001, 0.000001,100,0, true);

		
//		System.out.println("DONE:" + conc + " " + conc2);
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
//			Double term1 = local_x6*(Math.pow(alpha,-1.0));
//			Double term2 = ((b1ca/VV)*local_x6/(b1cak+local_x6));
//			Double term3 = ((benz2/VV)*local_x6/(benz2k+local_x6));
//			System.out.println("1: " + term1 + " 2: " + term2 + " 3: " + term3 + " VV: " + VV);
//			System.out.println("" + alpha + " " + b1ca + " " + b1cak + " " + benz2 + " " + benz2k);
			cabb = local_x6*(Math.pow(alpha,-1.0))+((b1ca/VV)*local_x6/(b1cak+local_x6))+((benz2/VV)*local_x6/(benz2k+local_x6));
//			System.out.println("CABB: " + cabb + " Cat: " + cell.Cat.getAmount());
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
				System.out.println("It: " + no_its + ", X_3: " + X_3 + ", S: " + S + ", Y/S: " + Y_1/S);
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
	
	public ResultHash makeResultHash() {
		ResultHash new_result = new ResultHash(this.sampling_time*60.0); // convert to minutes for publishing
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
		new_result.setItem("CH/nM", 1e9*Math.pow(10.0,-this.cell.getpH()));
		new_result.setItem("CCa2+",this.cell.Caf.getConcentration());
		new_result.setItem("CMg2+",this.cell.Mgf.getConcentration());
		new_result.setItem("CHb",this.cell.Hb.getConcentration());
		new_result.setItem("CX",this.cell.X.getConcentration());
		new_result.setItem("nX", this.A_10);
		new_result.setItem("COs",this.cell.Os.getConcentration());
		new_result.setItem("MOs",this.medium.Os.getConcentration());
		new_result.setItem("rA",this.rA);
		new_result.setItem("rH",this.rH);
		new_result.setItem("fHb",this.fHb);
		new_result.setItem("nHb",this.nHb);
		new_result.setItem("MNa",this.medium.Na.getConcentration());
		new_result.setItem("MK",this.medium.K.getConcentration());
		new_result.setItem("MA",this.medium.A.getConcentration());
		new_result.setItem("MH/nM", 1e9*Math.pow(10.0, -this.medium.getpH()));
		new_result.setItem("MB", this.buffer_conc);
		new_result.setItem("MCat", this.medium.Cat.getConcentration());
		new_result.setItem("MCaf", this.medium.Caf.getConcentration());
		new_result.setItem("MMgt", this.medium.Mgt.getConcentration());
		new_result.setItem("MMgf", this.medium.Mgf.getConcentration());
		new_result.setItem("FNaP",this.getNapump().getFlux_net());
		new_result.setItem("FCaP",this.capump.getFlux_Ca());
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
//		new_result.setItem("FKGpiezo", this.goldman.computePKGPiezo(I_18));
		new_result.setItem("FKGgardos", this.goldman.computeFKGardos(I_18));
		new_result.setItem("FzKG", this.piezoGoldman.getFlux_K());
		new_result.setItem("FzNaG", this.piezoGoldman.getFlux_Na());
		new_result.setItem("FzAG", this.piezoGoldman.getFlux_A());
		new_result.setItem("FzCaG", this.piezoPassiveca.getFlux());
		
		
		new_result.setItem("FACo", this.cotransport.getFlux_A());
		new_result.setItem("FKCo", this.cotransport.getFlux_K());
		new_result.setItem("FNaCo", this.cotransport.getFlux_Na());
		
		
		new_result.setItem("fHb*CHb", this.fHb*this.cell.Hb.getConcentration());
		new_result.setItem("Msucr", this.medium.Sucrose.getConcentration());
		new_result.setItem("Mgluc-", this.medium.Gluconate.getConcentration());
		new_result.setItem("Mgluc+", this.medium.Glucamine.getConcentration());
		
		
		new_result.setItem("TransitHct", this.finalPiezoHct);
		
		
		new_result.setItem("FzKGTransit", this.finalPiezoFK);
		new_result.setItem("FzNaGTransit", this.finalPiezoFNa);
		new_result.setItem("FzAGTransit", this.finalPiezoFA);
		new_result.setItem("FzCaGTransit", this.finalPiezoFCa);
		
		double enFluxTest = this.total_flux_Na
				+ this.total_flux_K
				+ this.total_flux_H
				- this.total_flux_A
				+ 2.0*(this.total_flux_Ca+this.a23.getFlux_Mg());
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
	public void writeCsv(String name) {
		writeCsv(name,this.resultList);
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
}
