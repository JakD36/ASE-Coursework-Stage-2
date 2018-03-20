package ase2.views;

import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;

import ase2.interfaces.Observer;
import ase2.model.CheckInHandler;

public class DeskDisplay extends JPanel 
	implements Observer {

	JTextPane data;
	private static final long serialVersionUID = 1L;
	int id;
	CheckInHandler desk;

	public DeskDisplay(CheckInHandler desk, int id) {
		this.desk = desk;		
		this.id = id;
		
		desk.registerObserver(this);
		
		//set layout
		setLayout(new GridLayout(1,1));
		
		//add a border to the panel
		setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

		data = new JTextPane();
		data.setContentType("text/html");
		data.setEditable(false);
		data.setText("<html><p align = 'center'>Desk #" + id 
				+ " is<br/>" + desk.getStatus() + ".</p></html>");

		add(data);
	}

	@Override
	public void update() {
		//update display on GUI thread
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				data.setText("<html><p align = 'center'>Desk #" + id 
						+ " is<br/>" + desk.getStatus() + ".</p></html>");
			}
		});
	}
}
