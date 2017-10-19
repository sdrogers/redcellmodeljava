import java.util.ArrayList;
import java.util.HashMap;

public class MultiVMaxRun {
	// Run multiple runs with different vmax-cap
	private HashMap<String,String> options;
	private RBC_model rbc;
	int[] vmc_vals = {5, 10, 20, 30, 40, 50, 60};
	
	public MultiVMaxRun() {
		options = new HashMap<String,String>();
		options = LoadProtocol.loadOptions("resources/protocols/multiV1.txt",options);
		for(int i=0;i<vmc_vals.length;i++) {
			
			ArrayList<String> usedoptions = new ArrayList<String>();
			options = new HashMap<String,String>();
			options = LoadProtocol.loadOptions("resources/protocols/multiV1.txt",options);
			options.put("vmax-cap", ""+vmc_vals[i]);
			
			rbc = new RBC_model();
			rbc.setup(options,usedoptions);
			rbc.setupDS(options, usedoptions);
			
			rbc.runall();
			
			options = LoadProtocol.loadOptions("resources/protocols/multiV2.txt", options);
			rbc.setupDS(options, usedoptions);
			
			rbc.runall();
			
			String outname = "resources/traces/multiV/multiV_"+ vmc_vals[i] +".csv";
			rbc.writeCsv(outname);
		}
		
		
	}
	
	public static void main(String[] args) {
		MultiVMaxRun mv = new MultiVMaxRun();
	}
	
}
