package ase2.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

import ase2.exceptions.IllegalReferenceCodeException;

public class PassengerList {
	private HashMap<String,Passenger> passengersCheckedIn;
	private HashMap<String,Passenger> passengersNotCheckedIn;
	ArrayList<Passenger> passengersNotQueued;
	boolean allPassengersQueued = false;
	
	//the instance
	private static PassengerList instance;
		
	/**
	 * Returns the instance of PassengerList, or, if there is no
	 * instance, instantiates one and returns it. The method
	 * only blocks and enters the synchronized block if no
	 * instance exists upon entry.
	 * @return
	 */
	public static PassengerList getInstance() {
		//check if there's an instance before entering the
		//synchronized block
		if (instance == null) {
			//use class static lock
			synchronized(PassengerList.class) {
				//test must be performed again in case instance
				//was created whilst the thread was waiting
				if (instance == null) {
					instance = new PassengerList();
				}
			}
		}
		
		return instance;
	}
	
	
	/**
	 * Constructs a new PassengerList and invoke loadPassengers();
	 * 
	 * FlightList reference required for getting references to
	 * relevant Flights.
	 * 
	 * @param flights
	 */
	private PassengerList() throws IllegalReferenceCodeException  {
		//instantiate HashMaps
		passengersCheckedIn = new HashMap<String,Passenger>();
		passengersNotCheckedIn =new  HashMap<String,Passenger>();
		try{
			loadPassengers();
		}catch(IllegalReferenceCodeException e){
			System.out.println(e.getMessage());
		}
		
		// Collect passengers to be added into to system
		passengersNotQueued = new ArrayList<Passenger>();
		for(Passenger p : passengersNotCheckedIn.values()) {
			passengersNotQueued.add(p);
		}
		//totalPassengers = passengersNotQueued.size();
	}
	
