package ase2.views;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import javax.swing.event.ChangeListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;

import ase2.model.CheckInHandler;
import ase2.model.Flight;
import ase2.simulation.Clock;
import ase2.simulation.Simulation;


/**
 * Program entry point. Contains main method, which creates
 * an instance of this GUI class.
 * 
 * references:
 * https://docs.oracle.com/javase/tutorial/uiswing/layout/grid.html
 * https://docstore.mik.ua/orelly/java/exp/ch12_05.htm
 * https://docs.oracle.com/javase/tutorial/uiswing/components/border.html
 */
public class GUI extends JFrame
{

	private static final long serialVersionUID = 1L;
	//GUI controls need class wide access
	JTextField txtBookingRef;
	JTextField txtSurname;
	JLabel lblBookingRef;
	JLabel lblSurname;
	JLabel lblResponse;
	JSlider sldSpeed;
	JButton btnCheckIn;
	CheckInHandler checkInHandler;
	
	//simulation control buttons
	JButton btnStartSim;

	//the content panel
	JPanel panel;
	

	//displays the current queue
	QueueDisplay queueDisplay;

	
	//desks
	CheckInHandler[] desks;
	
	//flights
	Flight[] flights;

	/**
	 * Add listener for start button
	 * @param al the ActionListener 
	 */
	public void addStartListener(ActionListener al){
		btnStartSim.addActionListener((al));
	}
	
	/**
	 * Add listener for speed slider
	 * @param cl the ChangeListener
	 */
	public void addSetSpeedListener(ChangeListener cl){
		sldSpeed.addChangeListener((cl));
	}
	
	/**
	 * Instantiate GUI frame
	 * @param sim the Simulation represented
	 */
	public GUI(Simulation sim) {
		//set the title
		this.setTitle("Queue Check-In Simulation");
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//set size and min size
		this.setSize(800, 600);
		this.setMinimumSize(new Dimension(800, 600));
		
		//centre window
		this.setLocationRelativeTo(null);
		
		//create the panel
		setupGui(sim);
	}
	
