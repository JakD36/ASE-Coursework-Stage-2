package ase2;

import java.util.LinkedList;
import java.util.NoSuchElementException;

import ase2.model.Passenger;

import ase2.exceptions.IllegalReferenceCodeException;


public class QueueHandler {
    
    private LinkedList<Passenger> queue;
    
    public QueueHandler(){
        queue = new LinkedList<Passenger>();
    }

    synchronized public void joinQueue(Passenger newPassenger){
        queue.add(newPassenger);
    }

    synchronized public Passenger removeNextPassenger() throws NoSuchElementException{
        return queue.remove(); 
    }
}
