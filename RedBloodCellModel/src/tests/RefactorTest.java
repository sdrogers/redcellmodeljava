package tests;

import java.util.ArrayList;

import modelcomponents.*;
import utilities.*;
public class RefactorTest {
	public static void main(String[] args) {
		
		ExperimentalSettings experimentalSettings = ExampleProtocols.getPKG();
		double[] timeDiffs = new double[3];
		timeDiffs[0] = run(experimentalSettings);
		
		experimentalSettings = ExampleProtocols.getA();
		timeDiffs[1] = run(experimentalSettings);
		
		experimentalSettings = ExampleProtocols.getB();
		timeDiffs[2] = run(experimentalSettings);
		
		System.out.println();
		System.out.println();
		System.out.println();
		for(int i=0;i<timeDiffs.length;i++) {
			System.out.println(timeDiffs[i]);
		}
		
	}
	public static double run(ExperimentalSettings experimentalSettings) {
		RBC_model new_model = new RBC_model();
		RBC_model_old old_model = new RBC_model_old();

		ArrayList<String> usedoptionsRS = new ArrayList<String>();
		ArrayList<String> usedoptions = new ArrayList<String>();
		//
		new_model.setup(experimentalSettings.getRSOptions(), usedoptionsRS);
		for(DSSettings d: experimentalSettings.getDSStages()) {
			new_model.setupDS(d.getOptions(), usedoptions);
			new_model.runall(null);
		}
		
		
		old_model.setup(experimentalSettings.getRSOptions(), usedoptionsRS);
		for(DSSettings d: experimentalSettings.getDSStages()) {
			old_model.setupDS(d.getOptions(), usedoptions);
			old_model.runall(null);
		}
		
		ArrayList<ResultHash> old_res = old_model.getResults();
		ArrayList<ResultHash> new_res = new_model.getResults();
		
		double finalTimeDiff = -99.0;
		
		if(old_res.size() != new_res.size()) {
			System.out.println("Different lengths!!");
			
		}else {
			for(int i=0;i<old_res.size();i++) {
				Double oldTime = old_res.get(i).getTime();
				Double newTime = new_res.get(i).getTime();
				System.out.println(oldTime + "  " + newTime + "  " + (oldTime-newTime));
				finalTimeDiff = oldTime - newTime;
			}
		}
		return finalTimeDiff;
		
		
		
		
		
	}
}
