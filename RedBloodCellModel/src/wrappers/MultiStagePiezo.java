package wrappers;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import modelcomponents.RBC_model;
import utilities.ResultHash;

public class MultiStagePiezo {
	/* 
	 * A class to experiment with the multi-stage PIEZO system
	 */
	public static void main(String[] args) throws Exception {
//		ArrayList<ResultHash> r = new ArrayList<ResultHash>();
		double max_time = Double.parseDouble(args[0]);
		String out_file = args[1];
		HashMap<String,String> RSOptions = new HashMap<String,String>();
		HashMap<String,String> DSOptions = new HashMap<String,String>();
		DSOptions.put("Time", "1.0");
		DSOptions.put("Incorporate PIEZO stage","yes");
		DSOptions.put("piezo_start","0.0");
		DSOptions.put("Output Accuracy","6");
		Double stage_time = 1.0; // minutes
		Double open_time = 1.0/120.0; // 0.5 seconds
		Double recovery_time = stage_time - open_time - 1e-6;
		DSOptions.put("piezo_recovery",""+recovery_time);
		RBC_model rbc = new RBC_model();
		rbc.setVerbose(false);
		rbc.setup(RSOptions, new ArrayList<String>());
		Double time = rbc.getSamplingTime();
//		for(int i = 0;i<5;i++) {
		while(time < max_time) {
			rbc.setupDS(DSOptions, new ArrayList<String>());
			rbc.setPublish(false);
			rbc.runall(null);
			time = rbc.getSamplingTime();
//			if(time % 1.0 == 0.0) {
//				System.out.println("Time: " + time);
				rbc.setPublish(true);
				rbc.publish();
//				r.add(rbc.getLastResult());
				rbc.setPublish(false);
//			}
			
//			rbc.writeCsv("/Users/simon/TempStuff/piezo_test.csv");
//			rbc.clearResults();
		}
		
//		rbc.setResults(r);
		rbc.writeCsv(out_file);
	}
	private static final long MEGABYTE = 1024L * 1024L;
	public static long bytesToMegabytes(long bytes) {
	    return bytes / MEGABYTE;
	  }

}
