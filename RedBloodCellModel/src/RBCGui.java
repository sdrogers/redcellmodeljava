import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class RBCGui {
	private WelcomeFrame welcomeframe;
	private TimeOptions timeoptions;
	private HashMap<String,String> options;
	private RBC_model rbc;
	private NaPumpScreenRS napumpscreenrs;
	public RBCGui() {
		
		options = new HashMap<String,String>();
		welcomeframe = new WelcomeFrame("Red Blood Cell Model",this);
		
		String options_file = "./resources/protocols/short.txt";
		String results_file = "./resources/traces/short.txt";
		
		options = LoadProtocol.loadOptions(options_file);
		LoadProtocol.printOptions(options);
		
		rbc = new RBC_model();
		
		timeoptions = new TimeOptions(this,options);
		napumpscreenrs = new NaPumpScreenRS(this,options,rbc);
		
		
	}
	public void doneWelcome() {
		this.napumpscreenrs.makeVisible();
	}
	public void doneNaRS() {
		this.timeoptions.makeVisible();
	}
	public static void main(String[] args) {
		new RBCGui();
	}
	public void doneTime() {
		LoadProtocol.printOptions(options);
		ArrayList<String> usedoptions = new ArrayList<String>();
		rbc.setup(options, usedoptions);
		rbc.setupDS(options, usedoptions);
		
		JFileChooser jfc = new JFileChooser();
		int returnVal = jfc.showSaveDialog(null);
		System.out.println(jfc.getSelectedFile().getPath());
		
		rbc.runall();
		
		rbc.writeCsv(jfc.getSelectedFile().getPath());
	}
}
