package ase2.simulation;

public class Clock 
{
    
    long lastRealTime; // Time at last call
    long lastSimTime;
    long startSimTime = 6*3600*1000; // Start the simulation at 6 am
    long speed = 1000;
    boolean started = false;

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
    
    public synchronized long getCurrentTime(){
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
    public synchronized long getSpeed(){
        return speed;
    }

	public synchronized String getTimeString(){
        long simTime = getCurrentTime();
		long ms = simTime%1000;
        long s = (simTime / 1000) % 60 ;
        long min = (simTime / (1000*60)) % 60;
		long hour = (simTime / (1000*60*60)) % 24;
		return hour+":"+min+":"+s+":"+ms;
	}
    
    public synchronized void setSpeed(long speed){
        getCurrentTime(); // calculates the simulation time for the old speed!
        // So now when we change the speed below its only applied for the future!
        this.speed = speed;
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