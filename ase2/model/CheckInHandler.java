package ase2.model;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import ase2.QueueHandler;
import ase2.exceptions.DepartedFlightException;
import ase2.exceptions.IllegalReferenceCodeException;
import ase2.interfaces.Observer;
import ase2.interfaces.Subject;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Random;

import ase2.simulation.Clock;
import ase2.simulation.Logging;


public class CheckInHandler extends Thread implements Subject {
	public boolean open = true;
	private QueueHandler queue;
	private PassengerList passengers;
	private FlightList flights;
	long processTime = 10*Clock.SECONDSINMINUTE*Clock.MSINSECOND; // time in ms to 5 minutes
	final long numberOfCheckInStages = 3; // there are 3 stages to check in, check details, proces passenger, show results so need to split process time between these
	volatile String status = "started<br/>";
	long ClosureTime = FlightList.getInstance().getLastDepartureTime();
	Clock simClock;
	double totalFees;
	int queueId;
	volatile Passenger currentPassenger = null;

	ArrayList<Observer> observers = new ArrayList<Observer>();
	
	/**
	 * Constructor for the CheckInHandler.
	 * Instantiates a {@link PassengerList} object, and populates the collections of passengers and flights
	 * using the loadFlights and loadPassengers methods.
	 */
	public CheckInHandler(QueueHandler queue, int queueId) {
		this.queueId = queueId;
		//get the instance of FlightList
		flights = FlightList.getInstance();
		passengers = PassengerList.getInstance();
		this.queue = queue;
		simClock = Clock.getInstance();
	}

	/**
	 * Gets the time the desk closes.
	 * 
	 * @return the time in ms in simulation time that the desk should close.
	 */
	public synchronized long getClosureTime(){
		return ClosureTime;
	}

	/**
	 * Overrides the run method of Thread, This is the main loop for the check in desk.
	 */
	public void run(){
		//tweaked so GUI can update that the desk is closed
		while(simClock.getCurrentTime()<ClosureTime // While time hasnt passed the desks closure time and there are still passengers left to enter the system
				&& PassengerList.getInstance().getNotCheckedIn().size() > 0){// ){

			Logging log = Logging.getInstance();
			
			Passenger nextPassenger=null;
			
			try{
				nextPassenger = queue.removeNextPassenger(queueId); // get the next passenger from the queue 
				if(nextPassenger != null) {
					if(checkDetails(nextPassenger.getBookingRefCode(), nextPassenger.getLastName())){ // check the details of the passenger if they match move on to next stage
						

						// Assign random baggage dimensions to the passenger and baggage weight
						Random rand = new Random();
						nextPassenger.setBaggageWeight(rand.nextFloat()*40);
						float [] baggageDimensions = {
								rand.nextFloat()*70, rand.nextFloat()*60, rand.nextFloat()*40 // TODO correct units if not doen already
						};
						nextPassenger.setBaggageDimensions(baggageDimensions);
						
						// process the passenger, adds them to their flight and calculates any fees						
						float fee = processPassenger(nextPassenger.getBookingRefCode(), 
						nextPassenger.getBaggageDimensions(), nextPassenger.getBaggageWeight());
						

						// logs the event
						log.writeEvent(nextPassenger.getFirstName()+" "+nextPassenger.getLastName()+" checked in successfully to flight "+nextPassenger.getFlight().getFlightCode()+" and is charged £"+fee+" at time "+simClock.getTimeString());
						
						// Sets the status of the desk for use in the GUI display
						setStatus("checked in successfully and is charged:<br/> &pound;"+ String.format("%.2f", fee));	
						totalFees += fee;		// adds to the total fees the desk has collected
						
						notifyObservers(); // Notify the desks observers the status has changed
						
						// Calculate the time in the simulation the desk should be done with this stage of the check in process by
						long sleepTil = simClock.getCurrentTime()+processTime/numberOfCheckInStages;
						threadSleep(sleepTil); // put the thread to sleep until this time has passed
					}
					else{
						log.writeEvent(nextPassenger.getFirstName()+" "+nextPassenger.getLastName()+" checked in failed at time" + simClock.getTimeString());
						setStatus(nextPassenger.getFirstName()+" "+nextPassenger.getLastName()+" checked in failed");
					}
				}
				
			}catch(NoSuchElementException e){
				setStatus("Theres no one in the queue to process!");
				notifyObservers();
				// System.out.println("Theres no one in the queue to process!");
			}catch(DepartedFlightException dfe) {
				log.writeEvent(String.format("Passenger %s %s has arrived too late", nextPassenger.getFirstName(),nextPassenger.getLastName()));
				setStatus(String.format("Passenger %s %s has arrived too late", nextPassenger.getFirstName(),nextPassenger.getLastName()));
			}
			// setStatus("Waiting for next customer!");
			// notifyObservers();
		}

		setStatus("closed.<br/>Collected: "+String.format("&pound;%.2f", totalFees));
		notifyObservers();
	}
	


