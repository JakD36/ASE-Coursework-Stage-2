package ase2.simulation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Random;

import com.sun.nio.sctp.Notification;

import ase2.model.CheckInHandler;
import ase2.model.Passenger;
import ase2.model.PassengerList;
import ase2.QueueHandler;
import ase2.simulation.Clock;

public class Simulation {
	
	long simSpeed = 1000; // Sim runs simSpeed times faster than real life!
	long simEnd = 23;
	long simEndTimems = simEnd*3600*1000;
	
	Clock simClock;

	int passengersAdded = 0;
	// ArrayList<CheckInHandler> desks;
	CheckInHandler desk;
	boolean allPassengersQueued = false;
	PassengerList passengers = PassengerList.getInstance(); // Think this makes it basically a global, defeating the purpose of the singleton
	ArrayList<Passenger> passengersNotQueued;
	
	
	//Collections handout states, "please don’t use any of the Queue
	//implementations which handle concurrent access in the coursework, such as
	//ConcurrentLinkedQueue, since this would not help you to understand 
	//the basic principles and problems of using threads."
	//Therefore, a non-thread safe Queue has been used so that thread handling
	//techniques can be demonstrated. LinkedList implements the Queue interface.
	QueueHandler queue;
	
	public static void main(String[] args) {
		new Simulation();
	}
	
	/**
	 * Currently runs for a set period of time. Eventually this should be
	 * changed to close when all desks close or all passengers are checked in
	 */
	public Simulation() {

		// Set up our simulation
		simClock = Clock.getInstance();
		Logging log = Logging.getInstance();
		Random rand = new Random(); // Create our random number generator 
		// Collect passengers to be added into to system
		passengersNotQueued = new ArrayList<Passenger>();
		for(Passenger p : passengers.getNotCheckedIn().values()) {
			passengersNotQueued.add(p);
		}

		queue = new QueueHandler();
		desk = new CheckInHandler(queue);
		desk.start();
		
		// TODO Log start of sim
		// System.out.println("Simulation Instiated: " + simClock.getTimeString());
		log.writeEvent("Simulation Instiated: " + simClock.getTimeString());
		
		
		
		// Update simulation		
		
		while(simClock.getCurrentTime() < simEndTimems) {
			
			// Slow down our sim by a little bit, so we can see what happens and stuff
			// try { 
			// 	Thread.sleep(1);
			// } catch (InterruptedException e) {
			// 	System.out.println("There was an issue trying to put the thread to sleep");
			// }

			

			// Randomly decide to if passenger arrives at airport
			if(rand.nextInt(1000) < 500 && !allPassengersQueued) {
				try{
					Passenger passenger = getRandomToCheckIn();
					queue.joinQueue(passenger);
					
					// TODO notify queue we have a passenger!
					// TODO log passenger has arrived at airport
					// System.out.println("Adding " + passenger.getBookingRefCode() + " to Queue at time >> "+simClock.getTimeString()+ ", "+ ++passengersAdded + " added.");
					log.writeEvent("Adding " + passenger.getBookingRefCode() + " to Queue at time >> "+simClock.getTimeString()+ ", "+ ++passengersAdded + " added.");
				}
				catch(NullPointerException e){
					
				}
			}
		}
		
		// TODO log simulation has ended
		// System.out.println("Simulation complete: " + passengersAdded + " added.");
		log.writeEvent("Simulation complete: " + passengersAdded + " added.");
		desk.open = false;
		// TODO unsure if this is correct way of doing things, will need to discuss
		
		queue.close();
		desk.interrupt();
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
	
	
}
