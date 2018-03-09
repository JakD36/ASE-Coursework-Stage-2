package ase2.simulation;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

import ase2.model.CheckInHandler;
import ase2.model.Passenger;
import ase2.model.PassengerList;

public class Simulation {
	long startTime;
	long elapsed = 0;
	int passengersAdded = 0;
	ArrayList<CheckInHandler> desks;
	boolean allPassengersQueued = false;
	PassengerList passengers = PassengerList.getInstance();
	ArrayList<Passenger> passengersNotQueued;
	
	//Collections handout states, "please don’t use any of the Queue
	//implementations which handle concurrent access in the coursework, such as
	//ConcurrentLinkedQueue, since this would not help you to understand 
	//the basic principles and problems of using threads."
	//Therefore, a non-thread safe Queue has been used so that thread handling
	//techniques can be demonstrated. LinkedList implements the Queue interface.
	LinkedList<Passenger> queue;
	
	public static void main(String[] args) {
		new Simulation();
	}
	
	/**
	 * Currently runs for a set period of time. Eventually this should be
	 * changed to close when all desks close or all passengers are checked in
	 */
	public Simulation() {
		startTime = System.currentTimeMillis();
		System.out.println("Simulation Instiated: " + startTime);
		Random rand = new Random();
		
		//populate not queued passengersNotQueued ArrayList
		passengersNotQueued = new ArrayList<Passenger>();
		for(Passenger p : passengers.getNotCheckedIn().values()) {
			passengersNotQueued.add(p);
		}
		
		//create queue
		queue = new LinkedList<Passenger>();
				
		while(elapsed < 15000) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			elapsed =  System.currentTimeMillis() - startTime;
			//0.1% chance of passenger being added each loop
			//only enter if there's passengers left unqueued
			if(rand.nextInt(1000) == 1
					&& allPassengersQueued == false) {
				Passenger passenger = getRandomToCheckIn();
				
				System.out.println("Adding " + passenger.getBookingRefCode() + " to Queue");
				System.out.println(++passengersAdded + " added.");
				
				queue.add(passenger);				
			}
		}
		
		System.out.println("Simulation complete: " + passengersAdded + " added.");
		
		//test Passengers were successfully added
		popAndPrintQueue();
		
		System.out.println(passengersNotQueued.size() + " did not join queue.");
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
			Passenger passenger = passengersNotQueued.get(randInt);
			passengersNotQueued.remove(randInt);
			
			//check if Passenger was the last one
			if(passengersNotQueued.size() == 0)
				allPassengersQueued = true;
			
			return (passenger);
		}
		return null;
	}
	
	/**
	 * Temporary method to empty queue and test that Passengers have
	 * been added correctly.
	 */
	public void popAndPrintQueue() {
		while(!queue.isEmpty()) {
			System.out.println("Popping: " + queue.remove().getBookingRefCode());
		}
	}
}
