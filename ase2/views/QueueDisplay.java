package ase2.views;


import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EtchedBorder;
import javax.swing.table.DefaultTableModel;

import ase2.interfaces.Observer;

public class QueueDisplay extends JPanel 
	implements Observer {

	//represents the underlying data in the JTable
	DefaultTableModel model;
	
	//table labels
	String[] labels = {"Booking Ref", "Flight"};
	
	//the actual table
	JTable table;
	
	private static final long serialVersionUID = 1L;
	
	public QueueDisplay(int id) {
		//set layout
		setLayout(new GridLayout(1,1));
		
		//add a border to the panel
		setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		
		model = new DefaultTableModel(0, 2);
		model.setColumnIdentifiers(labels);

		
		table = new JTable(model);
		JScrollPane scrollPane = new JScrollPane(table);
		
		//make the table uneditable
		table.setDefaultEditor(Object.class, null);

		model.addRow(new String[] {"test", "test2"});
		
		add(scrollPane);			
	}

	@Override
	public void update() {
		//create a new model to clear old data
		model = new DefaultTableModel(0, 2);
		model.setColumnIdentifiers(labels);
		table.setModel(model);
		
		//TODO Get queue and loop through it adding the Passenger details
	}
}
