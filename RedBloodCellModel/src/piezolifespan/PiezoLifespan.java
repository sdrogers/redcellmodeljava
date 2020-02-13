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
	
	
	private String[] publish_order = {"V/V","Vw","Hct","Em","pHi","pHo","MCHC",
            "Density","QNa","QK","QA","QCa","QMg","CNa","CK","CA","CH/nM","CCa2+","CMg2+",
            "CX","CHb","fHb","COs","MOs","rA","rH","nHb","MNa","MK","MA","MH/nM","MB","MCat","MCaf",
            "MMgt","MMgf","FNaP","FACo","FKCo","FNaCo","FCaP","FKP","FNa","FKGgardos","FKG","FK",
            "FA","FH","FCa","FW","FNaG","FAG","FHG","FCaG","FAJS","FHJS","FA23Ca","FA23Mg",
            "EA","EH","EK","ENa","FzKG","FzNaG","FzAG","FzCaG","fHb*CHb","nX","Msucr","Mgluc-",
            "Mgluc+","EN test"};
	
	
	private JButton runButton,stopButton,saveButton,changePiezoButton,rsButton;
	private JTextArea modelOutput;
	private ModelWorker worker;
	private RBC_model rbc;
	private Plot2DPanel[] plot = new Plot2DPanel[4];
	private JTextField timeField,cycleField,caKField,pmcaKField,tnapField;
	private OptionsFrame piezoOptionsFrame,rsOptionsFrame,mediumOptionsFrame;
	HashMap<String,String> DSOptions,RSOptions,mediumOptions;
	private JFileChooser jfc = new JFileChooser();
	
	private ArrayList<ResultHash> piezoResults;

	
	public PiezoLifespan() {
		this.setSize(1500, 1000);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		layoutFrame();
		this.setVisible(true);
		makeOptionsDefault();
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
		
		
		
		
		
		buttonPanel.add(runButton);
		buttonPanel.add(stopButton);
		buttonPanel.add(saveButton);
		buttonPanel.add(changePiezoButton);
		buttonPanel.add(rsButton);
		
		
		runButton.addActionListener(this);
		stopButton.addActionListener(this);
		saveButton.addActionListener(this);
		changePiezoButton.addActionListener(this);
		rsButton.addActionListener(this);
		
		
		stopButton.setEnabled(false);
		saveButton.setEnabled(false);
		
		JPanel outputPanel = new JPanel(new GridLayout(2,0));
		modelOutput = new JTextArea(20,30);
		modelOutput.setFont(new Font("monospaced", Font.PLAIN, 12));
		JPanel topPanel = new JPanel(new GridLayout(0,2));
		topPanel.add(new JScrollPane(modelOutput));

		JPanel entryPanel = new JPanel(new GridLayout(0,2));
		topPanel.add(entryPanel);
		
		entryPanel.add(new JLabel("Lifespan duration (h): ",SwingConstants.RIGHT));
		timeField = new JTextField("2880");
		entryPanel.add(timeField);
		entryPanel.add(new JLabel("Data output periodicity (min): ",SwingConstants.RIGHT));
		cycleField = new JTextField("1440");
		entryPanel.add(cycleField);
		
		entryPanel.add(new JLabel("kNaP (1/min): ",SwingConstants.RIGHT));
		caKField = new JTextField("3e-5");
		entryPanel.add(caKField);
		pmcaKField = new JTextField("8e-6");
		entryPanel.add(new JLabel("kCaP (1/min): ",SwingConstants.RIGHT));
		entryPanel.add(pmcaKField);
		tnapField = new JTextField("115200");
		entryPanel.add(new JLabel("TNaP (min): ",SwingConstants.RIGHT));
		entryPanel.add(tnapField);
		
		
		outputPanel.add(topPanel);
	
		JPanel justPlots = new JPanel(new GridLayout(2,2));
		for(int i=0;i<4;i++) {
			plot[i] = new Plot2DPanel("SOUTH");
			plot[i].getAxis(0).setLabelText("Time");
			justPlots.add(plot[i]);
		}
		
		plot[0].getAxis(1).setLabelText("RCV");
		plot[1].getAxis(1).setLabelText("CK-CA-CNa");
		plot[2].getAxis(1).setLabelText("EK");
		plot[3].getAxis(1).setLabelText("pHi");
		
		outputPanel.add(justPlots);
		mainPanel.add(outputPanel,BorderLayout.CENTER);
		
		
	}
	private void makeOptionsDefault() {
		DSOptions = new HashMap<String,String>();
		RSOptions = new HashMap<String,String>();
//		mediumOptions = new HashMap<String,String>();

		DSOptions.put("Time", "1.0");
		DSOptions.put("Pz stage no or yes","yes");
//		DSOptions.put("piezo_start","0.0"); // is this used
		DSOptions.put("Accuracy","6");
		DSOptions.put("PzCaG", "70");
		
//		Double stage_time = 1.0; // minutes
//		Double open_time = 4.0*(1.0/60.0); // 4.0 seconds
//		Double recovery_time = stage_time - open_time - 1e-6;
//		DSOptions.put("piezo_recovery",""+recovery_time);
//		DSOptions.put("Open state",""+0.4);
		
		DSOptions.put("PzFrequencyFactor", "0.001");
//		DSOptions.put("Piezo Cycles per print","111");
//		DSOptions.put("PzKG","0.0");
//		DSOptions.put("PzNaG", "0.0");
//		DSOptions.put("PzAG","50.0");
//		DSOptions.put("PzCaG","10.0");
//		DSOptions.put("PMCA inhibition","0.0");
//		DSOptions.put("Transit cell volume fraction","0.9");
//		DSOptions.put("Piezo JS Inhibition/Stimulation","0.0");
		DSOptions.put("Restore Medium","yes");
		
		
//		DSOptions.put("Restored Medium HEPES-Na concentration","10.0");
//		DSOptions.put("Restored Medium pH","7.4");
//		DSOptions.put("Restored Medium Na","145.0");
//		DSOptions.put("Restored Medium K","5.0");
//		DSOptions.put("Restored Medium Mg","0.2");
//		DSOptions.put("Restored Medium Ca","1.0");
		
		RSOptions.put("Na/K pump Na efflux","-3.2");
		RSOptions.put("CK","145.0");
		RSOptions.put("CNa","5.0");
		RSOptions.put("CA","95.0");
		RSOptions.put("Vw","0.85");
//		RSOptions.put("PMCA Fmax","12.0");
//		RSOptions.put("PKGardosMax","30.0");
		RSOptions.put("KCa Gardos channel","0.01");
										

	}
	private class ModelWorker extends SwingWorker<Void,ResultHash> {
		public Void doInBackground() {
			piezoResults = new ArrayList<ResultHash>();
			rbc = new RBC_model();
			
			rbc.setLifespan(true);
			
			rbc.setPublishOrder(publish_order);
			
			
			rbc.setVerbose(false);
			System.out.println("Setting RS");
			rbc.setup(RSOptions, new ArrayList<String>());
			System.out.println("Done");
			

			/*
			 * Following chunk runs a 2 minute blank, to get to SS
			 */
			HashMap<String,String> tempDSOptions = new HashMap<String,String>();
			tempDSOptions.put("Time","2.0");
			tempDSOptions.put("CVF","0.00001");
			rbc.setupDS(tempDSOptions, new ArrayList<String>());
			rbc.runall(null);
			
			Double time = rbc.getSamplingTime();
			Double max_time = Double.parseDouble(timeField.getText());
			int cycles_per_output = Integer.parseInt(cycleField.getText());
			int cycle_counter = 0;
						
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
					rbc.setPublish(true);
					rbc.publish();
					rbc.setPublish(false);
					ResultHash finalPiezoResult = rbc.getFinalPiezoResult();
					piezoResults.add(finalPiezoResult);
					ResultHash r = rbc.getLastResult();
//					r.setItem("TransitHct", rbc.getFinalPiezoHct());
					publish(r);
					cycle_counter = 0;
				}
				
				
			}
			rbc.setPublish(true);
			rbc.publish();
			rbc.setPublish(false);
			publish(rbc.getLastResult());
			resetButtons();
			return null;
		}
		@Override
		public void process(List<ResultHash> res) {
			ResultHash lastItem = res.get(res.size()-1);
			Double time = lastItem.getTime();
			String newLine = String.format("%8.2f %8.4f %8.2f %8.2f %16.2f %8.2f %10d\n",
					lastItem.getTime(),
					lastItem.getItem("V/V"),
					lastItem.getItem("Em"),
					lastItem.getItem("FNaP"),
					1000.0*rbc.getFinalPiezoCCa(),
					lastItem.getItem("TransitHct"),
					rbc.getTotalCycleCount());
			modelOutput.append(newLine);
			make_plots();
		}
		
	}
	private void make_plots() { 
		synchronized(rbc) {
			int n_results = rbc.getResults().size();
			String[][] series = {{"V/V",},{"CNa","CK","CA"},{"EK"},{"pHi"}};
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

	}
	private void resetButtons() {
		runButton.setEnabled(true);
		stopButton.setEnabled(false);
		saveButton.setEnabled(true);
		changePiezoButton.setEnabled(true);
	}
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == runButton) {
			this.modelOutput.setText(String.format("%8s %8s %8s %8s %16s %8s %10s\n", "Time","RCV","Em","FNaP","TransCCa(x1000)","TransHct","Iterations"));
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
				String[] tokens = fName.split("[.]");
				String transitFName = tokens[0] + "_transit." + tokens[1];
				rbc.writeCsv(fName);
				rbc.writeCsv(transitFName,piezoResults);
			}
		}else if(e.getSource() == changePiezoButton) {
			piezoOptionsFrame = new OptionsFrame("PIEZO options","SettingFiles/piezoDSOptions.csv",DSOptions,this,"");
			piezoOptionsFrame.makeVisible();
		}else if(e.getSource() == rsButton) {
			rsOptionsFrame = new OptionsFrame("Reference state options","SettingFiles/piezoLifespanRSOptions.csv",RSOptions,this,"");
			rsOptionsFrame.makeVisible();
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
