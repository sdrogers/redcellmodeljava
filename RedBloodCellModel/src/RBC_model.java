
public class RBC_model {
	private Region cell;
	private Region medium;
	private JacobsStewart JS;
	private Cotransport cotransport;
	private NaPump napump;
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
	
	
	
	public RBC_model() {
		cell = new Region();
		medium = new Region();
		
		// Define the pumps
		JS = new JacobsStewart(cell,medium);
		cotransport = new Cotransport(cell,medium);
		napump = new NaPump(cell,medium);
		
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
	}
}
