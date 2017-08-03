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
		
		String options_file = "./resources/protocols/short.txt";
		String results_file = "./resources/traces/short.txt";
		
		options = LoadProtocol.loadOptions(options_file);
		LoadProtocol.printOptions(options);
		
		rbc = new RBC_model();
		welcomeframe = new WelcomeFrame(this,options,rbc);
		timeoptions = new TimeOptions(this,options,rbc);
		napumpscreenrs = new NaPumpScreenRS(this,options,rbc);
		
		welcomeframe.makeVisible();
		
	}
	public void doneMenu(MenuFrame mf) {
		if(mf instanceof WelcomeFrame) {
			this.napumpscreenrs.makeVisible();
		}
		if(mf instanceof NaPumpScreenRS) {
			System.out.println("Finished NaPumpScreen");
			this.timeoptions.makeVisible();
		}
		if(mf instanceof TimeOptions) {
			System.out.println("Finished time");
			this.doneTime();
		}
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
