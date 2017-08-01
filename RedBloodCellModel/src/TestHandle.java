import java.util.ArrayList;
import java.util.HashMap;


public class TestHandle {
	public static void main(String[] args) {
		System.out.println("RedBlodModel Handler");
		System.out.println();
		System.out.println();
		
		RBC_model rbc_model = new RBC_model();
		
//		HashMap<String,String> rsoptions = new HashMap<String,String>();
		
		HashMap<String,String> options = new HashMap<String,String>();

		options.put("pump-electro","1");
		options.put("hab", "0");
		
		// use get(key) to get the value back
		
		//		options.put("pgk", "30.0");
//		options.put("pga", "50.0");
//		options.put("time","30.0");
		options.put("cyclesperprint", "777");
//		options.put("time", "30");
		
		options.put("time","120");
		options.put("fraction", "0.1");
		options.put("caot", "0.2");
		options.put("pmg", "2e18");
		
		ArrayList usedoptions = new ArrayList<String>();
		
		rbc_model.setup(options,usedoptions);
		
		rbc_model.setupDS(options,usedoptions);
		
		rbc_model.runall();
		
		rbc_model.writeCsv("test.csv");
		
	}
}
