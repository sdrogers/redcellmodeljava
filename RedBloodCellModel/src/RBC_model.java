import java.util.HashMap;
import java.util.ArrayList;

public class RBC_model {
	private Region cell;
	private Region medium;
	private JacobsStewart JS;
	private Cotransport cotransport;
	private NaPump napump;
	private CarrierMediated carriermediated;
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

	
	public RBC_model() {
		cell = new Region();
		medium = new Region();
		
		// Define the pumps
		JS = new JacobsStewart(cell,medium);
		cotransport = new Cotransport(cell,medium);
		napump = new NaPump(cell,medium);
		carriermediated = new CarrierMediated(cell,medium);
		
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
		
		for(String option: usedoptions) {
			System.out.println(option);
		}
	}
	
	public void naPumpScreenRS(HashMap<String,String> rsoptions,ArrayList<String> usedoptions) {
		Double na_efflux_fwd = Double.parseDouble(rsoptions.get("na-efflux-fwd"));
		if(na_efflux_fwd != null) {
			this.napump.setFluxFwd(na_efflux_fwd);
			usedoptions.add("na-efflux-fwd");
		}
		
		if(this.napump.getFluxFwd() == -2.61) {
			this.napmaxf = 1.0;
		}else {
			this.napmaxf = 0.0;
		}
	}
}
