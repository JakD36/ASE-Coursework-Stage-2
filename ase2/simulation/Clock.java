package ase2.simulation;

public class Clock 
{
    
    long lastRealTime; // Time at last call
    long lastSimTime;
    long startSimTime = 6*HOUR*SECOND; // Start the simulation at 6 am
    volatile long speed = 250; //may be updated across threads
    boolean started = false;
    
    //ms in a second
    public static final long SECOND = 1000;
    //seconds in an hour
    public static final long HOUR = 3600;
    //seconds in am minute
    public static final long MINUTE = 60;

    //Static variable keeping the singleton instance of the class
	static private Clock instance;
	/*
	 * Creates a new instance of the class. It's private to allow the Singleton D.P.
	 */
	private Clock() {
    }
    
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
    
    public synchronized void resetClock() {
    	lastRealTime = System.currentTimeMillis();
        lastSimTime = startSimTime;
        started = true;
    }
    
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
    public long getSpeed(){
        return speed;
    }

	public String getTimeString(){
        long simTime = getCurrentTime();
		long ms = simTime%SECOND;
        long s = (simTime / SECOND) % MINUTE ;
        long min = (simTime / (SECOND*MINUTE)) % MINUTE;
		long hour = (simTime / (SECOND*MINUTE*MINUTE)) % 24;
		return String.format("%02d:%02d:%02d:%03d", hour, min, s, ms);
	}
    
    public void setSpeed(long speed){
        getCurrentTime(); // calculates the simulation time for the old speed!
        // So now when we change the speed below its only applied for the future!
        this.speed = speed;
    }

    public synchronized long getStartTime(){
        return startSimTime;
    }

	/*
	 * 
	 */
	synchronized static public Clock getInstance(){ // use double locking
		if (Clock.instance == null){
			synchronized(Clock.class) { // lock block if (instance == null) // and re-check
                instance = new Clock(); 
            }
		}
		return Clock.instance;
	}
}