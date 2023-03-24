package scheduler;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import shared.FloorRequest;

/**
 * 
 * @author ben, Abdul
 */
public class Scheduler {
	public boolean inProcess, isAvailable, onDestination, requiresPassengers; 
	
	public List<FloorRequest> floorRequests; // requests from floor system
	public List<CarInstance> elevatorList;
	public boolean isTest;

	public Scheduler(boolean test) {
		inProcess = false;
		isAvailable = true;
		// floorRequests = new LinkedList<>();
		floorRequests = Collections.synchronizedList(new LinkedList<>());
		elevatorList = Collections.synchronizedList(new LinkedList<>());
		onDestination = false;
		requiresPassengers = true;
		isTest=test; // TODO: Will be used for testing later
		
		// Construct a datagram socket and bind it to port 23
		// on the local host machine. This socket will be used to
		// receive UDP Datagram packets.
		for (int i = 30; i < 33; i++) {
			CarInstance elevatorInfo = new CarInstance();
			elevatorInfo.setPortNumber(i);
			elevatorInfo.setCurrentFloor(0);
			this.elevatorList.add(elevatorInfo);
		}
	}
	
	/**
	 * Method that checks elevatorList for all potential
	 * elevators to send a request to and returns the most
	 * suitable one for the given job. Calculates based on
	 * availability, distance, and direction.
	 * 
	 * @param targetFloor the floor to which the chosen elevator
	 *                    will be requested to go to.
	 * @return the chosen elevator's index within the elevatorList.
	 */
	public int findBestElevator(int targetFloor) {
		int[] eligibilityTable = new int[elevatorList.size()];

		// Iterating through the list of candidates
		for (int elevator = 0; elevator < elevatorList.size(); elevator++) {
			// Scoring priority amount of car priority (the lower the better)
			int priority = 0;

			// Standby skips further evaluation as its the better candidate for a request
			if (elevatorList.get(elevator).isOnStandby()) {
				eligibilityTable[elevator] = priority;
				continue;
			}

			// If too close to destination (to prevent race conditions)
			if (Math.abs(targetFloor - elevatorList.get(elevator).getCurrentFloor()) < 2) {
				priority += 20; // Ideally should be half of highest floor number
			}
			priority += Math.abs(targetFloor - elevatorList.get(elevator).getCurrentFloor());

			// Evaluating if elevator is matching the direction of the target floor
			if (targetFloor - elevatorList.get(elevator).getCurrentFloor() > 0) { // if target is on top of current car
				if (elevatorList.get(elevator).isAscending()) { // car is already going up towards the target
					priority += 4;
				} else {
					priority += 8;
				}
			} else {
				if (elevatorList.get(elevator).isAscending()) {
					priority += 8;
				} else {
					priority += 4;
				}
			}
			eligibilityTable[elevator] = priority; // Adding the priority value
		}
		// Iterating through array and returning the smallest element's
		// index which is the best suited car for the next request.
		int index = 0;
		int min = eligibilityTable[index];
		for (int element = 1; element < eligibilityTable.length; element++) {
			if (eligibilityTable[element] <= min) {
				min = eligibilityTable[element];
				index = element;
			}
		}
		return index;
	}

	public static void main(String[] args) throws Exception{
		Scheduler scheduler = new Scheduler(false);
		Thread floorControlThread = new Thread(new FloorControl(scheduler), "FloorControl");
		Thread elevatorControlThread1 = new Thread(new ElevatorControl(scheduler, 30), "ElevatorControl1");
		Thread elevatorControlThread2 = new Thread(new ElevatorControl(scheduler, 31), "ElevatorControl2");
		Thread elevatorControlThread3 = new Thread(new ElevatorControl(scheduler, 32), "ElevatorControl3");

		floorControlThread.start();
		// elevatorControlThread.start();
	}
}