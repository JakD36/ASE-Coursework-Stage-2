package ase2.exceptions;
/**
 * Exception to be thrown when there is an issue with the booking reference for a passenger.
 * Be it either a duplicate booking reference or a booking reference that does not exist in the keys of a map.
 */
public class IllegalReferenceCodeException extends RuntimeException{

	//eclipse seems to want this line
	private static final long serialVersionUID = 1L;

	/**
     * Constructor for IllegalReferenceCodeException, when there is an issue with the booking reference code.
     * Used for issues with duplicate reference codes or provided reference codes not being present.
     * 
     * @param   message more specific information on the error, provided by the method throwing the error.
     */
    public IllegalReferenceCodeException(String message){
        super("Error: " + message);
    }
}
