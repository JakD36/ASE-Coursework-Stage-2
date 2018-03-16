package ase2.interfaces;

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
}
