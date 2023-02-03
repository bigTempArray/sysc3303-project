package server;

import java.util.LinkedList;
import java.util.Queue;

public class Scheduler {
	
	private boolean inProcess, isAvailable, onDestination;  		// true means elevator is in process, initially is false until it gets request from floor system
	private Queue<Object> floorRequests; 							//requests from floor system
	private int elevatorLocation, destination;						//where elevator is and where is going to
	
	public Scheduler () {
		inProcess=false;
		isAvailable=true;
		floorRequests= new LinkedList<>();
		elevatorLocation=0;
		destination=-1;			
		onDestination=false;
		
	}
	
	//function receives request from floor system

	public synchronized void makeFloorRequest(Object request) {
		
		//if is not available is true means elevator is in process and floor has to wait
		while (!isAvailable) {
            try { 
                wait();
            } catch (InterruptedException e) {
                System.err.println(e);
            }
        }

		//after receiving request from floor it adds on queue
		// make inProcess true
		// and notify all threads
		floorRequests.add(request);
		isAvailable=false;
		onDestination=false;
		notifyAll();

	}
	
	/**
	 * the function returns the passenger request to elevator and starts the process 
	 */
	
	public synchronized Object getNextRequest() {
		
		//while elevator is still available means there is no request that have been made
		while (isAvailable) {
            try { 
                wait();
            } catch (InterruptedException e) {
                System.err.println(e);
            }
        }
		
		Object nextPassemger=floorRequests.remove();
		inProcess=true;
		return nextPassemger;
	}

	// lamps [] booleans
	// iterate through array and find the destination floor
	// function return destination=-1 if there is no button pressed

	public synchronized int sendlamps(boolean lamps[]) {
		
		
		while(inProcess) {
			try { 
                wait();
            } catch (InterruptedException e) {
                System.err.println(e);
            }
		}

		for(int i=0; i<lamps.length;i++) {
			if(lamps[i]) {
				destination=i;
			}
		}		
		return destination;
	}	
	
	
	
	//keeps track where the elevator is, during the process
	
	public synchronized boolean sendElevatorUpdates(int level) {
		
		// while the elevator is not in process and destination button haven't been pressed yet,
		//elevator should not be able to send updates 
		while(!inProcess && destination!=-1) {
			try { 
                wait();
            } catch (InterruptedException e) {
                System.err.println(e);
            }
		}
		elevatorLocation=level;
		
		//if elevatorLocation==destination means that elevator reached the destination
		//elevator is not longer in process
		// and elevator is available
		 if(elevatorLocation==destination) {
			 onDestination=true;
			 inProcess=false;
			 isAvailable=true;
			 destination=-1;
			 
			 notifyAll();
			 return true;
		 }	
		 
		 return false;
		 
		 
		
	}
	
	//sends the location of elevator(the floor level)
		public int getElevatorUpdates() {
			return elevatorLocation;
		}
		
		// returns true when elevator reached the destination
		public boolean isOnDestinatiom() {
			return onDestination;
		}

		// returns true when elevator is available
		public boolean isAvailable() {
			return isAvailable;
		}

}