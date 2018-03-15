package ase2.interfaces;

/**
 * Specifies methods that must be implemented by all
 * class which implement Observer.
 * 
 * Observers may subscribe to Subjects for update.
 */
public interface Observer {
	//TODO: add (at least) Subject params
	public void update();
}
