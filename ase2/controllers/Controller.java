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

    // inner class SetListener responds when user sets time
    public class StartListener implements ActionListener{
        public void actionPerformed(ActionEvent e){
        	//do not attempt to restart an active sim
            if(!model.isAlive()){
                if(model.getState() != Thread.State.TERMINATED){
						model.start();
					}
                else {
                	Clock.getInstance().resetClock();
                	PassengerList.reset();
                	FlightList.reset();
					model = new Simulation();
                	model.start();
                	view.setupGui(model);
                	// TODO check log is reset!
					view.addStartListener( new StartListener() );
					view.addSetSpeedListener( new setSpeedListener());
					
                }
            }
        }
	}
	
	// inner class SetListener responds when user sets time
	public class setSpeedListener implements ChangeListener {
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