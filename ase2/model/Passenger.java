package ase2.model;

import ase2.exceptions.IllegalReferenceCodeException;

public class Passenger {
	private String bookingRefCode;
	private String firstName;
	private String lastName;
	private Flight flight;
	
	// New stuff 
	private float[] baggageDimensions = {0,0,0};
	private float baggageWeight = 0;

	public Passenger(String bookingRefCode, String firstName, String lastName, Flight flight) 
		throws IllegalReferenceCodeException {
		//validate booking ref code
		if(!bookingRefCode.matches("[a-z]{3}[0-9]{4}")) {
			//if it fails throw an exception
			throw new IllegalReferenceCodeException
			("Illegal booking reference passed to constructor: " + bookingRefCode);
		}
			
		this.bookingRefCode = bookingRefCode;
		this.firstName = firstName;
		this.lastName = lastName;
		this.flight = flight;
		
	}

	public Passenger(String bookingRefCode, String firstName, String lastName, Flight flight,float[] baggageDimensions, float baggageWeight) 
		throws IllegalReferenceCodeException {
		//validate booking ref code
		if(!bookingRefCode.matches("[a-z]{3}[0-9]{4}")) {
			//if it fails throw an exception
			throw new IllegalReferenceCodeException
			("Illegal booking reference passed to constructor: " + bookingRefCode);
		}
			
		this.bookingRefCode = bookingRefCode;
		this.firstName = firstName;
		this.lastName = lastName;
		this.flight = flight;
		
		this.baggageDimensions = baggageDimensions;
		this.baggageWeight = baggageWeight;
	}


	public synchronized String getBookingRefCode() {
		return this.bookingRefCode;
	}
	
	public synchronized String getFirstName() {
		return this.firstName;
	}
	
	public synchronized String getLastName() {
		return this.lastName;
	}
	
	public synchronized Flight getFlight() {
		return this.flight;
	}

	public synchronized float[] getBaggageDimensions(){
		return this.baggageDimensions;
	}

	public synchronized float getBaggageWeight(){
		return this.baggageWeight;
	}
	
	public synchronized boolean equals(Object obj)
	{
		return (obj instanceof Passenger) && (((Passenger)obj).getBookingRefCode().toUpperCase().equals(this.getBookingRefCode().toUpperCase()));
	}

	public synchronized int compareTo(Passenger passenger)
	{
		return this.getBookingRefCode().toUpperCase().compareTo(passenger.getBookingRefCode().toUpperCase());
	}

	public synchronized void setBaggageDimensions(float[] baggageDimensions){
		this.baggageDimensions = baggageDimensions;
	}

	public synchronized void setBaggageWeight(float baggageWeight){
		this.baggageWeight = baggageWeight;
	}

}
