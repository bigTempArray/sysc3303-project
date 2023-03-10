package server;

import java.util.LinkedList;
import java.util.Queue;

import states.SchedulerState;

/**
 * 
 * @author ben, Abdul
 */
public class Scheduler implements Runnable {

	/**
	 * Scheduler states
	 * AVAILABLE is when elevator is not in process
	 * IN_PROCESS is elevator is processing the request
	 */

	private boolean inProcess, isAvailable, onDestination, requiresPassengers; // true means elevator is in process,
																				// initially is false
	// until it gets request from floor system
	private Queue<Passenger> floorRequests; // requests from floor system
	private int elevatorLocation, destination; // where elevator is and where is going to
	private SchedulerState curState;
	private LinkedList<CarInstance> elevatorList;
	

	public Scheduler() {
		inProcess = false;
		isAvailable = true;
		floorRequests = new LinkedList<>();
		elevatorList = new LinkedList<CarInstance>();
		elevatorLocation = 0;
		destination = -1;
		onDestination = false;
		requiresPassengers = true;
		curState = SchedulerState.AVAILABLE;

	}

	// [Floor Thread]
	public synchronized void makeFloorRequest(Passenger request) {
		this.requiresPassengers = false;
		floorRequests.add(request);
		onDestination = false;
		notifyAll(); // notify elevator

	}

	/**
	 * [Elevator Thread]: the function returns the passenger request to elevator and starts the process
	 */
	public synchronized Passenger getNextRequest() {
		// while elevator is still available means there is no request that have been made
		while (inProcess || floorRequests.isEmpty()) {
			try {
				wait();
			} catch (InterruptedException e) {
				System.err.println(e);
			}
		}

		System.out.println("Elevator: Getting the next floor request");
		Passenger nextPassenger = floorRequests.remove();

		destination = nextPassenger.getCarButton();
		changeState();
		return nextPassenger;
	}

	// keeps track where the elevator is, during the process

	public synchronized void reachedDestination() {
		this.elevatorLocation = this.destination;
		changeState();

		if (this.floorRequests.isEmpty()) {
			this.requiresPassengers = true;
		}

		System.out.println("Passenger has reached its destination");
	}
	
	/**
	 * Method that checks elevatorList for all potential 
	 * elevators to send a request to and returns the most 
	 * suitable one for the given job. Calculates based on
	 * availability, distance, and direction. 
	 * @param targetFloor the floor to which the chosen elevator 
	 * will be requested to go to. 
	 * @return the chosen elevator's index within the elevatorList. 
	 */
	private int findBestElevator(int targetFloor) {
		int[] eligibilityTable = new int[elevatorList.size()];
		
		
		//Iterating through the list of candidates 
		for (int elevator = 0; elevator < elevatorList.size(); elevator++) {
			//Scoring priority amount of car priority (the lower the better)
			int priority = 0;
			
			//Standby skips further evaluation as its the better candidate for a request
			if (elevatorList.get(elevator).isOnStandby()) {
				eligibilityTable[elevator] = priority;
				continue;
			}
			
			//If too close to destination (to prevent race conditions)
			if (Math.abs(targetFloor - elevatorList.get(elevator).getCurrentFloor()) < 2) {
				priority += 20; //Ideally should be half of highest floor number
			}
			priority += Math.abs(targetFloor - elevatorList.get(elevator).getCurrentFloor());
			
			//Evaluating if elevator is matching the direction of the target floor 
			if (targetFloor - elevatorList.get(elevator).getCurrentFloor() > 0) { // if target is on top of current car
				if (elevatorList.get(elevator).isAscending()) { // car is already going up towards the target 
					priority += 4;
				}else {
					priority += 8;
				}
			}else {
				if (elevatorList.get(elevator).isAscending()) {
					priority += 8;
				}else {
					priority += 4;
				}
			}
			eligibilityTable[elevator] = priority; //Adding the priority value
		}
		//Iterating through array and returning the smallest element's 
		//index which is the best suited car for the next request. 
		int index = 0;
	    int min = eligibilityTable[index];
	    for (int element = 1; element < eligibilityTable.length; element++){
	        if (eligibilityTable[element] <= min){
	        min = eligibilityTable[element];
	        index = element;
	        }
	    }
	    return index;
	}

	/**
	 * this function changes state according to current state
	 */
	public void changeState() {
		switch (curState) {
			case AVAILABLE:
				inProcess = true;
				curState = SchedulerState.IN_PROCESS;
				break;
			case IN_PROCESS:
				onDestination = true;
				inProcess = false;
				destination = -1;
				curState = SchedulerState.AVAILABLE;
				break;
		}
	}

	// sends the location of elevator(the floor level)
	public synchronized int getElevatorLocation() {
		return elevatorLocation;
	}

	// returns true when elevator reached the destination
	public synchronized boolean isOnDestination() {
		return onDestination;
	}

	public synchronized int getDestination() {
		return destination;
	}

	// returns true when elevator is available
	public synchronized boolean isAvailable() {
		return isAvailable;
	}

	public synchronized boolean isInProcess() {
		return inProcess;
	}

	public synchronized SchedulerState getCurState() {
		return curState;
	}

	public synchronized boolean requiresPassengers() {
		return requiresPassengers;
	}

	@Override
	public void run() {

	}

}