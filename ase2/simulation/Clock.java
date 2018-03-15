package ase2.simulation;

public class Clock 
{
    long startTime;
    long lastTime; // Time at last call
    long lastSimTime;
    long startSimTime = 6*3600*1000; // Start the simulation at 6 am
    long speed = 100000;


    //Static variable keeping the singleton instance of the class
	static private Clock instance;
	/*
	 * Creates a new instance of the class. It's private to allow the Singleton D.P.
	 */
	private Clock() {
        lastTime = System.currentTimeMillis();
        lastSimTime = startSimTime;
	}
    
    public synchronized long getCurrentTime(){
        long currentRealTime = System.currentTimeMillis();
        long elapsedTime = (currentRealTime - lastTime); // elapsed time from last call
        long currentSimTime = lastSimTime + speed*elapsedTime;
        lastSimTime = currentSimTime;
        lastTime = currentRealTime;
        return currentSimTime;
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
        getCurrentTime(); // updates the lastSimTime, so we are change the speed for only the time moving forward
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