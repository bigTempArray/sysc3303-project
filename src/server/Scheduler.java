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
	 * IN_PROCESS is elevator is processing the request
	 */

	private boolean inProcess, isAvailable, onDestination, requiresPassengers; // true means elevator is in process,
																				// initially is false
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
		requiresPassengers = true;
		curState = SchedulerState.AVAILABLE;

	}

	// FLOOR THREAD
	public synchronized void makeFloorRequest(Passenger request) {
		this.requiresPassengers = false;
		floorRequests.add(request);
		onDestination = false;
		notifyAll(); // notify elevator

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
		notifyAll();
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