	/**
	 * Puts the thread to sleep until the simulation time has passed sleepTil.
	 * Uses the difference in sleepTil and the current simulation time 
	 * divided by the simulations current speed to get the actual time to put the thread to sleep.
	 * If the speed is changed this thread is interupted, if the sim hasnt passed sleep til then the thread is put back to sleep calling threadSleep again.
	 *	
	 * 
	 * @param the simulation time in milliseconds that the thread should sleep until
	 */
	synchronized private void threadSleep(long sleepTil){
		Clock myClock = Clock.getInstance();
		long sleepTime = (sleepTil-myClock.getCurrentTime())/simClock.getSpeed();
		try { 
			Thread.sleep(sleepTime);
		} catch (InterruptedException e) {
			if(myClock.getCurrentTime()<sleepTil){
				threadSleep(sleepTil);
			}
		}

	}


	/**
	 * Checks the details of the passenger who wants to check in.
	 * Checks the passengers name to the provided booking reference. As well as checking to make sure that
	 * the booking reference hasn't already checked in, and that it is on on record.
	 * 
	 * @param	boookingReference	The booking reference code of the passenger
	 * @param	lastName			Last name of the passenger, to check against that associated with booking reference
	 * @return	A boolean to show if the last name provided matches that of passenger with booking reference
	 * @throws	IllegalReferenceCodeException	If the booking reference does match a passenger that is to be checked in or any passenger on the system.
	 */
	public synchronized boolean checkDetails(String bookingReference, String lastName) throws IllegalReferenceCodeException{
		setStatus("checking details of " + bookingReference + "<br/>");
		notifyObservers();
		Clock simClock = Clock.getInstance();
		// put the desk to sleep to take into account hte time to process passenger
		Logging log = Logging.getInstance();
		

		// Put the thread to sleep to take into account the time to process the stage of check in
		long sleepTil = simClock.getCurrentTime()+processTime/numberOfCheckInStages;
		threadSleep(sleepTil);

		if( passengers.getNotCheckedIn().containsKey(bookingReference) ){	// Check that the booking reference provided matches, a passenger to be checked in
			//Strings should compared with .equals in Java
			
			Flight flight = passengers.getNotCheckedIn().get(bookingReference).getFlight();
			
			if (flight.hasDeparted(simClock.getCurrentTime())) {
				throw new DepartedFlightException(flight);
			}
			
			if(passengers.getNotCheckedIn().get(bookingReference).getLastName().equals(lastName)){	// Checks if the passenger 
				return true; // Return true to show that the details match with a passenger to be checked in.
			}
			else{
				return false; // return false if they do not match,
			}
		}
		else if(passengers.getCheckedIn().containsKey(bookingReference)){ // Throw an exception if the matching passenger is already checked in
			setStatus(bookingReference + "Is already checked in!<br/><br/>");
			notifyObservers();
			throw new IllegalReferenceCodeException(bookingReference+": Is already checked in.");
			
		}
		else{ // Throw an exception if there is no passenger that matches this booking reference code
			setStatus(bookingReference+": There is no booking reference on record.");
			notifyObservers();
			throw new IllegalReferenceCodeException(bookingReference+": There is no booking reference on record.");
			
		}
		
	}

