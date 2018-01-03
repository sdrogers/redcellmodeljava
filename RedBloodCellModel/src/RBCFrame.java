import javax.swing.JFrame;

public class RBCFrame extends JFrame {
	protected JFrame parent;
	protected String title;
	public RBCFrame(String title, JFrame parent) {
		this.title = title;
		this.parent = parent;
		
		this.setTitle(title);
		this.setSize(400, 400);
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		
	}
	public void childFinished(RBCFrame child) {
		child.setVisible(false);
		this.setVisible(true);
	}
}
