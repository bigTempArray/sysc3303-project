/**
 * 
 */
package tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Queue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import server.FloorSubsystem;
import server.Passenger;
import server.Scheduler;
import states.SchedulerState;

/**
 * @author eric
 *
 */
class SchedulerTest {
	@Test
	@DisplayName("Can handle floor making a request for an elevator")
	public void makeFloorRequestCheck() {
		Scheduler scheduler = new Scheduler();
		FloorSubsystem floor = new FloorSubsystem(scheduler);
		Queue<Passenger> passengers = floor.readFile();

		// * elevator should be available right now
		assertEquals(scheduler.isAvailable(), true);

		// * floor thread -> make a request for an elevator
		scheduler.makeFloorRequest(passengers.poll());

		// * elevator is currently not available
		// assertEquals(scheduler.isAvailable(), false);
		assertEquals(scheduler.isOnDestination(), false);
	}

	@Test
	@DisplayName("Can handle elevator getting the next floor request")
	public void getNextRequestCheck() {
		Scheduler scheduler = new Scheduler();
		FloorSubsystem floor = new FloorSubsystem(scheduler);
		Queue<Passenger> passengers = floor.readFile();

		// floor thread -> make a request for an elevator
		scheduler.makeFloorRequest(passengers.poll());

		// elevator is currently not available
		// assertEquals(false, scheduler.isAvailable());
		assertEquals(false, scheduler.isOnDestination());

		// * elevator is not in use yet, and does not have a destination
		assertEquals(SchedulerState.AVAILABLE, scheduler.getCurState());
		assertEquals(false, scheduler.isInProcess());
		assertEquals(-1, scheduler.getDestination());

		// * elevator thread -> get the next floor request
		scheduler.getNextRequest();

		// * elevator should be in use now, and heading to a destination
		assertEquals(SchedulerState.IN_PROCESS, scheduler.getCurState());
		assertEquals(true, scheduler.isInProcess());
		assertEquals(4, scheduler.getDestination());
	}

	@Test
	@DisplayName("Elevator can send updates to the scheduler on the current floor level")
	public void sendElevatorUpdatesCheck() {
		Scheduler scheduler = new Scheduler();
		FloorSubsystem floor = new FloorSubsystem(scheduler);
		Queue<Passenger> passengers = floor.readFile();

		// floor thread -> make a request for an elevator
		scheduler.makeFloorRequest(passengers.poll());

		// elevator is currently not available
		// assertEquals(false, scheduler.isAvailable());
		assertEquals(false, scheduler.isOnDestination());

		// elevator is not in use yet, and does not have a destination
		assertEquals(SchedulerState.AVAILABLE, scheduler.getCurState());
		assertEquals(false, scheduler.isInProcess());
		assertEquals(-1, scheduler.getDestination());

		// elevator thread -> get the next floor request
		scheduler.getNextRequest();

		// * elevator should be in use now, and heading to a destination
		assertEquals(SchedulerState.IN_PROCESS, scheduler.getCurState());
		assertEquals(true, scheduler.isInProcess());
		assertEquals(4, scheduler.getDestination());

		// * elevator is currently at the 0th floor
		assertEquals(0, scheduler.getElevatorLocation());

		// * elevator thread -> reached the fourth floor (the destination)
		scheduler.waitForReachedDestination();
		assertEquals(SchedulerState.AVAILABLE, scheduler.getCurState());
		assertEquals(4, scheduler.getElevatorLocation());
		assertEquals(true, scheduler.isOnDestination());
		assertEquals(false, scheduler.isInProcess());
		assertEquals(true, scheduler.isAvailable());
		assertEquals(-1, scheduler.getDestination());
	}
}
