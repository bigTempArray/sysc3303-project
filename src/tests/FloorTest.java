package tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Queue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import floor.FloorSubsystem;
import scheduler.Scheduler;
import shared.FloorRequest;


class FloorTest {

	Scheduler scheduler = new Scheduler(true);
	FloorSubsystem floor;

	@BeforeEach
	@DisplayName("Setting prerequisites")
	public void defaultSetup() {
		this.floor = new FloorSubsystem();
	}

	@Test
	@DisplayName("Can properly read input file")
	public void readInputFileCheck() {
		Queue<FloorRequest> passengerQueue = this.floor.readFile();

		// assert there are 3 passengers in the queue
		assertEquals(3, passengerQueue.size()); 

		// assert the times are correct
		assertEquals("14:05:15.0", passengerQueue.poll().getTime());
		assertEquals("15:00:20.0", passengerQueue.poll().getTime());
		assertEquals("15:00:25.0", passengerQueue.poll().getTime());
	}
}
