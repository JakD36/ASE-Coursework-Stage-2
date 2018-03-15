package ase2.interfaces;

import ase2.model.CheckInHandler;
import ase2.model.Flight;
import ase2.model.Passenger;
import ase2.model.PassengerList;

/**
 * Specifies methods that must be implemented by all
 * class which implement Subject.
 * 
 * Observers may subscribe to Subjects for updates.
 */
public interface Subject {
	public void registerObserver(Observer obs);
	public void removeObserver(Observer obs);
	public void notifyObservers();
	
	public Passenger[] getPassengersNotQueuedList();
	public Passenger[]  getQueuedPassengersList();
	public CheckInHandler[] getCheckInDesks();
	public Flight[] getFlights();
}
