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
	
	QueueHandler queues;	
	
	ArrayList<Observer> simObservers;
	
	/**
	 * Starts the program by initialising components of MVC pattern, gui, simulation and the controller
	 */
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		Simulation model = new Simulation();
		GUI view = new GUI(model);
		Controller controller = new Controller(view,model);
	}
	
	public Simulation() {
		
		
		//create new lists to accommodate for observers and checkin desks
		simObservers = new ArrayList<Observer>();
		desks = new ArrayList<CheckInHandler>();
		
		//create new desks 
		queues = new QueueHandler(2);
		desks.add(new CheckInHandler(queues, 0)); //copy this to add more checkin desks
		desks.add(new CheckInHandler(queues, 1)); //copy this to add more checkin desks
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
		//the number of times Passenger arrival has been evaluated
		//to compare with the correct number based on time
		long timesEvaluated = 0;
		//the amount of evaluations that should have occured
		long evaluationsPending = 0;
		
		while(simClock.getCurrentTime() < simEndTime) {
			
			// Randomly decide if passengers arrive at airport	
			long AverageTimeBetweenArrival = 2*60*1000; // 2 min on average between arrivals
			
			//the amount of evaluations that should have occurred
			evaluationsPending = (simClock.getCurrentTime()-simClock.getStartTime())/AverageTimeBetweenArrival;
			
			double chanceOfArriving = 0.7d/(double)PassengerList.getInstance().getTotalPassengers();
			
			while(evaluationsPending > timesEvaluated) {
				timesEvaluated++;
				for(int n = 0;n < PassengerList.getInstance().getPassengersNotQueued().size(); n++){
					if( (rand.nextDouble() < chanceOfArriving) ) {
						try{
							Passenger passenger = PassengerList.getInstance().getRandomToCheckIn();
							queues.joinQueue(passenger);
							log.writeEvent("Adding " + passenger.getBookingRefCode() + " to Queue at time >> "+simClock.getTimeString()+ ", "+ ++passengersAdded + " added.");
						}
						catch(NullPointerException e){
							
						}
					}
				}
			}
		}
		
		log.writeEvent("Simulation complete: " + passengersAdded + " added.");
		
		// for(CheckInHandler desk : desks) {
		// 	desk.open = false; //this line will terminate all the threads "nicely"
		// }
		
		//wake up any desks waiting for Passengers
		synchronized(queues) {
			queues.close();
			System.out.println(desks.get(0).getStatus());
			System.out.println(desks.get(1).getStatus());
			desks.get(0).interrupt();
			desks.get(1).interrupt();
			queues.notifyAll();
		}
		

		try{
			log.flush();
		}catch(IOException e){}
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
		return queues;
	}
}
