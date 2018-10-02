package modelcomponents;

import java.util.ArrayList;
import java.util.HashMap;

public class OptionsParsers {
	@SuppressWarnings("unused")
	public static void set_screen_time_factor_options(HashMap<String,String> options, ArrayList<String> usedoptions, RBC_model model) {
		String temp = options.get("time");
		if(temp != null) {
			model.setDuration_experiment(model.getDuration_experiment() + Double.parseDouble(temp));
			usedoptions.add("time");
		}
		
		temp = options.get("dp");
		if(temp != null) {
			model.setDp(Integer.parseInt(temp));
			usedoptions.add("dp");
		}
		
		model.setI_43(model.getIntegration_interval_factor());
		temp = options.get("integrationfactor");
		if(temp != null) {
			model.setIntegration_interval_factor(Double.parseDouble(temp));
			usedoptions.add("integrationfactor");
		}
		
		temp = options.get("compute_delta_time");
		if(temp != null) {
			if(temp.equals("no")) {
				model.setCompute_delta_time(false);
				usedoptions.add("compute_delta_time");
			}else if(temp.equals("yes")) {
				model.setCompute_delta_time(true);
				usedoptions.add("compute_delta_time");
			}else {
				System.out.println("Invalud value for field compute_delta_time");
			}
		}
		temp = options.get("delta_time");
		if(temp != null) {
			model.setDelta_time(Double.parseDouble(temp));
			usedoptions.add("delta_time");
		}
		temp = options.get("cyclesperprint");
		if(temp != null) {
			model.setCycles_per_print(Integer.parseInt(temp));
			usedoptions.add("cyclesperprint");
		}
		
	}
	public static void set_transport_changes_options(HashMap<String,String> options, ArrayList<String> usedoptions,RBC_model model) {
		String temp = options.get("na-pump-flux-change");
		if(temp != null) {
			Double inhibFac = (100.0-Double.parseDouble(temp))/100.0;
			model.getNapump().setP_1(model.getNapump().getDefaultP_1()*inhibFac);
			usedoptions.add("na-pump-flux-change");
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
		temp = options.get("js-stimulation-inhibition");
		if(temp != null) {
			Double jsfactor = Double.parseDouble(temp);
			jsfactor = (100.0 - jsfactor)/100.0;
			model.getJS().setPermeability(model.getJS().getDefaultPermeability() * jsfactor);
			usedoptions.add("js-stimulation-inhibition");
		}
		temp = options.get("ca-pump-vmax-change");
		if(temp != null) {
			Double fc = (100.0 - Double.parseDouble(temp))/100.0;
			model.getCapump().setFcapm(model.getCapump().getDefaultFcapm() * fc);
			usedoptions.add("ca-pump-vmax-change");
		}
		
//		temp = options.get("vmax-leak-change");
//		if(temp != null) {
//			model.passiveca.setFcalm(model.passiveca.getFcalm()*Double.parseDouble(temp));
//			usedoptions.add("vmax-leak-change");
//		}
		
		temp = options.get("percentage-inhibition");
		if(temp != null) {
			Double gc = (100.0 - Double.parseDouble(temp))/100.0;
			model.getGoldman().setPkm(model.getGoldman().getDefaultPkm() * gc);
			usedoptions.add("percentage-inhibition");
		}
	}
}
