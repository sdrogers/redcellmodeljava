import java.util.HashMap;


public class TestHandle {
	public static void main(String[] args) {
		System.out.println("RedBlodModel Handler");
		
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
		
		
		
	}
}
