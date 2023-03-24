/**
 * 
 */
package tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import scheduler.Scheduler;
import shared.states.SchedulerState;

/**
 * @author eric
 *
 */

class SchedulerTest {
	private Scheduler scheduler;
	@BeforeEach
	public void setup() {
	 scheduler = new Scheduler(true);
	scheduler.isTest=true;
	}

	@Test
	@DisplayName("Can handle floor making a request for an elevator")
	public void makeFloorRequestCheck() {
		
		//FloorSubsystem floor = new FloorSubsystem(scheduler);
		//Queue<Passenger> passengers = floor.readFile();

		// * elevator should be available right now
		// assertEquals(scheduler.isAvailable, true);

		// * floor thread -> make a request for an elevator
		//scheduler.makeFloorRequest(passengers.poll());

		// * elevator is currently not available
		// assertEquals(scheduler.isAvailable(), false);
		
	}

	@Test
	@DisplayName("Can handle elevator getting the next floor request")
	public void getNextRequestCheck() {
		
		// FloorSubsystem floor = new FloorSubsystem();
		// Queue<Passenger> passengers = floor.readFile();

		// floor thread -> make a request for an elevator
		// scheduler.makeFloorRequest(passengers.poll()); // TODO: function commented due to refactor


		// * elevator is not in use yet, and does not have a destination
		// assertEquals(SchedulerState.AVAILABLE, scheduler.getCurState());
		// assertEquals(false, scheduler.isInProcess());
		// assertEquals(-1, scheduler.getDestination());

		// * elevator thread -> get the next floor request
		// scheduler.getNextRequest(); // TODO: Refactored

		// * elevator should be in use now, and heading to a destination
		// assertEquals(SchedulerState.IN_PROCESS, scheduler.getCurState());
		// assertEquals(true, scheduler.isInProcess());
		// assertEquals(4, scheduler.getDestination());
	}

	@Test
	@DisplayName("Elevator can send updates to the scheduler on the current floor level")
	public void sendElevatorUpdatesCheck() {
	
		// FloorSubsystem floor = new FloorSubsystem();
		// Queue<Passenger> passengers = floor.readFile();

		// floor thread -> make a request for an elevator
		// scheduler.makeFloorRequest(passengers.poll()); // TODO: function commented due to refactor

		// elevator is currently not available
		// assertEquals(false, scheduler.isAvailable());
		// assertEquals(false, scheduler.isOnDestination());

		// // elevator is not in use yet, and does not have a destination
		// assertEquals(SchedulerState.AVAILABLE, scheduler.getCurState());
		// assertEquals(false, scheduler.isInProcess());
		// assertEquals(-1, scheduler.getDestination());

		// // elevator thread -> get the next floor request
		// // scheduler.getNextRequest(); // TODO: Refactored

		// // * elevator should be in use now, and heading to a destination
		// assertEquals(SchedulerState.IN_PROCESS, scheduler.getCurState());
		// assertEquals(true, scheduler.isInProcess());
		// assertEquals(4, scheduler.getDestination());

		// // * elevator is currently at the 0th floor
		// assertEquals(0, scheduler.getElevatorLocation());

		// // * elevator thread -> reached the fourth floor (the destination)
		// // scheduler.waitForReachedDestination(2,3); // TODO: function commented due to refactor
		// assertEquals(SchedulerState.AVAILABLE, scheduler.getCurState());
		// assertEquals(4, scheduler.getElevatorLocation());
		// assertEquals(true, scheduler.isOnDestination());
		// assertEquals(false, scheduler.isInProcess());
		// assertEquals(true, scheduler.isAvailable());
		// assertEquals(-1, scheduler.getDestination());
	}
	@Test
	@DisplayName("bestElevator")
	public void findBEstElevatorTest() {
		// assertEquals(2,scheduler.findBestElevator(4));
	}
	
}
