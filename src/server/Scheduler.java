package server;

import java.util.LinkedList;
import java.util.Queue;

public class Scheduler implements Runnable{
	
	// scheduler states
	enum State {
        AVAILABLE,
        IN_PROCESS
    }
	
	private boolean inProcess, isAvailable, onDestination;  		// true means elevator is in process, initially is false until it gets request from floor system
	private Queue<Passenger> floorRequests; 							//requests from floor system
	private int elevatorLocation, destination;						//where elevator is and where is going to
	private State curState; 
	
	public Scheduler () {
		inProcess=false;
		isAvailable=true;
		floorRequests= new LinkedList<>();
		elevatorLocation=0;
		destination=-1;			
		onDestination=false;
		curState=State.AVAILABLE;
		
	}
	
	//function receives request from floor system

	public synchronized void makeFloorRequest(Passenger request) {

		//after receiving request from floor it adds on queue
		// changes the state 
		floorRequests.add(request);
		changeState();

	}
	
	/**
	 * the function returns the passenger request to elevator and starts the process 
	 */
	
	public synchronized Passenger getNextRequest() {
		
		//while elevator is still available means there is no request that have been made
		while (inProcess || floorRequests.isEmpty()) {
            try { 
                wait();
            } catch (InterruptedException e) {
                System.err.println(e);
            }
        }
		
		Passenger nextPassemger=floorRequests.remove();
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
			 changeState();			 
			 notifyAll();
			 return true;
		 }	
		 
		 return false;
		 
		 
		
	}
	
	/**
	 * this function changes state according to the current state
	 */
	public void changeState() {
		
		switch(curState) {
		
		//changes state from AVAILABLE to IN_PROCESS 
        case AVAILABLE:
    		onDestination=false;
            curState=State.IN_PROCESS;
            break;
            
        case IN_PROCESS:
        	onDestination=true;
			inProcess=false;
			destination=-1;
	        curState=State.AVAILABLE;
	        break;
		}
	
	}
		
	
	//sends the location of elevator(the floor level)
		public int getElevatorUpdates() {
			return elevatorLocation;
		}
		
		// returns true when elevator reached the destination
		public boolean isOnDestination() {
			return onDestination;
		}

		// returns true when elevator is available
		public boolean isAvailable() {
			return isAvailable;
		}
		
		// return current State
		public State getCurState() {
			return curState;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			while(true) {
				
			}
		}

}