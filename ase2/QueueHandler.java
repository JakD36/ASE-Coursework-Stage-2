package ase2;

import java.util.LinkedList;
import java.util.NoSuchElementException;

import ase2.model.Passenger;

import ase2.exceptions.IllegalReferenceCodeException;


public class QueueHandler {
    
    private LinkedList<Passenger> queue;
    private boolean closed; 
    
    public QueueHandler(){
        queue = new LinkedList<Passenger>();
        closed = false;
    }

    synchronized public void joinQueue(Passenger newPassenger){
        queue.add(newPassenger);
        notifyAll();
    }

    synchronized public Passenger removeNextPassenger() throws NoSuchElementException{
        while(queue.isEmpty() && !closed){ // TODO Discuss if this is correct way to do this
            try{wait();}catch(InterruptedException e){System.out.println("Thread was interupted");}
        }
        return queue.remove(); 
    }

    synchronized public void close(){
        closed = true;
    }
}
