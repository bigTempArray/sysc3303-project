package tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Queue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import floor.FloorSubsystem;
import scheduler.Scheduler;
import shared.FloorRequest;

/**
 * 
 * @author abdul
 * 
 * Testing input file reading and validating
 * correcting information parsing. 
 */

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
		assertEquals(7, passengerQueue.size()); 
	}
}
