package guicomponents;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import utilities.HelpText;
/*
 * A class to display HTML formated help text
 */
public class HelpScreen extends JFrame implements ActionListener {
	private JButton closeButton;
	public HelpScreen() {
		this.setSize(700,900);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		JTabbedPane tabbedPane = new JTabbedPane();
		
		JPanel rs = new JPanel(new BorderLayout());
		JLabel rsContent = new JLabel(HelpText.rsHelp);
		rsContent.setBorder(new EmptyBorder(10,10,10,10));
		rs.add(new JScrollPane(rsContent,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER),BorderLayout.CENTER);
		tabbedPane.addTab("Reference State", null, rs,
                "Reference State");
		
		
		JPanel ds = new JPanel(new BorderLayout());
		JLabel dsContent = new JLabel(HelpText.dsHelp);
		dsContent.setBorder(new EmptyBorder(10,10,10,10));
		ds.add(new JScrollPane(dsContent,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER),BorderLayout.CENTER);
		tabbedPane.addTab("Dynamic State", null, ds,
                "Dynamic State");
		
		
		JPanel pr = new JPanel(new BorderLayout());
		JLabel prContent = new JLabel(HelpText.piezoHelp);
		prContent.setBorder(new EmptyBorder(10,10,10,10));
		pr.add(new JScrollPane(prContent,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER),BorderLayout.CENTER);
		tabbedPane.addTab("Piezo routine", null, pr,
                "Piezo routine");
		
		closeButton = new JButton("Close");
		closeButton.addActionListener(this);
		
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(tabbedPane,BorderLayout.CENTER);
		mainPanel.add(closeButton,BorderLayout.SOUTH);
		this.add(mainPanel);
		this.setVisible(false);
	}

	
	public void actionPerformed(ActionEvent e) {
		this.setVisible(false);
		
	}
	
	public static void main(String[] args) {
		new HelpScreen();
	}
	
}