	/**
	 * Creates or replaces the display panel
	 * @param sim the Simulation represented
	 */
	public void setupGui(Simulation sim) {
		this.add(new JButton("Test"));
		if(panel != null) {
			this.remove(panel);
		}
		
		//get the desks
		ArrayList<CheckInHandler> desks = sim.getCheckInDesks();
		
		//get the flights
		flights = sim.getFlights();
		
		//constraints for adding items to gridbag layout
		GridBagConstraints c = new GridBagConstraints();
		
		//create a panel for the input boxes and their labels
		panel = new JPanel();
		//set its layout to GridBagLayout
		panel.setLayout(new GridBagLayout());
		
		//add a border to the panel
		panel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		
		//items should fill horizontally
		c.fill = GridBagConstraints.HORIZONTAL;
		
		//labels for input get 1.0 x weight
		c.weightx = 0.8;
		
		//add a title label
		//use constraint to span all columns
		//of the first row
		JLabel title = new JLabel("Queue Check-In Simulation");
		title.setHorizontalAlignment(JLabel.CENTER);
		//set x grid position to 0
		c.gridx = 0;
		//set y grid position to 0
		c.gridy = 0;
		//set component width to full row
		c.gridwidth = 8;
		//set margins
		c.insets = new Insets(5,5,2,5);
		
		panel.add(title, c);
		
		c.gridx = 5;
		c.weightx = 0.1;
		panel.add(new ClockDisplay(sim), c);
		
		c.gridx = 0;
		
		c.weightx = 1;
		
		//set margins
		c.insets = new Insets(2,5,2,5);
		
		//add separator to row
		c.gridy = 1;
		panel.add(new JSeparator(), c);
	
		//add speed slider
		sldSpeed = new JSlider();
		sldSpeed.setMaximum(4000);
		sldSpeed.setMinimum(1);
		sldSpeed.setMajorTickSpacing(100);
		sldSpeed.setSnapToTicks(true);
		sldSpeed.setPaintTicks(true);
		sldSpeed.setValue((int)Clock.getInstance().getSpeed());
		c.gridy = 2;
		c.gridx = 0;
		c.gridwidth = 2;
		panel.add(sldSpeed,c);
		
		
		//add start button
		c.gridwidth = 2;
		c.gridx = 6;
		btnStartSim = new JButton("Start");
		c.gridy = 2;
		panel.add(btnStartSim, c);
		

		c.gridwidth = 4;
		c.insets = new Insets(2,5,2,0);
		
		//add a QueueDisplay
		c.gridy = 3;
		c.gridx = 0;
		c.weighty = 0.8;
		c.fill = GridBagConstraints.BOTH;
		//create a component to monitor the queue and pass it
		//a reference to the queue
		queueDisplay = new QueueDisplay(sim.getQueueHandler(), 0);
		panel.add(queueDisplay, c);
		
		c.insets = new Insets(2,5,2,5);
		
		//add a QueueDisplay
		c.gridy = 3;
		c.gridx = 4;
		c.weighty = 0.8;
		c.fill = GridBagConstraints.BOTH;
		//create a component to monitor the queue and pass it
		//a reference to the queue
		queueDisplay = new QueueDisplay(sim.getQueueHandler(), 1);
		panel.add(queueDisplay, c);
		
		//set margins
		c.insets = new Insets(2,5,2,0);
		
		//add a first check in desk
		c.weightx = 0.5;
		c.gridwidth = 4;
		c.gridx = 0;
		c.gridy = 4;
		c.weighty = 0.2;
		c.weightx = 0.5;
		c.fill = GridBagConstraints.BOTH;
		DeskDisplay dd = new DeskDisplay(desks.get(0), 1);
		panel.add(dd, c);

		c.insets = new Insets(2,5,2,5);
		
		//add a second check in desk
		c.gridwidth = 4;
		c.gridx = 3;
		c.gridy = 4;
		c.fill = GridBagConstraints.BOTH;
		dd = new DeskDisplay(desks.get(1), 2);
		panel.add(dd, c);
		sim.registerObserver(dd);
		
		//set margins
		c.insets = new Insets(2,5,2,5);
		
		//add a first flight
		c.weightx = 0.25;
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 5;
		c.weighty = 0.075;
		c.fill = GridBagConstraints.BOTH;
		FlightDisplay fd = new FlightDisplay(flights[0], sim);
		panel.add(fd, c);
		sim.registerObserver(fd);
		
		//add a second flight
		c.insets = new Insets(2,0,2,0);
		c.gridwidth = 1;
		c.gridx = 2;
		c.gridy = 5;
		c.weighty = 0.075;
		c.fill = GridBagConstraints.BOTH;
		fd = new FlightDisplay(flights[1], sim);
		panel.add(fd, c);
		sim.registerObserver(fd);
		
		//add a third flight
		c.insets = new Insets(2,5,2,0);
		c.gridwidth = 1;
		c.gridx = 4;
		c.gridy = 5;
		c.weighty = 0.075;
		c.fill = GridBagConstraints.BOTH;
		fd = new FlightDisplay(flights[2], sim);
		panel.add(fd, c);
		sim.registerObserver(fd);
		
		//add a fourth flight
		c.insets = new Insets(2,5,2,5);
		c.gridwidth = 1;
		c.gridx = 6;
		c.gridy = 5;
		c.weighty = 0.075;
		c.fill = GridBagConstraints.BOTH;
		fd = new FlightDisplay(flights[3], sim);
		panel.add(fd, c);
		sim.registerObserver(fd);
		
		//add a security officer display
		c.insets = new Insets(2,5,2,5);
		c.gridwidth = 8;
		c.gridx = 0;
		c.gridy = 6;
		c.weighty = 0.075;
		c.fill = GridBagConstraints.BOTH;
		SecurityDisplay sd = 
				new SecurityDisplay(sim.getSecurityOfficer(), 1);
		panel.add(sd, c);
		sim.registerObserver(sd);
		
		this.add(panel);
		
		//should stay as last line of GUI creation
		//to avoid weird behaviour on Mac
		this.setVisible(true);
	}
	
	
	/**
	 * Requests a named value for an attribute of a passenger's baggage using
	 * a dialog box.
	 * 
	 * references:
	 * http://hajsoftutorial.com/showinputdialogget-integer-value/
	 * https://docs.oracle.com/javase/tutorial/uiswing/components/dialog.html
	 * @throws NullPointerException if the user clicks cancel.
	 * @param name the name of the value to ask for
	 * @return the value taken from the user or -1 if they click cancel
	 */
	public float getFloat(String name) throws NullPointerException {
		//set dimensions to negative value as program loops
		//input request until positive value is entered
		float dim = -1;
		
		String input;
		
		//loop until user enters a valid positive value
		while(dim < 0) {
			try {
			input = JOptionPane.showInputDialog
					(this, "Enter Baggage " + name);
			dim = Float.parseFloat(input);
			if(dim < 0) throw new NumberFormatException();

			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(this, 
						"Invalid " + name + ". Please re-enter.",
						"Invalid " + name,
						JOptionPane.ERROR_MESSAGE);
			} catch (NullPointerException e) {
				return -1;
			}
		}
		return dim;
	}
}
