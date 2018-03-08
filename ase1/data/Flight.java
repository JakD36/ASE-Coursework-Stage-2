package ase1.data;
public class Flight {
	//flight description
	private String flightCode;
	private String destination;
	private String carrier;
	
	//limits per passengers
	private float maxBaggageVolume;
	private float maxBaggageWeight;
	private float feeMultiplier;
	
	//passengers/volume/weight up to date
	private int   currentTotalPassengers;
	private float currentTotalBaggageVolume;
	private float currentTotalBaggageWeight;
	private float currentTotalFees;
	
	//total limits in the aircraft
	private int passengerCapacity; 
	
	/**
	 * Constructs a Flight object
	 * @param flightCode the flight code
	 * @param destination the destination
	 * @param carrier the carrier
	 * @param passengerCapacity the passenger capacity
	 * @param maxVol the maximum volume of combined passenger luggage
	 * @param maxWeight the maximum weight of combined passenger luggage
	 * @param feeMultiplier used for calculating excess fees
	 */
	public Flight(String flightCode, String destination, String carrier,
			int passengerCapacity, float maxVol, float maxWeight,float feeMultiplier) {

		//assign the arguments to the class attributes
		this.flightCode = flightCode;
		this.destination = destination;
		this.carrier = carrier;
		this.passengerCapacity = passengerCapacity;
		this.maxBaggageVolume = maxVol;
		this.maxBaggageWeight = maxWeight;
		this.feeMultiplier = feeMultiplier;

		this.currentTotalPassengers = 0;
		this.currentTotalBaggageVolume = 0;
		this.currentTotalBaggageWeight = 0;
		this.currentTotalFees = 0;
	}
	
	/**
	 * returns the flight code
	 * @return the flight code
	 */
	public String getFlightCode() {
		return flightCode;
	}

	/**
	 * returns the destination
	 * @return the destination
	 */
	public String getDestination() {
		return destination;
	}

	/**
	 * returns the max baggage volume
	 * @return the max baggage volume
	 */
	public float getMaxBaggageVolume() {
		return maxBaggageVolume;
	}
	
	/**
	 * returns the max baggage weight
	 * @return the max baggage weight
	 */
	public float getMaxBaggageWeight() {
		return maxBaggageWeight;
	}

	/**
	 * returns the passenger capacity
	 * @return the passenger capacity
	 */
	public int getPassengerCapacity() {
		return passengerCapacity;
	}

	/**
	 * returns the carrier
	 * @return the carrier
	 */
	public String getCarrier() {
		return carrier;
	}

	/**
	 * returns the fee multiplier
	 * @return the fee multiplier
	 */
	public float getFeeMultiplier(){
		return feeMultiplier;
	}
	
	/**
	 * adds an item of baggage to the Flight
	 * @param vol the volume of the baggage
	 * @param weight the weight of the baggage
	 * @param fee any excess baggage fees incurred
	 */
	public void addPassengerAndBaggage(float vol, float weight,float fee) {
		//update weight and vol with this passenger's data
		this.currentTotalBaggageWeight+=weight;
		this.currentTotalBaggageVolume+=vol;
		this.currentTotalFees+=fee;
		
		//add a passenger to the current count
		this.currentTotalPassengers++;
	}
	
	/**
	 * Generates a summary of the Flight information as a String
	 * @return the report String
	 */
	public String generateReport()
	{
		StringBuilder report = new StringBuilder();

		String excess = "no";
		if( ( this.currentTotalBaggageVolume > this.maxBaggageVolume ) || ( this.currentTotalBaggageWeight > this.maxBaggageWeight )|| ( this.currentTotalPassengers > this.passengerCapacity ) ){
			excess = "yes";
		}

		report.append(String.format("Flight code: %s\n",this.flightCode));
		report.append(String.format("Number of Passengers: %d\n", this.currentTotalPassengers));
		report.append(String.format("Total Baggage Weight: %.2f\n", this.currentTotalBaggageWeight));
		report.append(String.format("Total Baggage Volume: %.2f\n", this.currentTotalBaggageVolume));
		report.append(String.format("Total Excess Fees: %.2f\n", this.currentTotalFees));
		report.append(String.format("Exceeded: %s\n",excess));
		

		return report.toString();
	}
	/**
	 * Compares if to Flights are equal
	 * @param the object with which to compare
	 * @return true if equal, false if unequal
	 */
	public boolean equals(Object obj)
	{
		//two flights are equal if their flight codes are equal  (case-insensitive)
		return (obj instanceof Flight) && (((Flight)obj).getFlightCode().toUpperCase().equals(this.getFlightCode().toUpperCase()));
	}
	
	/**
	 * Compares a Flight's code with another Flight's code
	 * @param flight the with which to compare
	 * @return the result of the String comparison
	 */
	public int compareTo(Flight flight)
	{
		//comparisons is done w.r.t. the flight code (case-insensitive)
		return this.getFlightCode().toUpperCase().compareTo(flight.getFlightCode().toUpperCase());
	}
}
