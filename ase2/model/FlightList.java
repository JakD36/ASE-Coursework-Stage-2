package ase2.model;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Scanner;

import ase2.simulation.Clock;

/**
 * Handles a list of Flights.
 * 
 * Implements the Singleton pattern.
 */
public class FlightList {
	//the instance
	private static FlightList instance;
	
	HashMap<String, Flight> flights;
	
	long lastDeparture;
	
	//Flights that have not departed
	ArrayList<Flight> activeFlights;
	
	/**
	 * Loads the list of Flights.
	 */
	private FlightList() {
		loadFlights();
	}
	
	/**
	 * Returns the instance of FlightList, or, if there is no
	 * instance, instantiates one and returns it. The method
	 * only blocks and enters the synchronized block if no
	 * instance exists upon entry.
	 * @return
	 */
	public static FlightList getInstance() {
		//check if there's an instance before entering the
		//synchronized block
		if (instance == null) {
			//use class static lock
			synchronized(FlightList.class) {
				//test must be performed again in case instance
				//was created whilst the thread was waiting
				if (instance == null) {
					instance = new FlightList();
				}
			}
		}
		return instance;
	}
	
	/**
	 * Loads the flights from the comma separated txt file.
	 * Parses each line of the text file as a different flight, and adds them to the collection of flights.
	 */
	private synchronized void loadFlights() {
		File f = new File("flight.txt");
		Scanner scanner;
		
		//instantiate Flight HashMap
		flights = new HashMap<String, Flight>();
		
		//instaniated Flights not departed
		activeFlights = new ArrayList<Flight>();
		
		lastDeparture = 0;
		
		//added try catch
		try {
			scanner = new Scanner(f);
			while (scanner.hasNextLine()) {     
				String inputLine = scanner.nextLine();   //do something with this line     
				String parts[] = inputLine.split(",");
				
				String []depTime = parts[7].split(":");
				// (hours * 3600) -> hours converted in secs - (mins * 60) -> mins converted in secs.
				//their sum is multiplied by 1000 to convert them in millsecs.
				long departureTime = (Integer.parseInt(depTime[0]) * 3600l + Integer.parseInt(depTime[1]) * 60l) * 1000l;
				
				//check if this is the last departure
				if(departureTime > lastDeparture) {
					lastDeparture = departureTime;
				}
				
				Flight currentFlight = new Flight(parts[0],
						parts[1],
						parts[2],
						Integer.parseInt(parts[3]),
						Float.parseFloat(parts[4]),
						Float.parseFloat(parts[5]),
						Float.parseFloat(parts[6]),
						departureTime);

				//add to all Flights
				flights.put(parts[0], currentFlight);
				//add to Flights not departed
				activeFlights.add(currentFlight);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	
	/**
	 * Check if Flights have departed and notify their display if they have
	 */
	public void checkFlightDepartures() {
		//the for loop must be broken to remove an item, so the outer loop is
		//is needed to reenter it when it is broken
		boolean loop = true;
		while(loop) {
			loop = false;
			for(Flight flight : activeFlights) {
				if(flight.hasDeparted(Clock.getInstance().getCurrentTime())) {
					activeFlights.remove(flight);
					//notify Flight view of end
					flight.notifyObservers();					
					//restart loop as it will be broken
					loop = true;
					//avoid concurrent modification exception
					break;
				}
			}
		}
	}
	
	
	public long getLastDepartureTime() {
		return lastDeparture;
	}
	
	/**
	 * Returns a Flight matching the given code
	 * @param flightCode the code of the Flight to get
	 * @return the requested Flight
	 */
	public synchronized Flight get(String flightCode) {
		return flights.get(flightCode);
	}
	
	/**
	 * Returns all the values in the handled HashMap
	 * @return the values stored in the HashMap
	 */
	public synchronized Collection<Flight> getValues() {
		return flights.values();
	}
	
	public static synchronized void reset() {
		instance = new FlightList();
	}
}
