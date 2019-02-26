package piezolifespan;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
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
	private RBC_model rbc;
	private Plot2DPanel[] plot = new Plot2DPanel[4];
	private JTextField timeField,cycleField,caKField,pmcaKField;
	private OptionsFrame piezoOptionsFrame;
	HashMap<String,String> DSOptions;
	private JFileChooser jfc = new JFileChooser();

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
		JPanel topPanel = new JPanel(new GridLayout(0,2));
		topPanel.add(new JScrollPane(modelOutput));

		JPanel entryPanel = new JPanel(new GridLayout(0,2));
		topPanel.add(entryPanel);
		
		entryPanel.add(new JLabel("Max model time (hours): ",SwingConstants.RIGHT));
		timeField = new JTextField("1.0");
		entryPanel.add(timeField);
		entryPanel.add(new JLabel("Number of PIEZO cycles per output: ",SwingConstants.RIGHT));
		cycleField = new JTextField("1440");
		entryPanel.add(cycleField);
		
		entryPanel.add(new JLabel("k(Na/K pump) (1/min): ",SwingConstants.RIGHT));
		caKField = new JTextField("1.7e-6");
		entryPanel.add(caKField);
		pmcaKField = new JTextField("5.2e-6");
		entryPanel.add(new JLabel("k(PMCA) (1/min): ",SwingConstants.RIGHT));
		entryPanel.add(pmcaKField);
		
		
		outputPanel.add(topPanel);
	
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
			rbc.setVerbose(false);
			rbc.setup(RSOptions, new ArrayList<String>());
			Double time = rbc.getSamplingTime();
			Double max_time = Double.parseDouble(timeField.getText());
			int cycles_per_output = Integer.parseInt(cycleField.getText());
			int cycle_counter = 0;
			/*
			 * Need to decide if we should have the initial point

			rbc.setPublish(true);
			rbc.publish();
			rbc.setPublish(false);
			publish(rbc.getLastResult()); 
			*/

			Double naK = Double.parseDouble(caKField.getText());
			Double pmcaK = Double.parseDouble(pmcaKField.getText());
			Double FMaxCa = rbc.getCaPump().getDefaultFcapm();
			Double FMaxNa = rbc.getNapump().getP_1();
			Double FMaxNaRev = rbc.getNapump().getP_2();
			Double timeInMinutes = 0.0;
			while(time < max_time) {
				timeInMinutes = time*60.0; // RBC time is in hours
				if(this.isCancelled()) {
					break;
				}
				rbc.setupDS(DSOptions, new ArrayList<String>());
				rbc.getCaPump().setDefaultFcapm(FMaxCa - pmcaK*timeInMinutes);
				rbc.getNapump().setP_1(FMaxNa - naK*timeInMinutes);
				rbc.getNapump().setP_2(FMaxNaRev - naK*timeInMinutes);
				rbc.setPublish(false);
				rbc.runall(null);
				time = rbc.getSamplingTime();
				cycle_counter++;
				if(cycle_counter == cycles_per_output) {
					// make some output
					rbc.setPublish(true);
					rbc.publish();
					rbc.setPublish(false);
					publish(rbc.getLastResult());
					cycle_counter = 0;
				}
				System.out.println("fcapm: " + rbc.getCaPump().getDefaultFcapm());
				System.out.println("p1: " + rbc.getNapump().getP_1());
				System.out.println("p2: " + rbc.getNapump().getP_2());
				
				
			}
			rbc.setPublish(true);
			rbc.publish();
			rbc.setPublish(false);
			publish(rbc.getLastResult());
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
		String[][] series = {{"V/V",},{"CNa","CK","CA"},{"Em"},{"rA","rH"}};
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
		}

	}
	private void resetButtons() {
		runButton.setEnabled(true);
		stopButton.setEnabled(false);
		saveButton.setEnabled(true);
	}
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == runButton) {
			this.modelOutput.setText("Time\tV/V\tEm\n");
			stopButton.setEnabled(true);
			runButton.setEnabled(false);
			saveButton.setEnabled(false);
			changePiezoButton.setEnabled(false);
			worker = new ModelWorker();
			worker.execute();
		}else if(e.getSource() == stopButton) {
			runButton.setEnabled(true);
			stopButton.setEnabled(false);
			saveButton.setEnabled(true);
			changePiezoButton.setEnabled(true);
			if(worker != null) {
				worker.cancel(true);
			}
		}else if(e.getSource() == saveButton) {
			if(rbc != null) {
				int returnVal = jfc.showSaveDialog(this);
				String fName = jfc.getSelectedFile().getPath();
				if(!fName.endsWith(".csv")) {
					fName += ".csv";
				}
				rbc.writeCsv(fName);
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
