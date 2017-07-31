import java.util.HashMap;


public class TestHandle {
	public static void main(String[] args) {
		System.out.println("RedBlodModel Handler");
		System.out.println();
		System.out.println();
		
		RBC_model rbc_model = new RBC_model();
		
		HashMap<String,String> rsoptions = new HashMap<String,String>();
		
		
		rsoptions.put("pump-electro","1");
		rsoptions.put("hab", "0");
		
		// use get(key) to get the value back
		
		HashMap<String,String> options = new HashMap<String,String>();
		options.put("pgk", "30.0");
		options.put("pga", "50.0");
		options.put("time","30.0");
		
		rbc_model.setup(rsoptions);
		
		rbc_model.setupDS(options);
		
		rbc_model.runall();
		
		rbc_model.writeCsv("test.csv");
		
	}
}
