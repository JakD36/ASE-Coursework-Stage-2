package ase2;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import ase2.interfaces.Observer;
import ase2.interfaces.Subject;
import ase2.model.Passenger;

public class QueueHandler implements Subject {
    private LinkedList<Passenger> queue;
    private boolean closed; 
    
	ArrayList<Observer> observers = new ArrayList<Observer>();
    
    public QueueHandler(){
        queue = new LinkedList<Passenger>();
        closed = false;
    }

    synchronized public void joinQueue(Passenger newPassenger){
    	notifyObservers();
        queue.add(newPassenger);
        notifyAll();
    }

    synchronized public Passenger removeNextPassenger() throws NoSuchElementException{
        while(queue.isEmpty() && !closed){ // TODO Discuss if this is correct way to do this
            try{wait();}catch(InterruptedException e){System.out.println("Remove next passenger: Thread was interupted");}
        }
        
        //get and remove Passenger
        Passenger removed = queue.remove();
        notifyObservers();
        
        return removed;
    }

    synchronized public void close(){
    	notifyObservers();
        closed = true;
    }
    
	@Override
	/**
	 * add an Observer to this object
	 */
	public void registerObserver(Observer obs) {
		this.observers.add(obs);
	}

	/**
	 * remove an Observer from this object
	 */
	@Override
	public void removeObserver(Observer obs) {
		this.observers.remove(obs);	
	}

	/**
	 * notify all Observers of an update
	 */
	@Override
	public void notifyObservers() {
		for (Observer obs : this.observers)
		{
			obs.update();
		}
	}
	
	@SuppressWarnings("unchecked")
	public LinkedList<Passenger> getCurrentQueue() {
		//clone returned list so it can be read safely by another thread
		return (LinkedList<Passenger>) queue.clone();
	}
}
