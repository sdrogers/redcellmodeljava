import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class LoadProtocol {
	public static HashMap<String,String> loadOptions(String filename) {
		HashMap<String,String> options = new HashMap<String,String>();
		BufferedReader reader = null;
		File file = new File(filename);
		try {
			reader = new BufferedReader(new FileReader(file));
			String text = null;
			while ((text = reader.readLine()) != null ) {
				String[] parts = text.split(" ");
				options.put(parts[0],parts[1]);
			}
		} catch (FileNotFoundException e) {
		    e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
		    try {
		        if (reader != null) {
		            reader.close();
		        }
		    } catch (IOException e) {
		    }
		}
		return options;
	}
	public static void printOptions(HashMap<String,String> options) {
		System.out.println("OPTIONS...");
		for(String key: options.keySet()) {
			System.out.println(key + " -> " + options.get(key));
		}
	}
}
