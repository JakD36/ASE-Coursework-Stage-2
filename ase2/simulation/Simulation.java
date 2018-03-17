package ase2.simulation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import ase2.model.CheckInHandler;
import ase2.model.Flight;
import ase2.model.FlightList;
import ase2.model.Passenger;
import ase2.model.PassengerList;
import ase2.QueueHandler;
import ase2.interfaces.Observer;
import ase2.interfaces.Subject;
import ase2.simulation.Clock;
import ase2.views.GUI;
import ase2.controllers.Controller;
import ase2.simulation.Logging;

public class Simulation extends Thread implements Subject {
	
	long simSpeed = 1000; // Sim runs simSpeed times faster than real life!
	long simEnd = 23;
	long simEndTimems = simEnd*3600*1000;
	
	Clock simClock;

	int passengersAdded = 0;
	ArrayList<CheckInHandler> desks;
	//CheckInHandler desk;
	boolean allPassengersQueued = false;
	PassengerList passengers = PassengerList.getInstance(); // Think this makes it basically a global, defeating the purpose of the singleton
	ArrayList<Passenger> passengersNotQueued;
	
	ArrayList<Observer> simObservers;
	
	
	QueueHandler queue;
	
	public static void main(String[] args) {
		Simulation model = new Simulation();
		GUI view = new GUI(model);
		Controller controller = new Controller(view,model);
	}
	
	/**
	 * Currently runs for a set period of time. Eventually this should be
	 * changed to close when all desks close or all passengers are checked in
	 */
	public Simulation() {

		
		// Collect passengers to be added into to system
		passengersNotQueued = new ArrayList<Passenger>();
		for(Passenger p : passengers.getNotCheckedIn().values()) {
			passengersNotQueued.add(p);
		}
		
		//create new lists to accommodate for observers and checkin desks
		simObservers = new ArrayList<Observer>();
		desks = new ArrayList<CheckInHandler>();
		
		//create new desks 
		queue = new QueueHandler();
		desks.add(new CheckInHandler(queue)); //copy this to add more checkin desks
	}

	public synchronized void run(){
		simClock = Clock.getInstance();
		simClock.startClock();
		Logging log = Logging.getInstance();
		log.writeEvent("Simulation Instiated: " + simClock.getTimeString());
		// Set up our simulation
		
		Random rand = new Random(); // Create our random number generator 
		for (CheckInHandler desk : desks) {
			desk.start(); //start all the checkin threads
		}
		
		
		// Update simulation		
		
		while(simClock.getCurrentTime() < simEndTimems) {
			
			//== Slow down our sim by a little bit, so we can see what happens and stuff
			try { 
				Thread.sleep(6*3600*1/simClock.getSpeed());
			} catch (InterruptedException e) {
			 	System.out.println("There was an issue trying to put the thread to sleep");
			}

			

			// Randomly decide to if passenger arrives at airport
			if( (rand.nextDouble() < 0.25d) && !allPassengersQueued) {
				try{
					Passenger passenger = getRandomToCheckIn();
					queue.joinQueue(passenger);
					
					//TODO: add setters for baggage in Passenger Class
				
					log.writeEvent("Adding " + passenger.getBookingRefCode() + " to Queue at time >> "+simClock.getTimeString()+ ", "+ ++passengersAdded + " added.");
				}
				catch(NullPointerException e){
					
				}
			}
		}
		
		// TODO log simulation has ended
		// System.out.println("Simulation complete: " + passengersAdded + " added.");
		log.writeEvent("Simulation complete: " + passengersAdded + " added.");
		
		for(CheckInHandler desk : desks) {
			desk.open = false; //this line will terminate all the threads "nicely"
		}
		// TODO unsure if this is correct way of doing things, will need to discuss
		
		queue.close();
		System.out.println(passengersNotQueued.size() + " did not join queue.");
		try{
			log.flush();
		}catch(IOException e){}
	}
	
	/**
	 * Returns a random Passenger who has not checked in
	 * or no null if all Passengers are checked in
	 * @return a random Passenger who has not checked in or null
	 * if all have checked in
	 */
	public Passenger getRandomToCheckIn() {
		int passengersLeft = passengersNotQueued.size();
		System.out.println("Passengers left to enter system "+passengersLeft);
		if(passengersLeft > 0) {
			Random rand = new Random();
			int randInt = rand.nextInt(passengersLeft);
			Passenger passenger = passengersNotQueued.remove(randInt);
			
			//check if Passenger was the last one
			if(passengersNotQueued.size() == 0)
				allPassengersQueued = true;
			
			return (passenger);
		}
		return null;
	}

	@Override
	public void registerObserver(Observer obs) {
		this.simObservers.add(obs);
		
	}

	@Override
	public void removeObserver(Observer obs) {
		this.simObservers.remove(obs);
		
	}

	@Override
	public void notifyObservers() {
		for (Observer obs : this.simObservers)
		{
			obs.update();
		}
	}

	public Passenger[] getPassengersNotQueuedList() {
		Passenger[] passengers = new Passenger[this.passengersNotQueued.size()];
		this.passengersNotQueued.toArray(passengers);
		
		return passengers;
	}

	public Passenger[]  getQueuedPassengersList() {
		//TODO: get method in QueueHandler needed!
		
		return null;
	}

	public CheckInHandler[] getCheckInDesks() {
		CheckInHandler[] desks = new CheckInHandler[this.desks.size()];
		this.desks.toArray(desks);
		
		return desks;
	}

	public Flight[] getFlights() {
		Collection<Flight> flights = FlightList.getInstance().getValues();
		Flight[] f = new Flight[flights.size()];
		
		flights.toArray(f);
		
		return f;
	}
	

	/**
	 * return the the queue
	 * @return the current queue
	 */
	public QueueHandler getQueueHandler() {
		return queue;
	}
}
