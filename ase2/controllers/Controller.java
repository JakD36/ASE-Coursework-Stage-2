package ase2.controllers;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import java.awt.event.ActionListener;
import java.io.IOException;
import java.awt.event.ActionEvent;

import ase2.interfaces.Observer;
import ase2.interfaces.Subject;
import ase2.simulation.Logging;
import ase2.simulation.Simulation;
import ase2.views.GUI;
import ase2.model.Passenger;

public class Controller{
    
    private GUI view; 
    private Simulation model;

    public Controller(GUI view,Simulation model){
        this.model = model;
        this.view = view;
        // Specify the listener for the view
        view.addStartListener( new StartListener() );
        // TODO add update Listener in sprint 3
        
        
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
							// TODO Auto-generated catch block
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
                if(model.getState() != Thread.State.TERMINATED)
                		model.start();
            }
        }
    }
}