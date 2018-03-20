package ase2.views;

import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;

import ase2.interfaces.Observer;
import ase2.model.SecurityOfficer;

public class SecurityDisplay extends JPanel 
	implements Observer {

	JTextPane data;
	private static final long serialVersionUID = 1L;
	int id;
	SecurityOfficer officer;

	public SecurityDisplay(SecurityOfficer officer, int id) {
		this.officer = officer;		
		this.id = id;
		
		officer.registerObserver(this);
		
		//set layout
		setLayout(new GridLayout(1,1));
		
		//add a border to the panel
		setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

		data = new JTextPane();
		data.setContentType("text/html");
		data.setEditable(false);
		data.setText("<html><p align = 'center'>Security officer #" + id 
				+ ":<br/>" + officer.getStatus() + "</p></html>");

		add(data);
	}

	@Override
	public void update() {
		//update display on GUI thread
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				data.setText("<html><p align = 'center'>Security officer #" + id 
						+ ":<br/>" + officer.getStatus() + "</p></html>");
			}
		});
	}
}
