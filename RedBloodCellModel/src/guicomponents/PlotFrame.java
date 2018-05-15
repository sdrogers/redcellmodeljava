package guicomponents;


import java.util.ArrayList;

import javax.swing.JFrame;

import org.math.plot.*;

import utilities.ResultHash;


public class PlotFrame extends JFrame {
	private final ArrayList<ResultHash> results;
	private final Plot2DPanel plot;
	public PlotFrame(ArrayList<ResultHash> results,String[] series) {
		
		this.results = results;
		plot = new Plot2DPanel("SOUTH");
		this.setSize(1500,1000);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		int n_results = this.results.size();
		double[] x = new double[n_results];
		double[] y = new double[n_results];
		String combined = "";
		for(String s: series) {
			int pos = 0;
			for(ResultHash r: results) {
				x[pos] = r.getTime();
				y[pos] = r.getItem(s);
				pos++;
			}
			plot.addLinePlot(s, x,y);
			combined += s;
		}
		
		plot.getAxis(0).setLabelText("time");
        plot.getAxis(1).setLabelText(combined);
		
		this.setContentPane(plot);
		this.setVisible(true);
	}
	
}
