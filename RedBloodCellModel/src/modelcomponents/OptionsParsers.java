package modelcomponents;

import java.util.ArrayList;
import java.util.HashMap;

public class OptionsParsers {
	@SuppressWarnings("unused")
	public static void set_screen_time_factor_options(HashMap<String,String> options, ArrayList<String> usedoptions, RBC_model model) {
		String temp = options.get("Time");
		if(temp != null) {
			model.setDuration_experiment(model.getDuration_experiment() + Double.parseDouble(temp));
			usedoptions.add("Time");
		}
		
		temp = options.get("Output Accuracy");
		if(temp != null) {
			model.setDp(Integer.parseInt(temp));
			usedoptions.add("Output Accuracy");
		}
		
		model.setI_43(model.getIntegration_interval_factor());
		temp = options.get("Frequency Factor");
		if(temp != null) {
			model.setIntegration_interval_factor(Double.parseDouble(temp));
			usedoptions.add("Frequency Factor");
		}
		
		temp = options.get("Regular dt");
		if(temp != null) {
			if(temp.equals("no")) {
				model.setCompute_delta_time(false);
				usedoptions.add("Regular dt");
			}else if(temp.equals("yes")) {
				model.setCompute_delta_time(true);
				usedoptions.add("Regular dt");
			}else {
				System.out.println("Invalud value for field compute_delta_time");
			}
		}
		temp = options.get("dt");
		if(temp != null) {
			model.setDelta_time(Double.parseDouble(temp));
			usedoptions.add("dt");
		}
		temp = options.get("Cycles per print");
		if(temp != null) {
			model.setCycles_per_print(Integer.parseInt(temp));
			usedoptions.add("Cycles per print");
		}
	}
	public static void set_transport_changes_options(HashMap<String,String> options, ArrayList<String> usedoptions,RBC_model model) {
		String temp = options.get("Na/K pump");
		if(temp != null) {
			Double inhibFac = (100.0-Double.parseDouble(temp))/100.0;
			model.getNapump().setP_1(model.getNapump().getDefaultP_1()*inhibFac);
			usedoptions.add("Na/K pump");
		}
		
		temp = options.get("na-pump-reverse-flux-change");
		if(temp != null) {
			model.getNapump().setP_2(Double.parseDouble(temp));
			usedoptions.add("na-pump-reverse-flux-change");
		}
		temp = options.get("naa-change");
		if(temp != null) {
			model.getCarriermediated().setPermeability_Na(model.getCarriermediated().getDefaultPermability_Na()*Double.parseDouble(temp)/100.0);
			usedoptions.add("naa-change");
		}
		temp = options.get("ka-change");
		if(temp != null) {
			model.getCarriermediated().setPermeability_K(model.getCarriermediated().getDefaultPermeability_K() * Double.parseDouble(temp)/100.0);
			usedoptions.add("ka-change");
		}
		// Check this one with Arieh
		// Removed from GUI at the moment....
		temp = options.get("cotransport-activation");
		if(temp != null) {
			Double co_f = Double.parseDouble(temp);
			model.getCotransport().setPermeability(0.0002 * co_f / 100.0);
			usedoptions.add("cotransport-activation");
		}
		temp = options.get("JS cycle");
		if(temp != null) {
			Double jsfactor = Double.parseDouble(temp);
			jsfactor = (100.0 - jsfactor)/100.0;
			model.getJS().setPermeability(model.getJS().getDefaultPermeability() * jsfactor);
			usedoptions.add("JS cycle");
		}
		temp = options.get("PMCA");
		if(temp != null) {
			Double fc = (100.0 - Double.parseDouble(temp))/100.0;
			model.getCaPump().setFcapm(model.getCaPump().getDefaultFcapm() * fc);
			usedoptions.add("PMCA");
		}
		
//		temp = options.get("vmax-leak-change");
//		if(temp != null) {
//			model.passiveca.setFcalm(model.passiveca.getFcalm()*Double.parseDouble(temp));
//			usedoptions.add("vmax-leak-change");
//		}
		
		temp = options.get("Gardos channel");
		if(temp != null) {
			Double gc = (100.0 - Double.parseDouble(temp))/100.0;
			model.getGoldman().setPkm(model.getGoldman().getDefaultPkm() * gc);
			usedoptions.add("Gardos channel");
		}
	}
}
