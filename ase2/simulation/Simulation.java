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
	
	long simEndTime = 0; // When the simulation will end!
	
	ArrayList<CheckInHandler> desks;
	int passengersAdded = 0;
	boolean allPassengersQueued = false;
	ArrayList<Passenger> passengersNotQueued;
	QueueHandler queue;
	int totalPassengers;
	
	
	ArrayList<Observer> simObservers;
	
	/**
	 * Starts the program by initialising components of MVC pattern, gui, simulation and the controller
	 */
	public static void main(String[] args) {
		Simulation model = new Simulation();
		GUI view = new GUI(model);
		Controller controller = new Controller(view,model);
	}
	
	public Simulation() {
		// Collect passengers to be added into to system
		PassengerList passengers = PassengerList.getInstance(); 
		passengersNotQueued = new ArrayList<Passenger>();
		for(Passenger p : passengers.getNotCheckedIn().values()) {
			passengersNotQueued.add(p);
		}
		totalPassengers = passengersNotQueued.size();
		
		//create new lists to accommodate for observers and checkin desks
		simObservers = new ArrayList<Observer>();
		desks = new ArrayList<CheckInHandler>();
		
		//create new desks 
		queue = new QueueHandler();
		desks.add(new CheckInHandler(queue)); //copy this to add more checkin desks
		desks.add(new CheckInHandler(queue)); //copy this to add more checkin desks
	}

	public synchronized void run(){
		
		// Get the clock and start the clock!
		Clock simClock = Clock.getInstance();
		simClock.startClock();
		
		// Get our log 
		Logging log = Logging.getInstance();
		log.writeEvent("Simulation Instiated: " + simClock.getTimeString());
		// log.enableDebug();
		
		Random rand = new Random(); // Create our random number generator 
		
		// Start each of the check in desk threads
		for (CheckInHandler desk : desks) {
			desk.start(); 
			long closeTime = desk.getClosureTime();
			if(closeTime > simEndTime){
				simEndTime = closeTime;
			}
		}

		// TODO not happy with this to be honest it ends up in busy waiting 
		// add passengers to the system!
		while(simClock.getCurrentTime() < simEndTime) {
			
			// Randomly decide if passengers arrive at airport	
			long AverageTimeBetweenArrival = 2*60*1000; // 2 min on average between arrivals
			try { 
				long sleepTime = AverageTimeBetweenArrival/simClock.getSpeed();
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
				System.out.println("Simulation was interupted from sleep");
			}

			double chanceOfArriving = 0.5d/(double)totalPassengers;
			
			for(int n = 0;n < passengersNotQueued.size(); n++){
				if( (rand.nextDouble() < chanceOfArriving) ) {
					try{
						Passenger passenger = getRandomToCheckIn();

						queue.joinQueue(passenger);
					
						log.writeEvent("Adding " + passenger.getBookingRefCode() + " to Queue at time >> "+simClock.getTimeString()+ ", "+ ++passengersAdded + " added.");
					}
					catch(NullPointerException e){
						
					}
				}
			}
		}
		
		log.writeEvent("Simulation complete: " + passengersAdded + " added.");
		
		for(CheckInHandler desk : desks) {
			desk.open = false; //this line will terminate all the threads "nicely"
			desk.interrupt();
		}
		
		
		queue.close();
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
		//TODO: get method in QueueHandler needed! Add it then!
		
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
