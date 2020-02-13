package utilities;

public class HelpText {
	public static final String rsHelp = "<html><body style='width: 500px'>"
			+ "<h1>The Reference State (RS):</h1>"
			+ "<p>The RS is a steady-state representing the initial physiological condition of a RBC at the start of experiments. Changing default values automatically recalculates a new initial steady-state condition for the redefined cell. For instance, to approximate a young RBC one could replace the corresponding defaults by CNa 5, CK 145, Vw 0.82 and FNaP of -3.2.</p><hr />" + 
			"<p><b>HbA or HbS:</b> HbA and HbS differ in the values of their isoelectric point and net charge per mol, a.  For HbA (default) pI(oC) = 7.2 and a = -10 Eq/(mol*∆(pH – pI) unit); for HbS, the corresponding values are 7.4 and -8.  The net charge on Hb at each pHi, nHb, is computed from nHb = a(pHi – pI), where a is the slope of the proton titration curve of Hb in Eq/mol, and pI is the isoelectric pH of Hb.</p><hr />" + 
			"<p><b>Na/K pump Na efflux:</b>	Changing the Na flux automatically resets the associated pump-mediated K influx and reverse Na/K fluxes following the stoichiometries and relative forward-reverse flux ratio encoded in the model.</p><hr />" + 
			"<p><b>CA:</b>	Initial cell Cl- + HCO3- concentration (mmol/Lcw). Ca and CCl are used indistinctly throughout.</p><hr />" + 
			"<p><b>MCHC:</b> Mean Cell Haemoglobin Concentration, a common haematological parameter in blood test assays, traditionally reported in gHb/dLoc; MCHC of <i>mean</i> model cell = 34 gHb/dL</p><hr />" + 
			"<p><b>PMCA Fmax:</b>	Maximal Ca2+ extrusion flux through an ATP and Ca2+-saturated plasma membrane calcium pump</p><hr />" + 
			"<p><b>PKGardosMax:</b>	electrodiffusional K+ permeability through Ca2+-saturated Gardos channels</p><hr />" + 
			"<p><b>KCaGardos channel:</b>	Half-maximal Ca2+ dissociation constant (K1/2) for Gardos channel activation</p><hr />" + 
			"<p><b>Vw:</b>	Water content associated with 340 g Hb; the volume occupied by 340 g Hb at a molar weight of 1.36 g/ml for the Hb tetramer is 0.25 L. The default 0.75 Lcw/Loc for Vw sets a value of 1 L/Loc for the initial volume of the default <i>mean</i> RBC.</p><hr />" + 
			"<p><b>Q10 active or passive:</b>	The Q10 factors determine the extent by which active and passive fluxes (F) are set to increase or decrease for each 10oC increase or decrease in temperature, T.</p><hr />" + 
			"</body></html>";
	public static final String dsHelp = "<html><body style='width: 500px'>" + 
			"<h1>The Dynamic State (DS)</h1>" + 
			"<p>Displays five tabs grouping lists of parameters and variables with default values for constructing one or successive stages in simulated experimental protocols</p>" + 
			"<hr />" + 
			"<h2>Time & Data Output Frequency:</h2>" + 
			"<p><b>Time:</b>Sets the duration of each DSn stage </p>" + 
			"<p><b>FrequencyFactor:</b> Sets the duration of each iteration interval (∆t at time = t) inversely proportional to the absolute value of the sum of all the net fluxes across the cell membrane at time t.  Allows data output frequencies to appear proportional to the rate of change in the system at constant “cyclesperprint” values. </p>" + 
			"<p><b>Cyclesperprint:</b> sets the number of computational cycles between data output points</p>" + 
			"<p><b>Accuracy:</b> sets the decimal precision on the output data  </p>" + 
			"<hr />" + 
			"<h2>Cell fraction and Medium Composition: </h2>" + 
			"<p>Medium concentrations of X are indicated by MX in mM units.  Isosmotic exchanges of X for Y are shown as X x Y.  Addition/removals allow changes in medium osmolarity. HEPES, Glucamine, gluconate, sucrose, Mg, EGTA and EDTA are treated as impermeant medium solutes.     </p>" + 
			"<p><b>MB:</b> Medium buffer concentration, HEPES by default</p>" + 
			"<p><b>EDGTA 0; G(1); D(2):</b>  Prompts for the addition of EGTA (G) or EDTA (D) to the cell suspension. </p>" + 
			"<p><b>MEDGTA:</b>  Prompts for the concentration of EGTA or EDTA, if added.  The default is 0, no addition.</p>    " + 
			"<hr />" + 
			"<h2>Temperature & Permeabilities:</h2>" + 
			"<p>Notation on the unit used for electrodiffusional ion permeabilities, 1/h or h 1: 1/h is a widely used permeability unit in the RBC literature. For permeability comparisons between membranes from different cell types the most widely used unit is cm/s.  For RBCs, both units are related through Pcm/s = P1/h*(V/A)/3600, where V and A correspond to the RBC volume and membrane area (in cm units) at the time the permeability measurement was taken.    " + 
			"PHG: PHG was modelled to enable simulations of the effects of protonophore additions.  The default value represents no protonophore present.  To simulate observed effects enter values around 2e10.   </p>  " + 
			"<p><b>PA23CaMg:</b> Ionophore A23187 mediates electroneutral X2+:2H+ exchanges {Pressman] with well defined highly non-linear kinetics in human RBCs. The default value represents absence of ionophore.  To simulate the effects of ionophore concentrations capable of generating a Ca2+ flux exceeding that of the PMCA Fmax at medium Ca2+ concentrations around 0.2 mM use values around 2e18. </p>       " + 
			"<p><b>Hb pI(0oC) oxy(7.2), deoxy(7.5):</b> Hb is assumed to be in a oxygen-saturated condition by default (oxy).  Deoxygenation of Hb (deoxy) changes its pI(0oC) from 7.2 to 7.5.  The model automatically adjusts the actual pI change for the temperature of the experiment.  The pI shifts during oxy-deoxy transitions cause changes in the protonization condition of Hb with secondary pHi changes which the model accurately predicts.  </p>" + 
			"<hr />" + 
			"<h2>Transport inhibition (%; defaults = 0):</h2>" + 
			"<p>The default Fmax value for each transporter, Fm, is modified according to Fm*(100-X)/100 where X is the % inhibition entered at the prompt.  Fm stays modified in successive DS stages unless modified again. Entries in successive DSs always apply to the original default (0%).  To return to the original uninhibited Fm enter “0”.</p>" + 
			"<hr />" + 
			"<h2>Piezo</h2>" + 
			"<p><b>Pz stage no or yes:</b> “yes”activates a pre-programmed three-stage sequence.  A 2 min control period is followed by a brief (~0.4 s by default) open state period of PIEZO1 channels meant to inform about the homeostatic RBC changes expected during a capillary transit. The post-open-state period of about three min is intended to illustrate the likely extent of reversibility between consecutive capillary transits. It is necessary to increase the accuracy of data output by orders of magnitude because of the minute magnitude of the simulated changes attributed to single transits. </p>   " + 
			"<p><b>PzTime:</b> Applies to the total duration of the three-stage routine </p>" + 
			"</body></html>";
}
