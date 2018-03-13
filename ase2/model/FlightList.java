package ase2.model;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Handles a list of Flights.
 * 
 * Implements the Singleton pattern.
 */
public class FlightList {
	//the instance
	private static FlightList instance;
	
	HashMap<String, Flight> flights;
	
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
		
		//added try catch
		try {
			scanner = new Scanner(f);
			while (scanner.hasNextLine()) {     
				String inputLine = scanner.nextLine();   //do something with this line     
				String parts[] = inputLine.split(",");
				
				Flight currentFlight = new Flight(parts[0],
						parts[1],
						parts[2],
						Integer.parseInt(parts[3]),
						Float.parseFloat(parts[4]),
						Float.parseFloat(parts[5]),
						Float.parseFloat(parts[6]));

				flights.put(parts[0], currentFlight);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
}
