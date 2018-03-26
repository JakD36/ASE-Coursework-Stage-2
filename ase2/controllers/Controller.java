package ase2.controllers;

import java.util.*;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JSlider;

import java.awt.event.ActionListener;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.io.IOException;
import java.awt.event.ActionEvent;

import ase2.model.CheckInHandler;
import ase2.model.Flight;
import ase2.model.FlightList;
import ase2.model.PassengerList;
import ase2.simulation.Clock;
import ase2.simulation.Logging;
import ase2.simulation.Simulation;
import ase2.views.GUI;

public class Controller{
    
    // @SuppressWarnings("unused")
	private GUI view; 
    private Simulation model;


	/**
	 * Construct the controller for the MVC design pattern used for the model(simulation) and view(GUI).
	 * 
	 * Sets the listeners on the start button, the change in simulation speed time and for windows close.
	 * 
	 * @param the view for the MVC pattern.
	 * @param the model of the MVC pattern.
	 */
    public Controller(GUI view,Simulation model){
        this.model = model;
        this.view = view;
        // Specify the listener for the view
        view.addStartListener( new StartListener() );
		view.addSetSpeedListener( new setSpeedListener());
		
        
        
        //adding an event on closing the view to ensure the log flush
        view.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        view.addWindowListener(new java.awt.event.WindowAdapter() {
		    @Override
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
		        if (JOptionPane.showConfirmDialog(view, 
		            "Are you sure to quit the simulation?", "Really Closing?", 
		            JOptionPane.YES_NO_OPTION,
		            JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION){
		        		try {
							Logging.getInstance().flush();
						} catch (IOException e) {
							e.printStackTrace();
						}
		            System.exit(0);
		        }
		    }
		});
    }

    /**
	 * An implementation of ActionListener, for use with the start button on the GUI.
	 */
    public class StartListener implements ActionListener{

		/**
		 * On action performed checks if model is running, if not starts the simulation, or if it has finished restarts the sim.
		 * 
		 * @param The event on the start button.
		 */
        public void actionPerformed(ActionEvent e){
        	
            if(!model.isAlive()){ // If the simulation is running do nothing
                if(model.getState() != Thread.State.TERMINATED){ // If the thread hasnt been terminated already 
						model.start(); // start the simulation
					}
                else { // else if the thread has been stopped then we shoud reset the simulation
                	Clock.getInstance().resetClock();
                	FlightList.reset(); // Passenger list loads flightlist on reset so needs to go before/
                	PassengerList.reset();
                	
					model = new Simulation();
                	model.start();
                	view.setupGui(model);
					view.addStartListener( new StartListener() );
					view.addSetSpeedListener( new setSpeedListener());
					
                }
            }
        }
	}
	
	/**
	 * An implementation of ChangeListener, for use with the change speed slider on the GUI.
	 */
	public class setSpeedListener implements ChangeListener {
		/**
		 * If the state is changed, get the value from the slider and apply to the simulation speed.
		 * Loops through all the check-in desks and interrupts their threads to wake them if they are sleeping,
		 * this causes them to re-evaluate how long they need to sleep for to take into account the new speed of the simulation.
		 * 
		 * @param the change event performed on the JSlider.
		 */
		public void stateChanged(ChangeEvent e) {
			Clock myClock = Clock.getInstance();
			JSlider mySlider = (JSlider)e.getSource();
			ArrayList<CheckInHandler> desks = model.getCheckInDesks();
			for(int n = 0; n < desks.size(); n++){
				desks.get(n).interrupt();
			}
			myClock.setSpeed((long)mySlider.getValue());
			
		}
	}
}