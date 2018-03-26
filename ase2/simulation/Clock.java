package ase2.simulation;

public class Clock 
{
    //ms in a second
    public static final long MS_IN_SECOND = 1000;
    //seconds in am minute
    public static final long SECONDS_IN_MINUTE = 60;
    public static final long MINUTES_IN_HOUR = 60;
    
    long lastRealTime; // Time at last call
    long lastSimTime;
    long startSimTime = 6*MINUTES_IN_HOUR*SECONDS_IN_MINUTE*MS_IN_SECOND; // Start the simulation at 6 am
    volatile long speed = 250; //may be updated across threads
    boolean started = false;
    
    //Static variable keeping the singleton instance of the class
	static private Clock instance;
    
    /*
	 * Default constructor.
	 */
	private Clock() {
    }
    
    /**
     * Starts the simulation clock.
     * 
     * Clock does not start when the first instance is called, instead the start clock method is called it checks to see if the clock has started.
     *
     * @return a boolean, true if the clock has just been started or false if started beforehand
     */
    public synchronized boolean startClock(){
        if(!started){
            lastRealTime = System.currentTimeMillis();
            lastSimTime = startSimTime;
            started = true;
            return true;
        }
        else{
            return false;
        }
    }

    /**
     * Restarts the clock, back to initial simulation start time
     */
    public synchronized void resetClock() {
    	lastRealTime = System.currentTimeMillis();
        lastSimTime = startSimTime;
        started = true;
    }
    
    /**
     * Gets the current time in the simulation, in milliseconds.
     * 
     * Each time this is called it takes the actual elapsed time since the last call to this function, and uses the speed of the simulation,
     * to get the time passed in the simulation. This is added to the simulation time at the last call to this functio to get the new simulation time.
     * 
     * @return The current simulation time in milliseconds
     */
    public long getCurrentTime(){
        if(started){
            long currentRealTime = System.currentTimeMillis();
            long elapsedRealTime = (currentRealTime - lastRealTime); // elapsed time from last call
        
            long currentSimTime = lastSimTime + speed*elapsedRealTime;
        
            lastSimTime = currentSimTime;
            lastRealTime = currentRealTime;
        
            return currentSimTime;
        }else{
            return startSimTime;
        }
    }

    /**
     * Gets the current speed of the simulation.
     * 
     * @return the current of the simulation at time of call.
     */
    public long getSpeed(){
        return speed;
    }


    /**
     * Gets the current time in the simulation as a string.
     * @return String of the current simulation time, formatted as hours:minutes:seconds:milliseconds.
     */
	public String getTimeString(){
        long simTime = getCurrentTime();
		long ms = simTime%MS_IN_SECOND;
        long s = (simTime / MS_IN_SECOND) % SECONDS_IN_MINUTE ;
        long min = (simTime / (MS_IN_SECOND*SECONDS_IN_MINUTE)) % MINUTES_IN_HOUR;
		long hour = (simTime / (MS_IN_SECOND*SECONDS_IN_MINUTE*MINUTES_IN_HOUR)) % 24;
		return String.format("%02d:%02d:%02d:%03d", hour, min, s, ms);
	}
    
    /**
     * Sets the speed of the simulation going forward.
     * 
     * Calls getCurrentTime(), so that the lastRealTime and lastSimTime can be updated to this function,
     * this way the change in speed only effects the simulation going forward.
     * As applying the speed to the total elapsed time of the simulation would lead to the simulation going back in time if the speed was lowered.
     * 
     * @param the rate of change of simulation time with respect to real time.
     */
    public void setSpeed(long speed){
        getCurrentTime(); // calculates the simulation time for the old speed!
        // So now when we change the speed below its only applied for the future!
        this.speed = speed;
    }

    /**
     * Get the start time of the simulation.
     * 
     * @return the start time of the simulation in milliseconds in simulation time.
     */
    public synchronized long getStartTime(){
        return startSimTime;
    }

	/** 
	 * Gets the instance of the clock, or creates a new instance if it does not already exist.
     * 
     * @return an instance of the clock.
	 */
	synchronized static public Clock getInstance(){ // use double locking
		if (Clock.instance == null){
            synchronized(Clock.class) { // lock block 
                if (instance == null) {
                    instance = new Clock();
                } 
            }
		}
		return Clock.instance;
	}
}