package ase2.simulation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Logging 
{
	//List of recorded events
	private ArrayList<String> events;
	
	//Events counter
	private int counter = 1;

	private boolean debug = false;
	
	//Constant keeping the filename for the log file
	static private final String FILENAME = "logging.txt";
	
	//Static variable keeping the singleton instance of the class
	static private Logging instance;
	
	/*
	 * Creates a new instance of the class. It's private to allow the Singleton D.P.
	 */
	private Logging() {
		this.events = new ArrayList<String>();
	}
	
	/*
	 * Adds a new event in the list
	 * 
	 * event: Event to append
	 */
	public void writeEvent(String event)
	{
		synchronized(this.events) {
			events.add(String.format("Event #%d: %s"+ System.lineSeparator() , this.counter,event));
			this.counter++;
			if(debug){
				System.out.println(String.format("Event #%d: %s"+ System.lineSeparator() , this.counter,event));
			}
		}
	}

	public void enableDebug(){
		debug = true;
	}
	
	/*
	 * Returns the current number of recorded events
	 * 
	 * Return: number of the current events
	 */
	public int getNumberOfEvents()
	{
		return events.size();
	}
	
	/*
	 * Writes all the events in the logging file
	 */
	public void flush() throws IOException 
	{
		StringBuilder event_str = new StringBuilder();
		
		synchronized(this.events) 
		{
			if (!this.events.isEmpty())
			{
				for (String e : this.events) {
					event_str.append(e);
				}
				
				File logFile = new File(Logging.FILENAME);
		
				BufferedWriter writer = new BufferedWriter(new FileWriter(logFile));
			    writer.write(event_str.toString());
			    writer.close();
			}
		    this.counter = 1;
		    this.events.clear();
		}
	
	}
	
	
	/*
	 * Returns the Singleton instance of the Logging class
	 */
    static public Logging getInstance()
	{
		if (Logging.instance == null)
		{
            synchronized(Logging.class)
            {
                if (Logging.instance == null)
                {
                    Logging.instance = new Logging();
                }
            }
		}
		
		return Logging.instance;
	}
}
