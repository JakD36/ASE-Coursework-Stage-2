package ase2.views;

import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import ase2.interfaces.Observer;

public class DeskDisplay extends JPanel 
	implements Observer {

	JEditorPane data;
	private static final long serialVersionUID = 1L;

	public DeskDisplay(int id) {
		//set layout
		setLayout(new GridLayout(1,1));
		
		//add a border to the panel
		setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

		data = new JEditorPane();
		data.setContentType("text/html");
		data.setEditable(false);
		data.setText("<html><p align = 'center'>Desk #" + id + " goes here</p></html>");

		add(data);
	}

	@Override
	public void update() {
		// TODO Get Desk status and display it in the editor pane
		
	}
}
