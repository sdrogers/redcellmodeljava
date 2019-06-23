package piezolifespan;

import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JTextArea;

import modelcomponents.RBC_model;

public class CoupledDS {
	public static void main(String[] args) {
		/*
		 * Experiment with running piezo as a series of 
		 * pairs of DS instead of the piezo routine
		 * and the medium restoration
		 */
		HashMap<String,String> rSOptions = makeRSOptions();
		HashMap<String,String> initialDs = makeInitialDs();
		HashMap<String,String> dS1Options = makeDS1Options();
		HashMap<String,String> dS2Options = makeDS2Options();
		
		RBC_model r = new RBC_model();
		r.setup(rSOptions, new ArrayList<String>());
		r.setupDS(initialDs, new ArrayList<String>());
		r.setPublish(true);
		r.publish();
		r.setPublish(false);
		r.runall(new JTextArea());
		int cycles_per_output = 1;
		int cycle_count = 0;
		while(r.getSamplingTime() < 24.0) {
			r.setupDS(dS1Options, new ArrayList<String>());
			r.runall(new JTextArea());
			
			Double deltaNa = 145.0 - r.getMediumNaConcentration();
			Double deltaK = 5.0 - r.getMediumKConcentration();
			
			dS2Options.put("Add or remove NaCl", "" + deltaNa);
			dS2Options.put("Add or remove KCl", "" + deltaK);
			
			r.setupDS(dS2Options, new ArrayList<String>());
			
			// New exponential decay version
			Double timeInMinutes = 0.0;
			timeInMinutes = r.getSamplingTime()*60.0;
			Double FMaxCa = r.getCaPump().getDefaultFcapm();
			Double FMaxNa = r.getNapump().getP_1();
			Double FMaxNaRev = r.getNapump().getP_2();
			Double pmcaK = 7e-6;
			Double tNaP = 0.0;
			Double naK = 7e-6;
			r.getCaPump().setDefaultFcapm(FMaxCa*Math.exp(-pmcaK*timeInMinutes));
			if(timeInMinutes > tNaP) {
				r.getNapump().setP_1(FMaxNa*Math.exp(-naK*(timeInMinutes - tNaP)));
				r.getNapump().setP_2(FMaxNaRev*Math.exp(-naK*(timeInMinutes - tNaP)));
			}
			
			
			
			r.runall(new JTextArea());
			cycle_count ++;
			if(cycle_count == cycles_per_output) {
				r.setPublish(true);
				r.publish();
				r.setPublish(false);
				cycle_count = 0;
			}
		}
		r.writeCsv("/Users/simon/TempStuff/Arieh/PiezoBig/cds_day_with_decay.csv");
	}
	public static HashMap<String,String> makeRSOptions() {
		HashMap<String,String> RSOptions = new HashMap<String,String>();
		RSOptions.put("Na/K pump Na efflux","-3.2");
		RSOptions.put("[K]i","145.0");
		RSOptions.put("[Na]i","5.0");
		RSOptions.put("[A]i","95.0");
		RSOptions.put("Cell water content","0.85");
		RSOptions.put("PMCA Fmax","12.0");
		RSOptions.put("PKGardosMax","30.0");
		RSOptions.put("KCa(Gardos channel)","0.01");
		return RSOptions;
	}
	public static HashMap<String,String> makeDS1Options() {
		HashMap<String,String> dS1Options = new HashMap<String,String>();
		dS1Options.put("Time","0.0066666666");
		dS1Options.put("Cell volume fraction","0.9");
		dS1Options.put("PAG","50.0");
		dS1Options.put("PCaG","70.0");
		dS1Options.put("Frequency Factor","0.00001");
		dS1Options.put("Cycles per print","111");
		return dS1Options;
	}
	public static HashMap<String,String> makeDS2Options() {
		HashMap<String,String> dS2Options = new HashMap<String,String>();
		dS2Options.put("Cell volume fraction","0.00001");
		dS2Options.put("PAG","1.2");
		dS2Options.put("Medium pH","7.4");
		
		// compute time in mins of 1 minute minus 0.4 seconds
		Double time_mins = 1.0 - (0.4 / 60.0);
		
		dS2Options.put("Time",""+time_mins);
		dS2Options.put("Ca concentration","1.0");
		dS2Options.put("PCaG","0.05");
		dS2Options.put("HEPES-Na concentration","10.0");
		dS2Options.put("Mg concentration","0.2");
		

		return dS2Options;
	}
	public static HashMap<String,String> makeInitialDs() {
		HashMap<String,String> initial = new HashMap<String,String>();
		initial.put("Cell volume fraction","0.00001");
		initial.put("Time","2.0");
		initial.put("Output Accuracy","6");
		return initial;
	}

}

