package ase2.views;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;

import ase2.model.CheckInHandler;
import ase2.exceptions.IllegalReferenceCodeException;

/**
 * Program entry point. Contains main method, which creates
 * an instance of this GUI class.
 * 
 * references:
 * https://docs.oracle.com/javase/tutorial/uiswing/layout/grid.html
 * https://docstore.mik.ua/orelly/java/exp/ch12_05.htm
 * https://docs.oracle.com/javase/tutorial/uiswing/components/border.html
 */
public class GUI extends JFrame implements ActionListener, 
	WindowListener
{

	private static final long serialVersionUID = 1L;
	//GUI controls need class wide access
	JTextField txtBookingRef;
	JTextField txtSurname;
	JLabel lblBookingRef;
	JLabel lblSurname;
	JLabel lblResponse;
	JButton btnCheckIn;
	CheckInHandler checkInHandler;
	
	//displays the current queue
	QueueDisplay queueDisplay;
	
	/**
	 * Entry point to program. Creates GUI window.
	 * @param args program arguments
	 */
	public static void main(String[] args) {
		new GUI();
	}
	

	public GUI() {
		//add window listener to generate report and terminate program
		this.addWindowListener(this);		
		
		//set the title
		this.setTitle("Queue Check-In Simulation");
		
		//constraints for adding items to gridbag layout
		GridBagConstraints c = new GridBagConstraints();
		
		//create a panel for the input boxes and their labels
		JPanel panel = new JPanel();
		//set its layout to GridBagLayout
		panel.setLayout(new GridBagLayout());
		
		//add a border to the panel
		panel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		
		//items should fill horizontally
		c.fill = GridBagConstraints.HORIZONTAL;
		
		//labels for input get 1.0 x weight
		c.weightx = 1.0;
		
		//add a title label
		//use constraint to span all columns
		//of the first row
		JLabel title = new JLabel("Queue Check-In Simulation");
		title.setHorizontalAlignment(JLabel.CENTER);
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 8;
		//set margins
		c.insets = new Insets(5,5,2,5);
		panel.add(title, c);
		
		//set margins
		c.insets = new Insets(2,5,2,5);
		
		//add separator to row
		c.gridy = 1;
		panel.add(new JSeparator(), c);
		
		//add a QueueDisplay
		c.gridy = 2;
		c.weighty = 0.5;
		c.fill = GridBagConstraints.BOTH;
		queueDisplay = new QueueDisplay(1);
		panel.add(queueDisplay, c);
		
		//set margins
		c.insets = new Insets(2,5,2,0);
		
		//add a first check in desk
		c.gridwidth = 4;
		c.gridx = 0;
		c.gridy = 3;
		c.weighty = 0.3;
		c.weightx = 0.5;
		c.fill = GridBagConstraints.BOTH;
		panel.add(new DeskDisplay(1), c);
		
		c.insets = new Insets(2,5,2,5);
		
		//add a second check in desk
		c.gridwidth = 4;
		c.gridx = 3;
		c.gridy = 3;
		c.fill = GridBagConstraints.BOTH;
		panel.add(new DeskDisplay(2), c);
		
		//set margins
		c.insets = new Insets(2,5,2,5);
		
		//add a first flight
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 4;
		c.weighty = 0.2;
		c.fill = GridBagConstraints.BOTH;
		panel.add(new FlightDisplay(1), c);
		
		//add a second flight
		c.insets = new Insets(2,0,2,0);
		c.gridwidth = 1;
		c.gridx = 2;
		c.gridy = 4;
		c.weighty = 0.2;
		c.fill = GridBagConstraints.BOTH;
		panel.add(new FlightDisplay(2), c);
		
		//add a third flight
		c.insets = new Insets(2,5,2,0);
		c.gridwidth = 1;
		c.gridx = 4;
		c.gridy = 4;
		c.weighty = 0.2;
		c.fill = GridBagConstraints.BOTH;
		panel.add(new FlightDisplay(3), c);
		
		//add a fourth flight
		c.insets = new Insets(2,5,2,5);
		c.gridwidth = 1;
		c.gridx = 6;
		c.gridy = 4;
		c.weighty = 0.2;
		c.fill = GridBagConstraints.BOTH;
		panel.add(new FlightDisplay(4), c);
		
		this.add(panel);
		this.setSize(800, 600);
		
		//centre window
		this.setLocationRelativeTo(null);
		
		//should stay as last line of GUI creation
		//to avoid weird behaviour on Mac
		this.setVisible(true);
		
		try {
			Thread.sleep(2500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
						"Invalid " + name + ". Please reenter.",
						"Invalid " + name,
						JOptionPane.ERROR_MESSAGE);
			} catch (NullPointerException e) {
				return -1;
			}
		}
		return dim;
	}
		
	
	@Override
	/**
	 * Triggered upon any event whose source has this class as
	 * its ActionListener.
	 * 
	 * Deals with GUI events by determining the source and
	 * taking appropriate action.
	 * 
	 * @param arg0 the source event that triggered the method
	 */
	public void actionPerformed(ActionEvent arg0) {
		//determine source of event
		//if event is btnCheckIn click, attempt to check passenger in
		if(arg0.getSource() == btnCheckIn) {
			//make booking ref lowercase
			txtBookingRef.setText
				(txtBookingRef.getText().toLowerCase());
			
			//get and validate user input
			String bookingRef = txtBookingRef.getText();
			String lastName = txtSurname.getText();
			
			//if booking ref is invalid, inform user and return
			if(!bookingRef.matches("[a-z]{3}[0-9]{4}")) {
				lblResponse.setText("<html><font color = 'red'>"
						+ "Invalid booking reference!</font>"
						+ "</html>");
				return;
			}
			
			if(lastName.length() < 1) {
				lblResponse.setText("<html><font color = 'red'>"
						+ "Please supply a last name!</font>"
						+ "</html>");
				return;
			}
			
			//try to check user in
			try {			
				//try to check in
				boolean matches = checkInHandler.checkDetails(bookingRef, lastName);
				
				//if the booking ref exists and matches the surname, proceed
				if(matches) {
					//declare dimensions float array
					float[] dimensions = new float[3];
					
					//declare weight float
					float weight = 0;
					
					//track if user cancels operation
					boolean isCancelled = false;
					
					//populate floats with information from user
					//check user hasn't cancelled between each
					dimensions[0] = getFloat("width");
					
					//if the previous window wasn't cancelled
					//get the next value
					if(dimensions[0] != -1)
						dimensions[1] = getFloat("height");
					else
						//user has cancelled the operation
						isCancelled = true;

					//if the previous wasn't cancelled and
					//no other previous windows were cancelled,
					//get the next value
					if(dimensions[1] != -1 && !isCancelled)
						dimensions[2] = getFloat("depth");
					else
						//user has cancelled the operation
						isCancelled = true;

					if(dimensions[2] != -1 && !isCancelled)
						weight = getFloat("weight");
					else
						//user has cancelled the operation
						isCancelled = true;
					
					if(weight == -1)
						isCancelled = true;
					
					//make sure values have been supplied for all baggage attributes
					if(!isCancelled) {
						//try to process passenger
						float fees = 
								checkInHandler.processPassenger(bookingRef, dimensions, weight);
						
						//if there are no baggage fees, inform user
						if(fees == 0)
							lblResponse.setText("User checked in. Baggage ok.");
						//check for error code
						else if(fees == -1) {
							lblResponse.setText("<html><font color = 'red'>" +
									"Check in error." +
									"</font></html>");
						}
						//if there are baggage fees, inform user
						else {
							//format string to 2dp and use red colouring
							String feeString = String.format("%.2f", fees);
							
							lblResponse.setText("<html>User checked in. "
									+ "Collect baggage fee: <font color = 'red'>"
									+ feeString + ".</font></html>");
							}
						} else {
							//if the user cancelled whilst inputting baggage details
							//update status
							lblResponse.setText("<html><font color = 'red'>"
									+ "Check In Cancelled!</font></html>");
					//if the booking ref exists, but does not match a user
					} 
				}
				else {
					//inform user
					lblResponse.setText("<html><font color = 'red'>"
							+ "Booking Reference does not match surname!"
							+ "</font></html>");
						
				} 
			//this exception is thrown by CheckInHandler if check in
			//booking ref does not exist
			} catch(IllegalReferenceCodeException e) {
				//inform user
				lblResponse.setText("<html><font color = 'red'>" + e.getMessage()
				+ "</font></html>");
			}
		}
	}

	@Override
	public void windowActivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosed(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	/**
	 * Code triggered when GUI is closed.
	 */
	public void windowClosing(WindowEvent arg0) {
		System.exit(0);
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowIconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowOpened(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
