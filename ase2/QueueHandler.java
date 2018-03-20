package ase2;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Random;

import ase2.interfaces.Observer;
import ase2.interfaces.Subject;
import ase2.model.Passenger;
import ase2.model.PassengerList;

public class QueueHandler implements Subject {
    private ArrayList<LinkedList<Passenger>> queues;
    private boolean closed; 
    Random rand = new Random();
    
	ArrayList<Observer> observers = new ArrayList<Observer>();
    
    public QueueHandler(int numberOfQueues){
        queues = new ArrayList<LinkedList<Passenger>>();
        closed = false;
        
        for(int i = 0; i < numberOfQueues; i++) {
        	queues.add(new LinkedList<Passenger>());
        }
    }

    /**
     * Adds Passenger to the shortest queue
     * @param newPassenger the Passenger to add
     */
     public void joinQueue(Passenger newPassenger){ 	
    	//find the shortest queue
    	LinkedList<Passenger> shortest = queues.get(0);
    	
    	for(LinkedList<Passenger> queue : queues) {
    		if(queue.size() < shortest.size())
    	    	shortest = queue;
    	}
    	
    	synchronized(shortest) {
	    	shortest.add(newPassenger);
	    	shortest.notifyAll();
	    	
    	}
    	
    	//notify security officer
    	synchronized(this) {
    		this.notifyAll();
    	}
    	
    	notifyObservers();
    }
    
    /**
     * Checks all queues to see if they have Passengers.
     * @return whether the queues are empty
     */
    public boolean isEmpty() {
		boolean empty = true;

		for(LinkedList<Passenger> queue : queues) {
    		if(queue.size() > 0)
    			empty = false;
    	}
		
		return empty;
    }

    public Passenger removeNextPassenger(int queueId) throws NoSuchElementException{
    	Passenger removed = null;
    	//sync on the specific queue
    	synchronized(queues.get(queueId)) {
	    	if(PassengerList.getInstance().getNoNotQueued() > 0) {
		        while(queues.get(queueId).size() < 1 && !closed){ // TODO Discuss if this is correct way to do this
		       		try{queues.get(queueId).wait();}catch(InterruptedException e){System.out.println("Remove next passenger: Thread was interupted");}
		        }  
	    	}
	    	//get and remove Passenger
	    	removed = queues.get(queueId).remove();
    	}
    	
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
	
	public LinkedList<Passenger> getCurrentQueue(int id) {
		//clone returned list so it can be read safely by another thread
		synchronized(queues.get(id)) {
			return (LinkedList<Passenger>) queues.get(id);
		}
	}
	
	public Passenger getRandomPassenger() {
		Passenger p = null;
		int queueNo;
		LinkedList<Passenger> q;
		
		while(!isEmpty() && p == null) {
			queueNo = rand.nextInt(queues.size());
			q = queues.get(queueNo);
			synchronized (q) {
				if(q.size() > 0)
					p = q.get(rand.nextInt(q.size()));
			}
		}
		
		return p;
	}
	
	public void removePassenger(Passenger p) {
		//very inefficient
		for(LinkedList<Passenger> q : queues) {
			synchronized(q) {
				q.remove(p);
			}
		}
	}
	
	public boolean isClosed() { 
		return closed;
	}
}
