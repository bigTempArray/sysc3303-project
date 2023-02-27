package server;

import java.util.LinkedList;
import java.util.Queue;
/**
 * 
 * @author ben
 */
public class Scheduler implements Runnable {
	
	
	 /**
	  * Scheduler states
	  * AVAILABLE is when elevator is not in process
	  *IN_PROCESS is elevator is processing the request
	  */
	enum SchedulerState {
		AVAILABLE,
		IN_PROCESS
	}
	
	private boolean inProcess, isAvailable, onDestination; // true means elevator is in process, initially is false
															// until it gets request from floor system
	private Queue<Passenger> floorRequests; // requests from floor system
	private int elevatorLocation, destination; // where elevator is and where is going to
	private SchedulerState curState;
	public Scheduler() {
		inProcess = false;
		isAvailable = true;
		floorRequests = new LinkedList<>();
		elevatorLocation = 0;
		destination = -1;
		onDestination = false;
		curState=SchedulerState.AVAILABLE;

	}

	// function receives request from floor system

	public synchronized void makeFloorRequest(Passenger request) {

		floorRequests.add(request);
		onDestination = false;
		notifyAll();

	}

	/**
	 * the function returns the passenger request to elevator and starts the process
	 */

	public synchronized Passenger getNextRequest() {

		// while elevator is still available means there is no request that have been
		// made
		while (inProcess || floorRequests.isEmpty()) {
			try {
				wait();
			} catch (InterruptedException e) {
				System.err.println(e);
			}
		}
		Passenger nextPassenger = floorRequests.remove();
		destination = nextPassenger.getCarButton();
		changeState();
		return nextPassenger;
	}

	// keeps track where the elevator is, during the process

	public synchronized boolean sendElevatorUpdates(int currentLevel) {
		// while the elevator is not in process and destination button haven't been
		// pressed yet,
		// elevator should not be able to send updates
//		System.out.println("SCHEDULER: updates ");
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
		if (elevatorLocation == destination) {
			
			//changing scheduler state
			changeState();
			
			System.out.println("SCHEDULER: Passenger has reached its destination ");
			
			if(floorRequests.isEmpty()) {
				System.exit(0);
			}
			
			notifyAll();
			return true;
		}

		return false;

	}
	
	/**
	 * this function changes state according to current state
	 */
	public void changeState() {
		switch(curState) {
			case AVAILABLE:
				inProcess = true;
				curState=SchedulerState.IN_PROCESS;
				break;
			case IN_PROCESS:
				onDestination = true;
				inProcess = false;
				destination = -1;
				curState=SchedulerState.AVAILABLE;
				break;
		}
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
	
	public SchedulerState getCurState() {
		return curState;
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