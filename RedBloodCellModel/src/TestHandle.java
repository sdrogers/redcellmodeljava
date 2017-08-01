import java.util.ArrayList;
import java.util.HashMap;


public class TestHandle {
	public static void main(String[] args) {
		System.out.println("RedBlodModel Handler");
		System.out.println();
		System.out.println();
		
		RBC_model rbc_model = new RBC_model();
			
		HashMap<String,String> options = new HashMap<String,String>();
		
		String options_file = "./resources/protocols/A3.txt";
		String results_file = "./resources/traces/A3.txt";
		
		options = LoadProtocol.loadOptions(options_file);
		LoadProtocol.printOptions(options);
		
		ArrayList<String> usedoptions = new ArrayList<String>();
		
		rbc_model.setup(options,usedoptions);		
		rbc_model.setupDS(options,usedoptions);
		
		rbc_model.runall();
		
		rbc_model.writeCsv(results_file);
		
	}
}
