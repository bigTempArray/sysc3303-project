package server;

import java.util.LinkedList;
import java.util.Queue;

<<<<<<< HEAD
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
		
=======
public class Scheduler implements Runnable {

	private boolean inProcess, isAvailable, onDestination; // true means elevator is in process, initially is false
															// until it gets request from floor system
	private Queue<Passenger> floorRequests; // requests from floor system
	private int elevatorLocation, destination; // where elevator is and where is going to

	public Scheduler() {
		inProcess = false;
		isAvailable = true;
		floorRequests = new LinkedList<>();
		elevatorLocation = 0;
		destination = -1;
		onDestination = false;

>>>>>>> main
	}

	// function receives request from floor system

	public synchronized void makeFloorRequest(Passenger request) {

<<<<<<< HEAD
		//after receiving request from floor it adds on queue
		// changes the state 
		floorRequests.add(request);
		changeState();
=======
		// if is not available is true means elevator is in process and floor has to
		// wait
		while (!isAvailable) {
			try {
				wait();
			} catch (InterruptedException e) {
				System.err.println(e);
			}
		}

		// after receiving request from floor it adds on queue
		// make inProcess true
		// and notify all threads
		floorRequests.add(request);
		isAvailable = false;
		onDestination = false;
		notifyAll();
>>>>>>> main

	}

	/**
	 * the function returns the passenger request to elevator and starts the process
	 */

	public synchronized Passenger getNextRequest() {
<<<<<<< HEAD
		
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
=======

		// while elevator is still available means there is no request that have been
		// made
		while (isAvailable) {
			try {
				wait();
			} catch (InterruptedException e) {
				System.err.println(e);
			}
		}

		Passenger nextPassenger = floorRequests.remove();
		destination = nextPassenger.getCarButton();
		inProcess = true;
		return nextPassenger;
>>>>>>> main
	}

	// keeps track where the elevator is, during the process

	public synchronized boolean sendElevatorUpdates(int currentLevel) {
		// while the elevator is not in process and destination button haven't been
		// pressed yet,
		// elevator should not be able to send updates

		while (!inProcess && destination != -1) {
			try {
				wait();
			} catch (InterruptedException e) {
				System.err.println(e);
			}
		}
		elevatorLocation = currentLevel;

		// if elevatorLocation==destination means that elevator reached the destination
		// elevator is not longer in process
		// and elevator is available
<<<<<<< HEAD
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
=======
		if (elevatorLocation == destination) {
			onDestination = true;
			inProcess = false;
			isAvailable = true;
			destination = -1;
>>>>>>> main

			notifyAll();
			return true;
		}
		
		// return current State
		public State getCurState() {
			return curState;
		}

		return false;

	}

	// sends the location of elevator(the floor level)
	public int getElevatorLocation() {
		return elevatorLocation;
	}

	// returns true when elevator reached the destination
	public boolean isOnDestination() {
		return onDestination;
	}

	public int getDestination() {
		return destination;
	}

	// returns true when elevator is available
	public boolean isAvailable() {
		return isAvailable;
	}

	public boolean isInProcess() {
		return inProcess;
	}

	public synchronized void startMotor() {
		while (!this.inProcess) {
			try {
				wait();
			} catch (Exception e) {
				System.err.println(e);
			}
		}

		// do delay once in process
		notifyAll();
	}

	@Override
	public void run() {
		while (true) {
			this.startMotor();
		}
	}

}