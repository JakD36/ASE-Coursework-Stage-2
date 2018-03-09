package ase2.model;

import ase2.exceptions.IllegalReferenceCodeException;

public class Passenger {
	private String bookingRefCode;
	private String firstName;
	private String lastName;
	private Flight flight;

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
	
	public synchronized boolean equals(Object obj)
	{
		return (obj instanceof Passenger) && (((Passenger)obj).getBookingRefCode().toUpperCase().equals(this.getBookingRefCode().toUpperCase()));
	}

	public synchronized int compareTo(Passenger passenger)
	{
		return this.getBookingRefCode().toUpperCase().compareTo(passenger.getBookingRefCode().toUpperCase());
	}
}
