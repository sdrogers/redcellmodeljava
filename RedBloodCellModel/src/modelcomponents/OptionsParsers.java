package modelcomponents;

import java.util.ArrayList;
import java.util.HashMap;

import modelcomponents.RBC_model.Eqmg;

public class OptionsParsers {
	@SuppressWarnings("unused")
	public static void set_screen_time_factor_options(HashMap<String,String> options, ArrayList<String> usedoptions, RBC_model model) {
		String temp = options.get("Time");
		if(temp != null) {
			Double t = 0.0;
			if (temp.startsWith("random")) {
				// It's random, extract the values
				t = TimeGenerator.getTime(temp);
				System.out.println("DS time randomly sampled as " + t);
			} else {
				t = Double.parseDouble(temp);
			}
			model.setDuration_experiment(model.getDuration_experiment() + t);
			usedoptions.add("Time");
		}
		
		temp = options.get("Accuracy");
		if(temp != null) {
			model.setDp(Integer.parseInt(temp));
			usedoptions.add("Accuracy");
		}
		
		model.setI_43(model.getIntegration_interval_factor());
		temp = options.get("FrequencyFactor");
		if(temp != null) {
			model.setIntegration_interval_factor(Double.parseDouble(temp));
			usedoptions.add("FrequencyFactor");
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
		temp = options.get("Cyclesperprint(epochs)");
		if(temp != null) {
			model.setCycles_per_print(Integer.parseInt(temp));
			usedoptions.add("Cyclesperprint(epochs)");
		}
	}
	public static void set_transport_changes_options(HashMap<String,String> options, ArrayList<String> usedoptions,RBC_model model) {
		String temp = options.get("% inhibition of Na/K pump FNamax");
		if(temp != null) {
			Double inhibFac = (100.0-Double.parseDouble(temp))/100.0;
			model.getNapump().setP_1(model.getNapump().getDefaultP_1()*inhibFac);
			usedoptions.add("% inhibition of Na/K pump FNamax");
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
		// Not visible in the GUI at the moment....
		temp = options.get("cotransport-activation");
		if(temp != null) {
			Double co_f = Double.parseDouble(temp);
			model.getCotransport().setPermeability(0.0002 * co_f / 100.0);
			usedoptions.add("cotransport-activation");
		}
		temp = options.get("% inhibition/stimulation(-) of JS kAE1");
		if(temp != null) {
			Double jsfactor = Double.parseDouble(temp);
			jsfactor = (100.0 - jsfactor)/100.0;
			model.getJS().setPermeability(model.getJS().getDefaultPermeability() * jsfactor);
			usedoptions.add("% inhibition/stimulation(-) of JS kAE1");
		}
		temp = options.get("% inhibition/stimulation(-) of FCaPmax");
		if(temp != null) {
			Double fc = (100.0 - Double.parseDouble(temp))/100.0;
			model.getCaPump().setFcapm(model.getCaPump().getDefaultFcapm() * fc);
			usedoptions.add("% inhibition/stimulation(-) of FCaPmax");
		}
		
		temp = options.get("% inhibition of PKGardosMax");
		if(temp != null) {
			Double gc = (100.0 - Double.parseDouble(temp))/100.0;
			model.getGoldman().setPkm(model.getGoldman().getDefaultPkm() * gc);
			usedoptions.add("% inhibition of PKGardosMax");
		}
	}
	public static void naPumpScreenRS(HashMap<String,String> rsoptions,ArrayList<String> usedoptions, RBC_model model) {
		String na_efflux_fwd = rsoptions.get("Na/K pump Na efflux");
		if(na_efflux_fwd != null) {
			model.getNapump().setFluxFwd(Double.parseDouble(na_efflux_fwd));
			usedoptions.add("Na/K pump Na efflux");
		}
		
		
		String na_efflux_rev = rsoptions.get("na-efflux-rev");
		if(na_efflux_rev != null) {
			model.getNapump().setFluxRev(Double.parseDouble(na_efflux_rev));
			usedoptions.add("na-efflux-rev");
		}
		
		
		// Other code to be added here...
		// New ones added for reduced RS in March 18
		String temp = rsoptions.get("CNa");
		if(temp != null) {
			model.cell.Na.setConcentration(Double.parseDouble(temp));
			usedoptions.add("CNa");
		}
		temp = rsoptions.get("CK");
		if(temp != null) {
			model.cell.K.setConcentration(Double.parseDouble(temp));
			usedoptions.add("CK");
		}
		
		temp = rsoptions.get("Q10 passive");
		if(temp != null) {
			model.Q10Passive = Double.parseDouble(temp);
			usedoptions.add("Q10 passive");
		}
		/*
		 *  The following sets the active Q10 in the sodium pump
		 *  which is also used by the Ca-Mg transporter 
		 */
		temp = rsoptions.get("Q10 active");
		if(temp != null) {
			model.napump.setQ10Active(Double.parseDouble(temp));
			usedoptions.add("Q10 active");
		}
	}
	
	public static void cellwaterscreenRS(HashMap<String,String> rsoptions,ArrayList<String> usedoptions, RBC_model model) {
		String hb_content_str = rsoptions.get("MCHC");
		if(hb_content_str != null) {
			usedoptions.add("MCHC");
			model.setHb_content(Double.parseDouble(hb_content_str));
		}
		model.cell.Hb.setAmount(model.getHb_content() * 10.0/64.5);
		model.setI_79(1.0 - model.getHb_content()/136.0);
		model.setVlysis(1.45);
		if(model.getHb_content() == 34.0) {
			String temp = rsoptions.get("Vw");
			if(temp != null) {
				model.setI_79(Double.parseDouble(temp));
				usedoptions.add("Vw");
			}
			temp = rsoptions.get("lytic-cell-water");
			if(temp != null) {
				model.setVlysis(Double.parseDouble(temp));
				usedoptions.add("lytic-cell-water");
			}
		}
	}
	public static void cellanionprotonscreenRS(HashMap<String,String> rsoptions,ArrayList<String> usedoptions, RBC_model model) {
		String temp = rsoptions.get("CA");
		if(temp != null) {
			model.cell.A.setConcentration(Double.parseDouble(temp));
			usedoptions.add("CA");
		}
	}
	public static void chargeandpiscreenRS(HashMap<String,String> rsoptions,ArrayList<String> usedoptions, RBC_model model) {
		String temp = rsoptions.get("a");
		if(temp != null) {
			model.setA_1(Double.parseDouble(temp));
			usedoptions.add("a");
		}
		temp = rsoptions.get("pi");
		if(temp != null) {
			model.setPit0(Double.parseDouble(temp));
			usedoptions.add("pi");
		}
		
		// New option added for reduced RS, March 18
		temp = rsoptions.get("Hb A or S");
		if(temp != null) {
			if(temp == "A") {
				model.setA_1(-1.0);
				model.setPit0(7.2);
			}else if(temp == "S") {
				model.setA_1(-8.0);
				model.setPit0(7.4);
			}
			usedoptions.add("Hb A or S");
		}
	}
	public static void mgbufferscreenRS(HashMap<String,String> rsoptions, ArrayList<String> usedoptions,RBC_model model) {
		String temp = rsoptions.get("mgot-medium");
		if(temp != null) {
			model.medium.Mgt.setConcentration(Double.parseDouble(temp));
			usedoptions.add("mgot-medium");
		} else {
			model.medium.Mgt.setConcentration(0.2);
		}
		
		
		temp = rsoptions.get("mgit");
		if(temp != null) {
			model.cell.Mgt.setAmount(Double.parseDouble(temp));
			usedoptions.add("mgit");
		} else {
			model.cell.Mgt.setAmount(2.5);
		}
		
		
		temp = rsoptions.get("hab");
		if(temp != null) {
			model.setMgb0(Double.parseDouble(temp));
			usedoptions.add("hab");
		} else {
			model.setMgb0(0.05);
		}
		
		
		temp = rsoptions.get("atpp");
		if(temp != null) {
			model.setAtp(Double.parseDouble(temp));
			usedoptions.add("atpp");
		} else {
			model.setAtp(1.2);
		}
		
		temp = rsoptions.get("23dpg");
		if(temp != null) {
			model.setDpgp(Double.parseDouble(temp));
			usedoptions.add("23dpg");
		} else {
			model.setDpgp(15.0);
		}
		Double conc = model.newton_raphson(model.new Eqmg(), 0.02, 0.0001, 0.00001,100,0, false);
		model.cell.Mgf.setConcentration(conc);
		
	}
	
	public static void cabufferscreenRS(HashMap<String,String> rsoptions, ArrayList<String> usedoptions, RBC_model model) {
		String temp = rsoptions.get("cato-medium");
		if(temp != null) {
			model.medium.Cat.setConcentration(Double.parseDouble(temp));
			usedoptions.add("cato-medium");
		} else {
			model.medium.Cat.setConcentration(1.0);
		}
		
		temp = rsoptions.get("add-ca-buffer");
		if(temp != null) {
			model.setB1ca(Double.parseDouble(temp));
			usedoptions.add("add-ca-buffer");
		} else {
			model.setB1ca(0.026);
		}
		
		temp = rsoptions.get("kd-of-ca-buffer");
		if(temp != null) {
			model.setB1cak(Double.parseDouble(temp));
			usedoptions.add("kd-of-ca-buffer");
		} else {
			model.setB1cak(0.014);
		}

		temp = rsoptions.get("alpha");
		if(temp != null) {
			model.setAlpha(Double.parseDouble(temp));
			usedoptions.add("alpha");
		} else {
			model.setAlpha(0.3);
		}

		temp = rsoptions.get("benz2loaded");
		if(temp != null) {
			model.setBenz2(Double.parseDouble(temp));
			usedoptions.add("benz2loaded");
		} else {
			model.setBenz2(0.0);
		}
		model.setCbenz2(model.getBenz2()/model.getVw());

		temp = rsoptions.get("PMCA FCaPmax");
		if(temp != null) {
			model.capump.setDefaultFcapm(Double.parseDouble(temp));
			usedoptions.add("PMCA FCaPmax");
		} 

		temp = rsoptions.get("k1/2");
		if(temp != null) {
			model.capump.setCapk(Double.parseDouble(temp));
			usedoptions.add("k1/2");
		} else {
			model.capump.setCapk(2e-4);
		}
		
		temp = rsoptions.get("hills");
		if(temp != null) {
			model.capump.setH1(Double.parseDouble(temp));
			usedoptions.add("hills");
		} else {
			model.capump.setH1(4.0);
		}
		
		temp = rsoptions.get("pump-electro");
		Integer capstoich;
		if(temp != null) {
			capstoich = Integer.parseInt(temp);
			model.capump.setCah(2-capstoich);
			usedoptions.add("pump-electro");
		} else {
//			capstoich = 2; // Default sets cah to 0 which is 2 protons per Ca
			// This is now set in the constructor of the Calcium pump
		}
		
		
		temp = rsoptions.get("h+ki");
		if(temp != null) {
			model.capump.setHik(Double.parseDouble(temp));
			usedoptions.add("h+ki");
		} else {
			model.capump.setHik(4e-7);
		}
		
		temp = rsoptions.get("Mg2+K1/2");
		if(temp != null) {
			model.capump.setCapmgk(Double.parseDouble(temp));
			usedoptions.add("Mg2+K1/2");
		} else {
			model.capump.setCapmgk(0.1);
		}
		
		temp = rsoptions.get("PCaG");
		if(temp != null) {
			model.passiveca.setFcalm(Double.parseDouble(temp));
			usedoptions.add("PCaG");
		} 
//		else {
//			this.passiveca.setFcalm(0.05);
//		}
		
		
		temp = rsoptions.get("PKGardosMax");
		if(temp != null) {
			model.goldman.setDefaultPkm(Double.parseDouble(temp));
			usedoptions.add("PKGardosMax");
		} 
//		else {
//			this.goldman.setPkm(30.0);
//		}
		
		
		temp = rsoptions.get("KCa Gardos channel");
		if(temp != null) {
			model.goldman.setPkcak(Double.parseDouble(temp));
			usedoptions.add("KCa Gardos channel");
		}
//		else {
//			this.goldman.setPkcak(1e-2);
//		}
		if(model.getBenz2() != 0) {
			model.cell.Caf.setConcentration(1e-8);
			model.canr();
		}
		
		
	}
	
	
}
