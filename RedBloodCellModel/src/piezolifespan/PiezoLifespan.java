package piezolifespan;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

import modelcomponents.RBC_model;
import utilities.ResultHash;
import utilities.Updateable;

import org.math.plot.*;

import guicomponents.OptionsFrame;


public class PiezoLifespan extends JFrame implements ActionListener, Updateable{
	private JButton runButton,stopButton,saveButton,changePiezoButton;
	private JTextArea modelOutput;
	private ModelWorker worker;
	private PlotWorker plotter;
	private RBC_model rbc;
	private Plot2DPanel[] plot = new Plot2DPanel[4];
	private JTextField timeField,cycleField;
	private OptionsFrame piezoOptionsFrame;
	HashMap<String,String> DSOptions;
	public PiezoLifespan() {
		this.setSize(1000, 1000);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		layoutFrame();
		this.setVisible(true);
		makeOptionsDefault();
		DSOptions.put("Incorporate PIEZO stage", "yes");
	}
	
	private void layoutFrame() {
		JPanel mainPanel = new JPanel(new BorderLayout());
		this.add(mainPanel);
		JPanel buttonPanel = new JPanel(new GridLayout(0,2));
		mainPanel.add(buttonPanel,BorderLayout.SOUTH);
		
		runButton = new JButton("Run");
		stopButton = new JButton("Stop");
		saveButton = new JButton("Save csv");
		changePiezoButton = new JButton("Change PIEZO parameters");
		
		
		buttonPanel.add(new JLabel("Max model time (hours): "));
		timeField = new JTextField("1.0");
		buttonPanel.add(timeField);
		buttonPanel.add(new JLabel("Number of PIEZO cycles per output: "));
		cycleField = new JTextField("1440");
		buttonPanel.add(cycleField);
		
		
		buttonPanel.add(runButton);
		buttonPanel.add(stopButton);
		buttonPanel.add(saveButton);
		buttonPanel.add(changePiezoButton);
		
		runButton.addActionListener(this);
		stopButton.addActionListener(this);
		saveButton.addActionListener(this);
		changePiezoButton.addActionListener(this);
		
		stopButton.setEnabled(false);
		saveButton.setEnabled(false);
		
		JPanel outputPanel = new JPanel(new GridLayout(2,0));
		modelOutput = new JTextArea(20,30);
		outputPanel.add(new JScrollPane(modelOutput));
		JPanel justPlots = new JPanel(new GridLayout(2,2));
		for(int i=0;i<4;i++) {
			plot[i] = new Plot2DPanel("SOUTH");
			justPlots.add(plot[i]);
		}
		outputPanel.add(justPlots);
		mainPanel.add(outputPanel,BorderLayout.CENTER);
		
		
	}
	private void makeOptionsDefault() {
		DSOptions = new HashMap<String,String>();
		DSOptions.put("Time", "1.0");
		DSOptions.put("Incorporate PIEZO stage","yes");
		DSOptions.put("piezo_start","0.0");
		DSOptions.put("Output Accuracy","6");
		Double stage_time = 1.0; // minutes
//		Double open_time = 1.0/120.0; // 0.5 seconds - TODO make this 0.4
		Double open_time = 0.4*(1.0/60.0); // 0.4 seconds
		Double recovery_time = stage_time - open_time - 1e-6;
		DSOptions.put("piezo_recovery",""+recovery_time);
		DSOptions.put("Open state",""+0.4);
		
		DSOptions.put("Piezo Frequency factor", "0.00001");
		DSOptions.put("Piezo Cycles per print","111");
		DSOptions.put("PzKG","0.3");
		DSOptions.put("PzNaG", "0.15");
		DSOptions.put("PzAG","10.0");
		DSOptions.put("PzCaG","1.0");
	}
	private class ModelWorker extends SwingWorker<Void,ResultHash> {
		public Void doInBackground() {
			rbc = new RBC_model();
			HashMap<String,String> RSOptions = new HashMap<String,String>();
//			
			
			rbc.setVerbose(false);
			rbc.setup(RSOptions, new ArrayList<String>());
			Double time = rbc.getSamplingTime();
			Double max_time = Double.parseDouble(timeField.getText());
			int cycles_per_output = Integer.parseInt(cycleField.getText());
			int cycle_counter = 0;
//			rbc.setPublish(true);
//			rbc.publish();
//			rbc.setPublish(false);
//			publish(rbc.getLastResult());
			while(time < max_time) {
				if(this.isCancelled()) {
					break;
				}
				rbc.setupDS(DSOptions, new ArrayList<String>());
				rbc.setPublish(false);
				rbc.runall(null);
				time = rbc.getSamplingTime();
//				if(time % 1.0 == 0.0) {
//					System.out.println("Time: " + time);
					
//				}
				cycle_counter++;
				if(cycle_counter == cycles_per_output) {
					// make some output
					rbc.setPublish(true);
					rbc.publish();
//					r.add(rbc.getLastResult());
					rbc.setPublish(false);
					publish(rbc.getLastResult());
					cycle_counter = 0;
				}
				
				
//				rbc.writeCsv("/Users/simon/TempStuff/piezo_test.csv");
//				rbc.clearResults();
			}
			rbc.setPublish(true);
			rbc.publish();
//			r.add(rbc.getLastResult());
			rbc.setPublish(false);
			publish(rbc.getLastResult());
//			rbc.setResults(r);
//			rbc.writeCsv(out_file);
			resetButtons();
			return null;
		}
		public void process(List<ResultHash> res) {
			ResultHash lastItem = res.get(res.size()-1);
			Double time = lastItem.getTime();
			String newLine = String.format("%4.2f\t%8.4f\t%8.2f\n",lastItem.getTime(),lastItem.getItem("V/V"),lastItem.getItem("Em"));
			modelOutput.append(newLine);
			make_plots();
		}
		
	}
	private void make_plots() {
		int n_results = rbc.getResults().size();
		String combined = "";
		String[][] series = {{"V/V",},{"CNa","CK","CA"},{"Em"},{"rA","rH"}};
		MultiPlotData mpd = new MultiPlotData();
		int plot_pos = 0;
		for(String[] s: series) {
			plot[plot_pos].removeAllPlots();
			double[] x = new double[n_results];
			double[][] y = new double[s.length][n_results];
			int pos = 0;
			for(ResultHash r: rbc.getResults()) {
				x[pos] = r.getTime();
				for(int i=0;i<s.length;i++) {
					y[i][pos] = r.getItem(s[i]);
				}
				pos++;
				
			}
			for(int i=0;i<y.length;i++) {
				plot[plot_pos].addLinePlot(s[i], x,y[i]);
			}
			plot_pos ++;
//			plot.addLinePlot(s, x,y);
//			combined += s + " ";
			mpd.addPlot(new PlotData(s,x,y));
		}

	}
	private class PlotWorker extends SwingWorker<Void,MultiPlotData> {
		public Void doInBackground() {
			while(true) {
				if(this.isCancelled()) {
					break;
				}
				try {
					Thread.sleep(2000);
					// Make an output array and publish it
					int n_results = rbc.getResults().size();
					String combined = "";
					String[][] series = {{"V/V",},{"CNa","CK","CA"},{"Em"},{"rA","rH"}};
					MultiPlotData mpd = new MultiPlotData();
					int plot_pos = 0;
					for(String[] s: series) {
						plot[plot_pos].removeAllPlots();
						double[] x = new double[n_results];
						double[][] y = new double[s.length][n_results];
						int pos = 0;
						for(ResultHash r: rbc.getResults()) {
							x[pos] = r.getTime();
							for(int i=0;i<s.length;i++) {
								y[i][pos] = r.getItem(s[i]);
							}
							pos++;
							
						}
						for(int i=0;i<y.length;i++) {
							plot[plot_pos].addLinePlot(s[i], x,y[i]);
						}
//						plot.addLinePlot(s, x,y);
//						combined += s + " ";
						mpd.addPlot(new PlotData(s,x,y));
					}
					publish(mpd);
				}catch(InterruptedException e) {
					// do nothing, it's all good
				}
			}
			return null;
		}
		public void process(List<MultiPlotData> s) {
			if(s.size() > 0) {
				MultiPlotData mpd = s.get(s.size()-1);
				for(int i=0;i<mpd.plots.size();i++) {
					plot[i].removeAllPlots();
					PlotData temp = mpd.plots.get(i);
					for(int j=0;j<temp.y.length;j++) {
						plot[i].addLinePlot(temp.s[j], temp.x,temp.y[j]);
					}
				}
			}			
		}
	}
	private class MultiPlotData {
		public ArrayList<PlotData> plots = new ArrayList<PlotData>();
		public void addPlot(PlotData p) {
			plots.add(p);
		}
	}
	private class PlotData {
		public String[] s;
		public double[] x;
		public double[][] y;
		public PlotData(String[] s,double[] x,double[][] y) {
			this.s = s;
			this.x = x;
			this.y = y;
		}
	}
	private void resetButtons() {
		runButton.setEnabled(true);
		stopButton.setEnabled(false);
		saveButton.setEnabled(true);
		if(plotter != null) {
			plotter.cancel(true);
		}
	}
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == runButton) {
			this.modelOutput.setText("Time\tV/V\n");
			stopButton.setEnabled(true);
			runButton.setEnabled(false);
			saveButton.setEnabled(false);
			changePiezoButton.setEnabled(false);
			worker = new ModelWorker();
			worker.execute();
//			plotter = new PlotWorker();
//			plotter.execute();
		}else if(e.getSource() == stopButton) {
			runButton.setEnabled(true);
			stopButton.setEnabled(false);
			saveButton.setEnabled(true);
			changePiezoButton.setEnabled(true);
			if(worker != null) {
				worker.cancel(true);
			}
			if(plotter!= null) {
				plotter.cancel(true);
			}
		}else if(e.getSource() == saveButton) {
			if(rbc != null) {
				// Open a save dialog - copy from run frame
				// call write_csv
			}
		}else if(e.getSource() == changePiezoButton) {
			piezoOptionsFrame = new OptionsFrame("PIEZO options","SettingFiles/piezoDSOptions.csv",DSOptions,this,"");
			piezoOptionsFrame.makeVisible();
		}
	}
	
	public static void main(String[] args) {
		new PiezoLifespan();
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}

}
