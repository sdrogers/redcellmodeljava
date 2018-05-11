import javax.swing.JFrame;

public class TestParamSelector {
	public static void main(String[] args) {
//		JFrame f = new JFrame();
//		f.setSize(500,500);
//		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		
//		ParameterSelector ps = new ParameterSelector("resources/settingfiles/transportDSOptions.csv");
//		f.add(ps);
//		
//		f.setVisible(true);
		OptionsFrame o = new OptionsFrame("Some options","resources/settingfiles/transportDSOptions.csv",null,null,new JFrame());
	}
}
