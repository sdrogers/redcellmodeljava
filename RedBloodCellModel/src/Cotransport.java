
public class Cotransport {
	private Double permeability = 0.000000001;
	private Double flux_A = 0.0;
	private Double flux_Na = 0.0;
	private Double flux_K = 0.0;
	private Region cell;
	private Region medium;
	private Double zero_factor = 0.0;

	public Cotransport(Region cell, Region medium) {
		this.cell = cell;
		this.medium = medium;
	}
	
	
}
