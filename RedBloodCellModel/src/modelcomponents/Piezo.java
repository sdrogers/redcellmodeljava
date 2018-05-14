package modelcomponents;
// Stores the state of the Piezo
// and the values with which to change the permeabilities
// note that the actual changes are made by the model class
// Note also that time here is in minutes
public class Piezo {
	private Double startTime = 2.0/60.0; // hours
	private Double duration = (1.0/120.0)/60.0; // hours
	private Double recovery = 1.0/60.0; // hours
	private Integer cycles = 7;
	private Double pkg = 10.0;
	private Double pnag = 8.0;
	private Double pcag = 100.0;
	private Double pag = 50.0;
	private Double pmca = 90.0;
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
 }
