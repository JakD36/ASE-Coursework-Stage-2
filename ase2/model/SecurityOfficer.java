package ase2.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import ase2.QueueHandler;
import ase2.interfaces.Observer;
import ase2.interfaces.Subject;
import ase2.simulation.Clock;
import ase2.simulation.Logging;
import ase2.simulation.Simulation;

public class SecurityOfficer extends Thread implements Subject {
	long observeTime = 60 * 1000 * 2; //the time taken to observe a passenger
	long questionTime = 60 * 1000 * 10; //the time taken to interrogate a passenger
	long detainTime = 60 * 1000 * 7; //the time taken to detain a passenger
	Clock clock = Clock.getInstance(); //get the clock singleton instance
	QueueHandler queues; //the QueueHandler 
	HashSet<Passenger> alreadyObserved = new HashSet<Passenger>(); //to ensure Passengers aren't observed twice
	Random rand = new Random(); //for generating random numbers
	Simulation sim; //the sim model
	volatile String status; //the current status
	ArrayList<Observer> observers = new ArrayList<Observer>(); //the views observing this subject
	int totalDetained = 0; //total passengers detained
	float totalSeized = 0; //total contraband seized
	
	/**
	 * Create a new security officer.
	 * @param sim the simulation model
	 */
	public SecurityOfficer(Simulation sim) {
		this.sim = sim; 
		this.queues = sim.getQueueHandler();
		//update status String
		status = "Waiting to start.<br/><br/><br/>";
	}
	
	/**
	 * Main activity loop
	 */
	public void run() {
		//update status String
		status = "Security officer started.<br/><br/><br/>";
		notifyObservers();
		Logging log = Logging.getInstance();
		while(PassengerList.getInstance().getNotCheckedIn().size() > 0 
				&& sim.getState() != Thread.State.TERMINATED && !queues.isClosed())
		{
			try {
				observeRandomPassenger();
			} catch (InterruptedException e) {
				log.writeEvent("Security officer interrupted");
			}
		}
		
		status = "Security officer finished.<br/>"
				+ "&pound;" + String.format("%.2f", totalSeized) + " of "
				+ "contraband was seized.<br/>" + totalDetained + " passengers"
						+ " were detained.";
		notifyObservers();
	}
	
	
	/**
	 * Observe a random passenger
	 * @throws InterruptedException
	 */
	@SuppressWarnings("unchecked")
	private void observeRandomPassenger() throws InterruptedException {
		Passenger observed;
		
		observed = null;
		while((observed = queues.getRandomPassenger()) 
				== null || alreadyObserved.contains(observed)) {
			//update status String
			status = "Looking for suspicious passengers.<br/><br/><br/>";
			notifyObservers();
			synchronized(queues) {
				queues.wait();
			}
		}
		
		alreadyObserved.add(observed);

		//update status String
		status = "Observing " + observed.getBookingRefCode() + ".<br/><br/><br/>";
		notifyObservers();
		
		sleep(observeTime/clock.getSpeed());
		
		//should the Passenger be interrogated?
		//there's a 1 in 3 chance
		if(rand.nextInt(3) == 1) {
			removePassenger((ArrayList<CheckInHandler>) sim.getCheckInDesks().clone(), 
					sim.getCheckInDesks(), observed);
			interrogatePassenger(observed);
		}
	}
	
	/**
	 * Attempts to remove a Passenger from the queue
	 * 
	 * This method functions recursively to acquire the locks on all desks,
	 * to ensure that the Passenger isn't already checking in when the remove
	 * attempt is made.
	 * 
	 * Recursively getting the locks also ensures that the desks don't try and
	 * do anything with the Passenger whilst this remove is occurring.
	 * 
	 * @param desksSubset the desks left to lock
	 * @param desks all the desks
	 * @param p the passenger to remove
	 */
	private void removePassenger(ArrayList<CheckInHandler> desksSubset, 
			ArrayList<CheckInHandler> desks, Passenger p) {
		if(desks.size() > 0) {
			//make sure Passenger isn't currently checking in before removing
			//so the Passenger isn't interrogated whilst checking in
			synchronized(desksSubset.get(0)) {
				if(desksSubset.size() > 1) {
						//desksSubset.remove(0);
						removePassenger
							(new ArrayList<CheckInHandler>
							(desksSubset.subList(1, 
									desksSubset.size())), desks, p);
				} else {
					//notify observers of status change
					notifyObservers();
					
					//pull Passenger from queue
					queues.removePassenger(p);
				}
			}
		}
	}
	
	/**
	 * Interrogates a Passenger
	 * @param p the Passenger to interrogate
	 * @throws InterruptedException
	 */
	private void interrogatePassenger(Passenger p) throws InterruptedException {
		//update status String
		status = "Interrogating " + p.getBookingRefCode() + ".<br/><br/><br/>";

		//notify observers of status change
		notifyObservers();
		
		//sleep while Passenger is questioned
		sleep(questionTime/clock.getSpeed());
		
		float seized = 0;
		
		//was anything seized? 1 in 2 chance
		if(rand.nextInt(2) == 1) {
			//how much was seized?
			seized = rand.nextFloat() * 1000;
			
			//update status String
			status = "Seized &pound;" + String.format("%.2f", seized) + " of contraband.<br/>"
					+ "Detaining " + p.getBookingRefCode() + " " + p.getFirstName()
					+ " " + p.getLastName() + ".<br/><br/>";
			
			//add to totals
			totalSeized += seized;
			totalDetained++;
			
			//inform observers of state change			
			notifyObservers();
			
			sleep(detainTime/clock.getSpeed());
		} else {
			//if not detained and not already checked in, 
			//let them rejoin at the back of the queue
			if(!PassengerList.getInstance()
					.getCheckedIn().containsKey(p.getBookingRefCode())) {
				//update status String
				notifyObservers();
				
				//add the Passenger back to the queue
				queues.joinQueue(p);
			}
		}
	}
	
	/**
	 * Returns the current status of the Security Officer
	 * @return current status
	 */
	public String getStatus() {
		return status;
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
}