package modelcomponents;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

// Stores the state of the Piezo
// and the values with which to change the permeabilities
// note that the actual changes are made by the model class
// Note also that time here is in minutes
public class Piezo implements Serializable {
//	private Double startTime = 2.0/60.0; // hours
	private Double startTime = 0.0;
	private Double duration = (1.0/120.0)/60.0; // hours
	private Double recovery = 1.0/60.0; // hours
	private Integer cycles = 7;
	private Double pkg = 10.0;
	private Double pnag = 8.0;
	private Double pcag = 100.0;
	private Double pag = 50.0;
	private Double pmca = 90.0;
	private Double piezoFraction = 0.0001;
	private Double piezoJS = 1.0; // I.e. multiplicative, so by default, don't change
	private boolean restoreMedium = false;
	private HashMap<String,String> mediumDefaults;
	
	
	public Piezo() {
		this.mediumDefaults = new HashMap<String,String>();
		this.mediumDefaults.put("Restored Medium HEPES-Na concentration","10.0");
		this.mediumDefaults.put("Restored Medium pH","7.4");
		this.mediumDefaults.put("Restored Medium Na","145.54168"); // 145.0
		this.mediumDefaults.put("Restored Medium K","5.00253"); // 5.0
		this.mediumDefaults.put("Restored Medium Mg","0.2");
		this.mediumDefaults.put("Restored Medium Ca","1.0"); 
	}
	public void printKeys() {
		for(String key: this.mediumDefaults.keySet()) {
			System.err.println(key);
		}
	}
	public void setMediumDefaultItem(String key, String value) {
		System.err.println("Setting " + key);
		this.mediumDefaults.put(key, value);
	}
	public String getMediumDefaultsItem(String key) {
		return this.mediumDefaults.get(key);
	}
	
	public void setRestoreMedium(boolean res) {
		this.restoreMedium = res;
	}
	public boolean getRestoreMedium() {
		return restoreMedium;
	}
	public Double getPiezoJS() {
		return piezoJS;
	}
	public void setPiezoJS(Double piezoJS) {
		this.piezoJS = piezoJS;
	}
	public Double getPkg() {
		return pkg;
	}
	public void setPkg(Double pkg) {
		this.pkg = pkg;
	}
	public Double getPnag() {
		return pnag;
	}
	public void setPnag(Double pnag) {
		this.pnag = pnag;
	}
	public Double getPcag() {
		return pcag;
	}
	public void setPcag(Double pcag) {
		this.pcag = pcag;
	}
	public Double getPag() {
		return pag;
	}
	public void setPag(Double pag) {
		this.pag = pag;
	}
	public Double getPmca() {
		return pmca;
	}
	public void setPmca(Double pmca) {
		this.pmca = pmca;
	}
	public Integer getCycles() {
		return cycles;
	}
	public void setCycles(Integer cycles) {
		this.cycles = cycles;
	}
	public Double getRecovery() {
		return recovery;
	}
	public void setRecovery(Double recovery) {
		this.recovery = recovery;
	}
	public Double getStartTime() {
		return startTime;
	}
	public Double getDuration() {
		return duration;
	}
	private Double oldIF;
	private Double iF = 1e-5;
	public Double getiF() {
		return iF;
	}
	public void setiF(Double iF) {
		this.iF = iF;
	}
	private Double oldPKG;
	private Double oldPNaG;
	private Double oldPCaG;
	private Double oldPAG;
	private Double oldPMCA;
	public Double getOldPMCA() {
		return oldPMCA;
	}
	public void setOldPMCA(Double oldPMCA) {
		this.oldPMCA = oldPMCA;
	}
	public Double getOldPAG() {
		return oldPAG;
	}
	public void setOldPAG(Double oldPAG) {
		this.oldPAG = oldPAG;
	}
	public Double getOldPCaG() {
		return oldPCaG;
	}
	public void setOldPCaG(Double oldPCaG) {
		this.oldPCaG = oldPCaG;
	}
	private Integer oldCycles;
	public void setStartTime(Double startTime) {
		this.startTime = startTime;
	}
	public void setDuration(Double duration) {
		this.duration = duration;
	}
	
	public void setOldIF(Double i) {
		this.oldIF = i;
	}
	public Double getOldIF() {
		return this.oldIF;
	}
	public void setOldPKG(Double i) {
		this.oldPKG = i;
	}
	public Double getOldPKG() {
		return this.oldPKG;
	}
	public void setOldPNaG(Double i) {
		this.oldPNaG = i;
	}
	public Double getOldPNaG() {
		return this.oldPNaG;
	}
	public void setOldCycles(Integer i) {
		this.oldCycles = i;
	}
	public Integer getOldCycles() {
		return this.oldCycles;
	}
	public Double getPiezoFraction() {
		return piezoFraction;
	}
	public void setPiezoFraction(Double piezoFraction) {
		this.piezoFraction = piezoFraction;
	}
 }
