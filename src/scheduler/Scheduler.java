package scheduler;

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
	public List<ElevatorController> elevatorControllers;
	public boolean isTest;
	public int amountOfTasks;
	public int tasksProcessed;

	public Scheduler(boolean test) {
		this.floorRequests = new LinkedList<>();
		this.elevatorControllers = new LinkedList<>();
		this.isTest = test;
		this.amountOfTasks = 7;
		this.tasksProcessed = 0;
		
		if (!this.isTest) {
			// the elevator ports
			for (int i = 30; i < 33; i++) {
				

				// create a thread to control the elevator
				ElevatorController controller = new ElevatorController(this, i, false);
				this.elevatorControllers.add(controller);
				Thread elevatorControlThread = new Thread(controller, "ElevatorControl" + i);
				elevatorControlThread.start();
			}

			// create the floor control thread
			Thread floorControlThread = new Thread(new FloorController(this), "FloorControl");
			floorControlThread.start();
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
		int[] eligibilityTable = new int[elevatorControllers.size()];

		// Iterating through the list of candidates
		for (int elevator = 0; elevator < elevatorControllers.size(); elevator++) {
			// Scoring priority amount of car priority (the lower the better)
			int priority = 0;

			// Standby skips further evaluation as its the better candidate for a request
			if (elevatorControllers.get(elevator).elevatorInfo.isOnStandby()) {
				eligibilityTable[elevator] = priority;
				continue;
			}

			// If elevator is broken, skip further evaluation
			if (elevatorControllers.get(elevator).elevatorInfo.isElevatorBroken()) {
				eligibilityTable[elevator] = 10000;
				continue;
			}

			// If too close to destination (to prevent race conditions)
			if (Math.abs(targetFloor - elevatorControllers.get(elevator).elevatorInfo.getCurrentFloor()) < 2) {
				priority += 20; // Ideally should be half of highest floor number
			}
			priority += Math.abs(targetFloor - elevatorControllers.get(elevator).elevatorInfo.getCurrentFloor());

			// Evaluating if elevator is matching the direction of the target floor
			if (targetFloor - elevatorControllers.get(elevator).elevatorInfo.getCurrentFloor() > 0) { // if target is on top of current car
				if (elevatorControllers.get(elevator).elevatorInfo.isAscending()) { // car is already going up towards the target
					priority += 4;
				} else {
					priority += 8;
				}
			} else {
				if (elevatorControllers.get(elevator).elevatorInfo.isAscending()) {
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

	public ElevatorController getElevatorController(int elevatorPort) {
		for (ElevatorController controller: this.elevatorControllers) {
			if (controller.elevatorPort == elevatorPort) {
				return controller;
			}
		}

		return null;
	}

	public void processedTask() {
		this.tasksProcessed++;

		// System.out.println("Tasks processed: " + this.tasksProcessed);
	}

	public void elevatorBroke() {
		this.amountOfTasks--;
	}

	public void start() {
		long start = System.nanoTime();
		try {
			while (true) {
				if (this.tasksProcessed == this.amountOfTasks) {
					long elapsedTime = System.nanoTime() - start;
					System.out.println("The program took " + elapsedTime / 1000000000 + " seconds to run");
					System.out.println("AVG request time was " + (elapsedTime / 1000000000)/tasksProcessed +" seconds");
					this.amountOfTasks = 0;
				}

				FloorRequest floorRequest = this.floorRequests.poll();
				if (floorRequest != null) {
					// System.out.println("[Scheduler]: found a new floor request");

					int bestElevatorIndex = this.findBestElevator(floorRequest.getFloor());
					int elevatorPort = this.elevatorControllers.get(bestElevatorIndex).elevatorPort;
					ElevatorController controller = this.getElevatorController(elevatorPort);
					if (controller != null) {
						synchronized (controller) {
							controller.todoList.add(floorRequest);
						}
						// System.out.println("[Scheduler]: added new request to elevator controller " + elevatorPort);
					}
				}
				
				Thread.sleep(100);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		Scheduler scheduler = new Scheduler(false);
		scheduler.start();
	}
}
