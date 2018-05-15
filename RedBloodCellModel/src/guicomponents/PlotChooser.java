package guicomponents;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import utilities.Parameter;
import utilities.ResultHash;

public class PlotChooser extends JFrame implements ActionListener {
	private final ArrayList<ResultHash> results;
	private final String[] publishOrder;
	private final JButton plotButton;
	private final JList<String> plotList;
	private final DefaultListModel<String> plotPars;
	
	public PlotChooser(ArrayList<ResultHash> results,String[] publishOrder) {
		this.results = results;
		this.publishOrder = publishOrder;
		this.setSize(400,400);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setVisible(true);
		
		plotPars = new DefaultListModel<String>();
		plotList = new JList<String>();
		plotList.setModel(plotPars);
		
		for(String s: publishOrder) {
			plotPars.addElement(s);
		}
		JPanel contentPanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		
		contentPanel.add(new JLabel("<html>Double click a variable to plot<br> or select 1 or more variables and then click plot</html>"),c);
		c.gridy = 1;
		contentPanel.add(new JScrollPane(plotList),c);
		
		c.gridy = 2;
		plotButton = new JButton("Plot");
		plotButton.addActionListener(this);
		contentPanel.add(plotButton,c);
		this.add(contentPanel);
		
		plotList.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				if(evt.getClickCount() == 2) {
					String selected = plotList.getSelectedValue();
					if(selected != null) {
						String[] series = {selected};
						new PlotFrame(results,series);
					}
				}
			}
		});
		
	}
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == plotButton) {
			// Get all selected elements and add to an array
			Object[] oArray = plotList.getSelectedValuesList().toArray();
			if(oArray.length>0) {
				String[] series = new String[oArray.length];
				int pos = 0;
				for(Object o: oArray) {
					series[pos++] = (String)o;
				}
				new PlotFrame(results,series);
			}
		}
	}
}
