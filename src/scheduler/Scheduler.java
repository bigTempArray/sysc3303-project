package scheduler;

import java.util.ArrayList;
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
	public Queue<FloorRequest> floorRequests; // requests from floor system
	public List<ElevatorInfo> elevatorList;
	private List<ElevatorController> elevatorControllers;
	public boolean isTest;

	public Scheduler(boolean test) {
		this.floorRequests = new LinkedList<>();
		this.elevatorList = Collections.synchronizedList(new LinkedList<>());
		this.elevatorControllers = new ArrayList<>();
		this.isTest = test;

		// the elevator ports
		for (int i = 30; i < 31; i++) {
			// create the elevator info object
			ElevatorInfo elevatorInfo = new ElevatorInfo();
			elevatorInfo.setPortNumber(i);
			elevatorInfo.setCurrentFloor(0);
			this.elevatorList.add(elevatorInfo);

			// create a thread to control the elevator
			ElevatorController elevatorControl = new ElevatorController(this, i);
			this.elevatorControllers.add(elevatorControl);
			Thread elevatorControlThread = new Thread(elevatorControl, "ElevatorControl" + i);
			elevatorControlThread.start();
		}

		// create the floor control thread
		Thread floorControlThread = new Thread(new FloorController(this), "FloorControl");
		floorControlThread.start();
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
	private int findBestElevator(int targetFloor) {
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

	private ElevatorController getElevatorController(int elevatorPort) {
		for (ElevatorController controller: this.elevatorControllers) {
			if (controller.elevatorPort == elevatorPort) {
				return controller;
			}
		}

		return null;
	}

	private void start() throws Exception {
		while (true) {
			boolean hasFloorRequests = !this.floorRequests.isEmpty();
			if (hasFloorRequests) {
				FloorRequest floorRequest = this.floorRequests.poll();
				System.out.println("[Scheduler]: found a new floor request at floor: " + floorRequest.getFloor());

				int bestElevatorIndex = this.findBestElevator(floorRequest.getFloor());
				int elevatorPort = this.elevatorList.get(bestElevatorIndex).getPortNumber();
				ElevatorController controller = this.getElevatorController(elevatorPort);
				if (controller != null) {
					controller.todoList.add(floorRequest);
					System.out.println("[Scheduler]: added new request in elevator controller");
				}
			}
			
			Thread.sleep(1000);
		}
	}

	public static void main(String[] args) throws Exception {
		Scheduler scheduler = new Scheduler(false);
		scheduler.start();
	}
}