	/**
	 * Processes the passenger that wishes to be checked in.
	 * Calculates the fees to be payed based on the dimensions and weight of the baggage.
	 * Assigns the passenger as checked in, before adding to the number of passengers as well as the weight, and volume of the baggage on the flight.
	 * 
	 * @param	bookingReference	The booking reference of the passenger to be processed
	 * @param	dimensions			The width, height and depth of the baggage in a single array.
	 * @param	weight				The weight of the passengers baggage.
	 * @return 	The fee due from the passenger for any excesses on the baggage, -1 represents there was an error with check in
	 * @throws	IllegalReferenceCodeException	If there is no passenger with a matching booking reference code.
	 */
	public synchronized float processPassenger(String bookingReference, float[] dimensions, float weight) throws IllegalReferenceCodeException{
		setStatus("processing passenger " + bookingReference + "<br/>");
		notifyObservers();
		Clock simClock = Clock.getInstance();
		// put the desk to sleep to take into account hte time to process passenger
		Logging log = Logging.getInstance();
		

		// Put the thread to sleep to take into account the time to process the stage of check in
		long sleepTil = simClock.getCurrentTime()+processTime/numberOfCheckInStages;
		threadSleep(sleepTil);
		
		float fee;	// Fee due from passenger, calculated from the weight Fee, volume fee and the multiplier for the passengers flight
		float weightFee = 0f, volFee = 0f;
		float multiplier = 1f;

		float vol = dimensions[0]*dimensions[1]*dimensions[2]; // Calculate the volume of the baggage
		
		// Cant have a negative value for a scalar like weight or volume, so it is assumed for now that this was a mistake
		// So the value will be taken, could throw an exception and request user to try again
		
		if(vol<0) {vol*=-1;}
		if(weight<0) {weight*=-1;}
		
		
		Flight flight = passengers.get(bookingReference).getFlight(); // Get the information on the flight the passenger is going on
		multiplier = flight.getFeeMultiplier();
		// Find the maximum baggage allowances for each passenger on that flight, 
		// take into account some passengers may go beyond the limit!

		float factorOfSafety = 0.8f;
		float maxWeight = (float) (( flight.getMaxBaggageWeight() / flight.getPassengerCapacity() )*factorOfSafety);
		float maxVol = (float) (( flight.getMaxBaggageVolume() / flight.getPassengerCapacity() )*factorOfSafety);
		
		// basic calculation to find the fees from the excess
		weightFee = weight-maxWeight;
		volFee = vol-maxVol;
		
		// If the passengers baggage is below the maximums, no fee is applied,
		if (weightFee<0){ weightFee = 0; }
		if (volFee<0){ volFee = 0; }
		
		fee = (weightFee+volFee)*multiplier;
		
		if(passengers.checkInPassenger(bookingReference)){ // Attempt to check in the passenger
			flight.addPassengerAndBaggage(vol,weight,fee);	// If they are checked in add baggage, and increment number of passengers
		}
		else{
			// If for some reason the passenger cannot be checked in, we need to return an error
			setStatus("There is no passenger with this booking reference to be checked in,\n they may be already checked in: "+bookingReference);
			notifyObservers();
			throw new IllegalReferenceCodeException
			("There is no passenger with this booking reference to be checked in,\n they may be already checked in: "+bookingReference);
			
		}

		// Output the final fee due from the passenger,
		
		return fee;
	}
	
	/**
	 * Pass up the number of passengers still to be checked in.
	 * Asks {@link PassengerList}, to get the number of passengers who are still to be checked in.
	 * 
	 * @return The number of passengers still to be checked in.
	 */
	public synchronized int getNumToCheckIn() {
		return passengers.getNumToCheckIn();
	}
	
	/**
	 * Takes the reports from each individual flight and compiles them into one output.
	 * Loops through all the flights and gets their reports to be displayed by the GUI.
	 * 
	 * @return The compilation of all the reports from all the flights. separated by a two new lines
	 */
	public synchronized String generateReports() {
		// Follow this pattern
		//		Flight code: #
		//		Number of Passengers: #
		//		Total Baggage Weight: #
		//		Total Baggage Volume: #
		//		Total Excess Fees: #
		//		Capacity Exceeded: yes/no
		String finalReport = "";
		for(Flight f: flights.getValues()){
			finalReport += f.generateReport()+"\n";
		}
		
		//write to file
		try {
			PrintWriter out = new PrintWriter("report.txt");
			out.print(finalReport);
			out.close();
		} catch (FileNotFoundException e) {
		}
		
		//return for GUI
		return finalReport;
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
	
	/**
	 * Get the current status.
	 * 
	 * String is immutable, so this doesn't need synchronised.
	 * 
	 * @return the current status
	 */
	public String getStatus() {
		return status;
	}
	/**
	 * Sets the status of this desk
	 * 
	 * @param the string with information on the status of the desk.
	 */
	public synchronized void setStatus(String status) {
		this.status = status;
	}
}
