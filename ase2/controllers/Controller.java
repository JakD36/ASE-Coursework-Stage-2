package ase2.controllers;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import ase2.interfaces.Observer;
import ase2.interfaces.Subject;
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
    }

    // inner class SetListener responds when user sets time
    public class StartListener implements ActionListener{
        public void actionPerformed(ActionEvent e){
            if(!model.isAlive()){
                model.start(); 
            }
        }
    }
}