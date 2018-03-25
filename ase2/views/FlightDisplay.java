package ase2.views;

import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;

import ase2.interfaces.Observer;
import ase2.model.Flight;

public class FlightDisplay extends JPanel 
	implements Observer {
	JTextPane data;
	private static final long serialVersionUID = 1L;
	Flight flight;
	String name;
	
	public FlightDisplay(Flight flight) {
		this.flight = flight;
		name = flight.getFlightCode() + " " + flight.getDestination();
				
		flight.registerObserver(this);
		
		//set layout
		setLayout(new GridLayout(1,1));
		
		//add a border to the panel
		setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

		data = new JTextPane();
		data.setContentType("text/html");
		data.setEditable(false);
	
		data.setText("<html><p align = 'center'>" + name + "<br/>"
				+ flight.getTotalPassengersCheckedIn() + " checked in of "
					+ flight.getPassengersBookedAboard() + "<br/>"
					+ "Hold:<br/>"
					+ String.format("%.2f", flight.getTotalBaggageWeight()) + "kg/"
					+ String.format("%.2f", flight.getMaxBaggageWeight()) + "kg<br/>"
					+ String.format("%.2f", flight.getTotalBaggageVolume()/100) + "m<sup>2</sup>/"
					+ String.format("%.2f", flight.getMaxBaggageVolume()/100) + "m<sup>2</sup><br/>" // TODO Why is this divided by 100!?
					+ "<font color='white'>---------------------------------</font></p></html>");
		add(data);
	}

	@Override
	public void update() {
		//update display on GUI thread
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				data.setText("<html><p align = 'center'>" + name + "<br/>"
						+ flight.getTotalPassengersCheckedIn() + " checked in of "
							+ flight.getPassengersBookedAboard() + "<br/>"
							+ "Hold:<br/>"
							+ String.format("%.2f", flight.getTotalBaggageWeight()) + "kg/"
							+ String.format("%.2f", flight.getMaxBaggageWeight()) + "kg<br/>"
							+ String.format("%.2f", flight.getTotalBaggageVolume()/100) + "m<sup>2</sup>/"
							+ String.format("%.2f", flight.getMaxBaggageVolume()/100) + "m<sup>2</sup><br/>"
							+ "<font color='white'>---------------------------------</font></p></html>");	
			}
		});
	}
}
