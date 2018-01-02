import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class RBCGui extends JFrame {
	private WelcomeFrame welcomeframe;
	private OptionsFrame timeoptions;
	private HashMap<String,String> options;
	private RBC_model rbc;
	private NaPumpScreenRS napumpscreenrs;
	
	private DSScreen dsScreen;
	public RBCGui() {
		
		options = new HashMap<String,String>();
		
//		String options_file = "./resources/protocols/short.txt";
//		String results_file = "./resources/traces/short.txt";
		
//		options = LoadProtocol.loadOptions(options_file);
//		LoadProtocol.printOptions(options);
		
		rbc = new RBC_model();
		welcomeframe = new WelcomeFrame(this,options,rbc);
		
//		timeoptions = new TimeOptions(this,options,rbc);
		timeoptions = new OptionsFrame("Time Options","resources/settingfiles/timeOptions.csv",this,options, this);
		napumpscreenrs = new NaPumpScreenRS(this,options,rbc);
		dsScreen = new DSScreen(this,options,rbc);
		welcomeframe.makeVisible();
		
	}
	public void doneMenu(JFrame mf) {
		if(mf instanceof WelcomeFrame) {
			this.napumpscreenrs.makeVisible();
		}
		if(mf instanceof NaPumpScreenRS) {
			System.out.println("Finished NaPumpScreen");
			this.dsScreen.makeVisible();
		}
		if(mf.getTitle().equals("Time Options")) {
			System.out.println("Finished time");
			this.doneTime();
		}
		if(mf instanceof DSScreen) {
			System.out.println("Finished DS");
			this.doneDS();
		}
	}
	
	public static void main(String[] args) {
		new RBCGui();
	}
	public void doneTime() {
		LoadProtocol.printOptions(options);
		dsScreen.makeVisible();
		
		
	}
	public void doneDS() {
		ArrayList<String> usedoptions = new ArrayList<String>();
		rbc.setup(options, usedoptions);
		rbc.setupDS(options, usedoptions);
		
		LoadProtocol.printOptions(options);

		
		rbc.runall();
		
		// save the file
	}
}