	/**
	 * Loads the passengers from the comma separated txt file.
	 * REQUIRES Load flights to already exist
	 * Parses each line of the text file as a different passenger, and adds them to the collection of passengers.
	 */
	public void loadPassengers() throws IllegalReferenceCodeException  {
		File f = new File("passengers.txt");
		Scanner scanner;
		
		//get the instance of FlightList
		FlightList flights = FlightList.getInstance();
		
		try {
			scanner = new Scanner(f);
			ArrayList<String> duplicates = new ArrayList<String>();
			while (scanner.hasNextLine()) {     
				String inputLine = scanner.nextLine();   
				String parts[] = inputLine.split(",");
				
				Flight flight = flights.get(parts[3]);// Use flight code to link to the flight object
				flight.incrementPassengersBookedAboard();

				if(!this.add(new Passenger(
					parts[0], // Booking reference code
					parts[1], // First name
					parts[2], // Last name
					flight),	
					Boolean.parseBoolean(parts[4]))){ // Whether the passenger is already checked in
						duplicates.add(parts[0]);
				}
			}
			
			if(duplicates.size()>0){
				throw new IllegalReferenceCodeException("Duplicate ids were found in input:"+duplicates);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Retrieves the {@link Passenger} with the matching booking reference code.
	 * Checks the two hash maps, passengersCheckedIn and passengersNotCheckedIn for the passenger with matching booking reference code. 
	 * Starting with the checked in map, if that doesn't work moves to the notCheckedIn map.
	 * 
	 * @param	bookingRefCode	the booking reference code for the passenger wanted.
	 * @return	The passenger with the matching booking reference code.
	 * @throws	IllegalReferenceCodeException	If there is no passenger with a matching booking reference code in either of the two maps.
	 */
	public Passenger get(String bookingRefCode) throws IllegalReferenceCodeException {
		Passenger output;
		if((output = passengersCheckedIn.get(bookingRefCode)) != null){
			return output;
		}else if((output = passengersNotCheckedIn.get(bookingRefCode)) != null){
			return output;
		}else{
			throw new IllegalReferenceCodeException
			("There is no passenger with this reference code: "+bookingRefCode);
		}
	}


	//
	public HashMap<String,Passenger> getNotCheckedIn(){
		return passengersNotCheckedIn;
	}

	public HashMap<String,Passenger> getCheckedIn(){
		return passengersCheckedIn;
	}

	/**
	 * Adds a passenger to the collection.
	 * Adds the passenger to the correct hash map based on if they haved been checked in or not.
	 * Checks to make sure there isn't already a passenger with the same booking reference code.
	 * 
	 * @param	thePassenger	The passenger that is to be added to the collection
	 * @param	checkedIn		Whether the passenger has been checked in yet or not.
	 * @return	boolean to say if the passenger was added to the list successfully.
	 */
	public boolean add(Passenger thePassenger, boolean checkedIn) {
		// Cant have two passengers with the same booking reference code, first need to check both hashmaps
		if(passengersCheckedIn.containsKey(thePassenger.getBookingRefCode()) 
		|| passengersNotCheckedIn.containsKey(thePassenger.getBookingRefCode())){
			// As one of the lists contains a passenger with the same key, we return false to show that it cannot be added.
			return false;
		}
		// if the method hasn't been stopped from the return above, then we can add the passenger to the right hashmap
		if(checkedIn){
			passengersCheckedIn.put(thePassenger.getBookingRefCode(),thePassenger);
			thePassenger.getFlight().addPassengerAndBaggage(0,0,0); // Add to the number of passengers on this flight, there is no information on the baggage for these passengers
		}
		else{
			passengersNotCheckedIn.put(thePassenger.getBookingRefCode(),thePassenger);
		}
		// With the passenger added to the collection we can return true to show operation was successful.
		return true;
	}
	
	/**
	 * Remove the passenger with the matching booking reference code from the collection.
	 * Checks both hashmaps to see if they contain a matching booking reference code, 
	 * if one does removes the passenger from the collection.
	 * 
	 * @param	bookingRefCode	The booking reference of the passenger that is to be removed
	 * @return	A boolean to say if a passenger was removed or if there is no matching passenger with this booking reference code.
	 */
	public boolean remove(String bookingRefCode) { 
		if(passengersCheckedIn.containsKey(bookingRefCode)){ 		// If the checkIn map contains the booking reference
			passengersCheckedIn.remove(bookingRefCode); 			// remove from the hash map
			return true;											// And return true to show that this was successful
		}else if(passengersCheckedIn.containsKey(bookingRefCode)){	// Same as above
			passengersNotCheckedIn.remove(bookingRefCode);
			return true;
		}else{
			return false;											// If neither list contains the booking reference, return false to show remove was unsuccessful
		}
		
	}
	
	/**
	 * Retrieve the number of passengers that are left to be checked in.
	 * 
	 * @return	The number of passengers left to be checked in.
	 */
	public int getNumToCheckIn() {
		return passengersNotCheckedIn.size();
	}
	
	/**
	 * Process the passenger as being checked in.
	 * This moves them from the not checked in hash map to the checked in HashMap.
	 * 
	 * @param	bookingRefCode	The booking reference of the passenger to be checked in.
	 * @return	A boolean to show if the passenger was moved successfully, returns false when there is no matching bookingRefCode, in the collection for passengers to be checked in.
	 */
	public boolean checkInPassenger(String bookingRefCode){
		if(passengersNotCheckedIn.containsKey(bookingRefCode)){
			passengersCheckedIn.put(bookingRefCode,passengersNotCheckedIn.get(bookingRefCode));
			passengersNotCheckedIn.remove(bookingRefCode);
			return true;
		}
		else{
			return false;
		}
	}
	
	/**
	 * Returns a random Passenger who has not checked in
	 * or no null if all Passengers are checked in
	 * @return a random Passenger who has not checked in or null
	 * if all have checked in
	 */
	public Passenger getRandomToCheckIn() {
		if(passengersNotQueued.size() > 0) {
			Random rand = new Random();
			int randInt = rand.nextInt(passengersNotQueued.size());
			Passenger passenger = passengersNotQueued.remove(randInt);
			
			//check if Passenger was the last one
			if(passengersNotQueued.size() == 0)
				allPassengersQueued = true;
			
			return (passenger);
		}
		return null;
	}
	
	public int getNoNotQueued() {
		return passengersNotQueued.size();
	}
	
	public ArrayList<Passenger> getPassengersNotQueued() {
		return passengersNotQueued;
	}
	
	public int getTotalPassengers() {
		return passengersNotCheckedIn.size() + passengersCheckedIn.size();
	}
	
	public static synchronized void reset() {
		instance = new PassengerList();
	}
}
