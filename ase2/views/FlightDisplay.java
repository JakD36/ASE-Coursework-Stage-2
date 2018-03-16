package ase2.views;

import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import ase2.interfaces.Observer;
import ase2.model.Flight;

public class FlightDisplay extends JPanel 
	implements Observer {
	JEditorPane data;
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

		data = new JEditorPane();
		data.setContentType("text/html");
		data.setEditable(false);
		update();

		add(data);
	}

	@Override
	public void update() {
		// TODO Loop get flight details and put them in the editor pane
		System.out.println("updating flight");
		
		data.setText("<html><p align = 'center'>" + name + "<br/>"
				+ flight.getTotalPassengersCheckedIn() + " checked in of "
					+ flight.getPassengersBookedAboard() + "<br/>"
					+ "Hold:<br/>"
					+ flight.getTotalBaggageWeight() + "kg/"
					+ flight.getMaxBaggageWeight() + "kg<br/>"
					+ flight.getTotalBaggageVolume() + "cm<sup>2</sup>/"
					+ flight.getMaxBaggageVolume() + "<sup>2</sup></p></html>");
		
	}
}
