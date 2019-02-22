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

import org.math.plot.*;


public class PiezoLifespan extends JFrame implements ActionListener{
	private JButton runButton,stopButton;
	private JTextArea modelOutput;
	private ModelWorker worker;
	private PlotWorker plotter;
	private RBC_model rbc;
	private  Plot2DPanel plot;
	private JTextField timeField;
	public PiezoLifespan() {
		this.setSize(500, 500);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		layoutFrame();
		this.setVisible(true);
	}
	
	private void layoutFrame() {
		JPanel mainPanel = new JPanel(new BorderLayout());
		this.add(mainPanel);
		JPanel buttonPanel = new JPanel(new GridLayout(0,2));
		mainPanel.add(buttonPanel,BorderLayout.SOUTH);
		
		runButton = new JButton("Run");
		stopButton = new JButton("Stop");
		
		
		buttonPanel.add(new JLabel("Max model time (hours): "));
		timeField = new JTextField("1.0");
		buttonPanel.add(timeField);
		
		
		buttonPanel.add(runButton);
		buttonPanel.add(stopButton);
		
		runButton.addActionListener(this);
		stopButton.addActionListener(this);;
		
		stopButton.setEnabled(false);
		
		JPanel outputPanel = new JPanel(new GridLayout(2,0));
		modelOutput = new JTextArea(20,30);
		outputPanel.add(new JScrollPane(modelOutput));
		plot = new Plot2DPanel("SOUTH");
		outputPanel.add(plot);
		mainPanel.add(outputPanel,BorderLayout.CENTER);
		
		
	}
	
	private class ModelWorker extends SwingWorker<Void,ResultHash> {
		public Void doInBackground() {
			rbc = new RBC_model();
			HashMap<String,String> RSOptions = new HashMap<String,String>();
			HashMap<String,String> DSOptions = new HashMap<String,String>();
			DSOptions.put("Time", "1.0");
			DSOptions.put("Incorporate PIEZO stage","yes");
			DSOptions.put("piezo_start","0.0");
			DSOptions.put("Output Accuracy","6");
			Double stage_time = 1.0; // minutes
			Double open_time = 1.0/120.0; // 0.5 seconds
			Double recovery_time = stage_time - open_time - 1e-6;
			DSOptions.put("piezo_recovery",""+recovery_time);
			
			rbc.setVerbose(false);
			rbc.setup(RSOptions, new ArrayList<String>());
			Double time = rbc.getSamplingTime();
			Double max_time = Double.parseDouble(timeField.getText());
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
					rbc.setPublish(true);
					rbc.publish();
//					r.add(rbc.getLastResult());
					rbc.setPublish(false);
//				}
				publish(rbc.getLastResult());
				
//				rbc.writeCsv("/Users/simon/TempStuff/piezo_test.csv");
//				rbc.clearResults();
			}
			
//			rbc.setResults(r);
//			rbc.writeCsv(out_file);
			resetButtons();
			return null;
		}
		public void process(List<ResultHash> res) {
			ResultHash lastItem = res.get(res.size()-1);
			Double time = lastItem.getTime();
			String newLine = String.format("%4.2f\t%8.4f\n",lastItem.getTime(),lastItem.getItem("V/V"));
			modelOutput.append(newLine);
		}
		
	}
	
	private class PlotWorker extends SwingWorker<Void,PlotData> {
		public Void doInBackground() {
			while(true) {
				if(this.isCancelled()) {
					break;
				}
				try {
					Thread.sleep(1000);
					// Make an output array and publish it
					int n_results = rbc.getResults().size();
					double[] x = new double[n_results];
					double[] y = new double[n_results];
					String combined = "";
					String[] series = {"V/V",};
					for(String s: series) {
						int pos = 0;
						for(ResultHash r: rbc.getResults()) {
							x[pos] = r.getTime();
							y[pos] = r.getItem(s);
							pos++;
						}
//						plot.addLinePlot(s, x,y);
//						combined += s + " ";
						publish(new PlotData(s,x,y));
					}
				}catch(InterruptedException e) {
					// do nothing, it's all good
				}
			}
			return null;
		}
		public void process(List<PlotData> s) {
			PlotData lastRes = s.get(s.size()-1);
			plot.removeAllPlots();
			plot.addLinePlot(lastRes.s, lastRes.x, lastRes.y);
		}
	}
	private class PlotData {
		public String s;
		public double[] x;
		public double[] y;
		public PlotData(String s,double[] x,double[] y) {
			this.s = s;
			this.x = x;
			this.y = y;
		}
	}
	private void resetButtons() {
		runButton.setEnabled(true);
		stopButton.setEnabled(false);
		if(plotter != null) {
			plotter.cancel(true);
		}
	}
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == runButton) {
			this.modelOutput.setText("Time\tV/V\n");
			stopButton.setEnabled(true);
			runButton.setEnabled(false);
			worker = new ModelWorker();
			worker.execute();
			plotter = new PlotWorker();
			plotter.execute();
		}else if(e.getSource() == stopButton) {
			runButton.setEnabled(true);
			stopButton.setEnabled(false);
			if(worker != null) {
				worker.cancel(true);
			}
			if(plotter!= null) {
				plotter.cancel(true);
			}
		}
	}
	
	public static void main(String[] args) {
		new PiezoLifespan();
	}

}
