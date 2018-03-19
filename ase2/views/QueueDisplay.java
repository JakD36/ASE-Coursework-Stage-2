package ase2.views;


import java.awt.GridLayout;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
import javax.swing.table.DefaultTableModel;

import ase2.QueueHandler;
import ase2.interfaces.Observer;
import ase2.model.Passenger;

public class QueueDisplay extends JPanel 
	implements Observer {

	//represents the underlying data in the JTable
	DefaultTableModel model;
	
	//table labels
	String[] labels = {"Booking Ref", "Flight"};
	
	//the actual table
	JTable table;
	
	//the QueueHandler this QueueDisplay represents
	QueueHandler queueHandler;
	
	int queueId;
	
	private static final long serialVersionUID = 1L;
	
	public QueueDisplay(QueueHandler queueHandler, int queueId) {
		this.queueId = queueId;
		
		queueHandler.registerObserver(this);
		
		//set layout
		setLayout(new GridLayout(1,1));
		
		//set the QueueHandler this QueueDisplay represents
		this.queueHandler = queueHandler;
		
		//add a border to the panel
		setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		
		model = new DefaultTableModel(0, 2);
		model.setColumnIdentifiers(labels);

		
		table = new JTable(model);
		JScrollPane scrollPane = new JScrollPane(table);
		
		//make the table uneditable
		table.setDefaultEditor(Object.class, null);

		model.addRow(new String[] {"", ""});
		
		add(scrollPane);			
	}

	@Override
	public synchronized void update() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				//create a new model to clear old data
				model = new DefaultTableModel(0, 2);
				model.setColumnIdentifiers(labels);		
				
				
				//get a copy of the latest list of Passengers
				LinkedList<Passenger> currentQueue = queueHandler.getCurrentQueue(queueId);
				
				
				//add the Passengers to the new model
				//get lock
				synchronized(currentQueue) {
					for(Passenger p : currentQueue) {
						model.addRow(new String[] {p.getBookingRefCode(), p.getFlight().getFlightCode()});
					}
				}
				
				//if their are no Passengers, create a placeholder
				if(currentQueue.size() < 1)
					model.addRow(new String[] {"empty", "empty"});
				
				//the model must be update from the GUI thread by creating 
				//an anonymous object implementing runnable
		
				//update the table with the new model		
				table.setModel(model);
			}
		});
	}
}
