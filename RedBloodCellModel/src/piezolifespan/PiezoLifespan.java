package piezolifespan;

import java.awt.BorderLayout;
import java.awt.Font;
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
	private JButton runButton,stopButton,saveButton,changePiezoButton,rsButton,mediumButton;
	private JTextArea modelOutput;
	private ModelWorker worker;
	private RBC_model rbc;
	private Plot2DPanel[] plot = new Plot2DPanel[4];
	private JTextField timeField,cycleField,caKField,pmcaKField,tnapField;
	private OptionsFrame piezoOptionsFrame,rsOptionsFrame,mediumOptionsFrame;
	HashMap<String,String> DSOptions,RSOptions,mediumOptions;
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
		rsButton = new JButton("Change Reference State");
		mediumButton = new JButton("Change Default Media");
		
		
		
		
		buttonPanel.add(runButton);
		buttonPanel.add(stopButton);
		buttonPanel.add(saveButton);
		buttonPanel.add(changePiezoButton);
		buttonPanel.add(rsButton);
		buttonPanel.add(mediumButton);
		
		runButton.addActionListener(this);
		stopButton.addActionListener(this);
		saveButton.addActionListener(this);
		changePiezoButton.addActionListener(this);
		rsButton.addActionListener(this);
		mediumButton.addActionListener(this);
		
		stopButton.setEnabled(false);
		saveButton.setEnabled(false);
		
		JPanel outputPanel = new JPanel(new GridLayout(2,0));
		modelOutput = new JTextArea(20,30);
		modelOutput.setFont(new Font("monospaced", Font.PLAIN, 12));
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
		caKField = new JTextField("7e-6");
		entryPanel.add(caKField);
		pmcaKField = new JTextField("7e-6");
		entryPanel.add(new JLabel("k(PMCA) (1/min): ",SwingConstants.RIGHT));
		entryPanel.add(pmcaKField);
		tnapField = new JTextField("0");
		entryPanel.add(new JLabel("TNaP (minutes): ",SwingConstants.RIGHT));
		entryPanel.add(tnapField);
		
		
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
		RSOptions = new HashMap<String,String>();
		mediumOptions = new HashMap<String,String>();
		
		DSOptions.put("Time", "1.0");
		DSOptions.put("Incorporate PIEZO stage","yes");
		DSOptions.put("piezo_start","0.0");
		DSOptions.put("Output Accuracy","6");
		Double stage_time = 1.0; // minutes
		Double open_time = 4.0*(1.0/60.0); // 4.0 seconds
		Double recovery_time = stage_time - open_time - 1e-6;
		DSOptions.put("piezo_recovery",""+recovery_time);
		DSOptions.put("Open state",""+0.4);
		
		DSOptions.put("Piezo Frequency factor", "0.0001");
		DSOptions.put("Piezo Cycles per print","111");
		DSOptions.put("PzKG","1.0");
		DSOptions.put("PzNaG", "0.8");
		DSOptions.put("PzAG","50.0");
		DSOptions.put("PzCaG","60.0");
		DSOptions.put("PMCA inhibition","0.0");
		
		DSOptions.put("Restore Medium","yes");
		
//		RSOptions.put("[Na]i","5.0");
//		RSOptions.put("[K]i","145.0");
//		RSOptions.put("Cell water content","0.82");
		
		mediumOptions.put("HEPES-Na concentration","10.0");
		mediumOptions.put("Medium pH","7.4");
		mediumOptions.put("NaCl","145.0");
		mediumOptions.put("KCl","5.0");
		mediumOptions.put("Mg concentration","0.2");
		mediumOptions.put("Ca concentration","1.0");
	}
	private class ModelWorker extends SwingWorker<Void,ResultHash> {
		public Void doInBackground() {
			rbc = new RBC_model();
//			HashMap<String,String> RSOptions = new HashMap<String,String>();
			
			
			
			
			rbc.setVerbose(false);
			System.out.println("Setting RS");
			rbc.setup(RSOptions, new ArrayList<String>());
			System.out.println("Done");
			Double time = rbc.getSamplingTime();
			Double max_time = Double.parseDouble(timeField.getText());
			int cycles_per_output = Integer.parseInt(cycleField.getText());
			int cycle_counter = 0;
			
			rbc.setMediumDefaults(mediumOptions);
			
			rbc.setPublish(true);
			rbc.publish();
			rbc.setPublish(false);
			publish(rbc.getLastResult()); 
			
			Double naK = Double.parseDouble(caKField.getText());
			Double pmcaK = Double.parseDouble(pmcaKField.getText());
			Double tNaP = Double.parseDouble(tnapField.getText());
			Double FMaxCa = rbc.getCaPump().getDefaultFcapm();
			Double FMaxNa = rbc.getNapump().getP_1();
			Double FMaxNaRev = rbc.getNapump().getP_2();
			Double timeInMinutes = 0.0;
			
			while(time < max_time) {
				timeInMinutes = time*60.0; // RBC time is in hours
				if(this.isCancelled()) {
					break;
				}
				System.out.println("Settuping up DS");
				rbc.setupDS(DSOptions, new ArrayList<String>());
				System.out.println("Done setup");
				
//				
//				rbc.getCaPump().setDefaultFcapm(FMaxCa - pmcaK*timeInMinutes);
//				rbc.getNapump().setP_1(FMaxNa - naK*timeInMinutes);
//				rbc.getNapump().setP_2(FMaxNaRev - naK*timeInMinutes);
//				
				
				// New exponential decay version
				rbc.getCaPump().setDefaultFcapm(FMaxCa*Math.exp(-pmcaK*timeInMinutes));
				if(timeInMinutes > tNaP) {
					rbc.getNapump().setP_1(FMaxNa*Math.exp(-naK*(timeInMinutes - tNaP)));
					rbc.getNapump().setP_2(FMaxNaRev*Math.exp(-naK*(timeInMinutes - tNaP)));
				}
				
				
				rbc.setPublish(false);
				rbc.runall(null);
				time = rbc.getSamplingTime();
				cycle_counter++;
				System.out.println(cycles_per_output);
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
			String newLine = String.format("%8.2f%8.4f%8.2f%8.2f%8.2f\n",
					lastItem.getTime(),
					lastItem.getItem("V/V"),
					lastItem.getItem("Em"),
					lastItem.getItem("FNaP"),
					lastItem.getItem("FCaP"));
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
		changePiezoButton.setEnabled(true);
	}
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == runButton) {
			this.modelOutput.setText(String.format("%8s%8s%8s%8s%8s\n", "Time","V/V","Em","FNaP","FCaP"));
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
		}else if(e.getSource() == rsButton) {
			rsOptionsFrame = new OptionsFrame("Reference state options","SettingFiles/RSOptions.csv",RSOptions,this,"");
			rsOptionsFrame.makeVisible();
		}else if(e.getSource() == mediumButton) {
			mediumOptionsFrame = new OptionsFrame("Default Medium options,","SettingFiles/mediumDefaultOptions.csv",mediumOptions,this,"");
			mediumOptionsFrame.makeVisible();
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