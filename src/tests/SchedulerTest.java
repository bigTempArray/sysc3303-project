/**
 * These tests of scheduler are for the internal logic
 * of the methods seen in the class. The communication
 * testing and interactions would be all addressed through
 * the acceptance test suite. This also applies for any other 
 * test suite of classes that utilize UDP for their functionality.
 * (In other words, the 'glue code' tests are a-tests)
 * 
 * author Abdul
 */
package tests;

import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import scheduler.ElevatorController;
import scheduler.ElevatorInfo;
import scheduler.Scheduler;

public class SchedulerTest{
	
	Scheduler scheduler;
	ElevatorController control;
	
	
	//Helper functions to assist with the unit tests 
	public int getRandomPortNumber() {
	    int min = 1025;
	    int max = 65536;
	    Random random = new Random();
	    return random.nextInt(max - min + 1) + min;
	}
	
	public ElevatorController generateElevatorController(int currentFloor, boolean ascending, boolean onStandby) {
		ElevatorInfo car;
		car = new ElevatorInfo();
		car.setCurrentFloor(currentFloor);
		car.setAscending(ascending);
		car.setOnStandby(onStandby);
		control = new ElevatorController(scheduler, getRandomPortNumber(), true);
		control.elevatorInfo.setCurrentFloor(currentFloor);
		control.elevatorInfo.setAscending(ascending);
		control.elevatorInfo.setOnStandby(onStandby);
		control.socket.close();
		return control;
	}
	
	@AfterEach
	public void closePorts() {
		for (int controllers = 0; controllers < scheduler.elevatorControllers.size(); controllers++) {
			scheduler.elevatorControllers.get(controllers).socket.close();
		}
	}
	
	
	@Test
	@DisplayName("Validating function usable")
	public void methodUsable() {
		//Creating scheduler instance 
		scheduler = new Scheduler(true);
		
		//Adding simulated controller instances (represents elevators)
		scheduler.elevatorControllers.add(generateElevatorController(10, true, false));
		scheduler.elevatorControllers.add(generateElevatorController(1, true, false));
		scheduler.elevatorControllers.add(generateElevatorController(5, true, false));
		scheduler.elevatorControllers.add(generateElevatorController(8, true, false));
		
		int result = -1; //Results represents index of best elevator in reference to the list attribute of scheduler
		result = scheduler.findBestElevator(3);
		
		assertFalse(result == -1);
	}
	
	@Test
	@DisplayName("Correct choice of elevator all ascending")
	public void simpleCorrectChoice() {
		//Creating scheduler instance 
		scheduler = new Scheduler(true);
		
		//Adding simulated controller instances (represents elevators)
		scheduler.elevatorControllers.add(generateElevatorController(10, true, false));
		scheduler.elevatorControllers.add(generateElevatorController(1, true, false));
		scheduler.elevatorControllers.add(generateElevatorController(6, true, false));
		scheduler.elevatorControllers.add(generateElevatorController(8, true, false));
		
		int result = -1;
		result = scheduler.findBestElevator(3);
		
		assertEquals(1, result); 
	}
	
	
	@Test
	@DisplayName("Correct choice of elevator all decending")
	public void simpleCorrectChoiceDecending() {
		//Creating scheduler instance 
		scheduler = new Scheduler(true);
		
		//Adding simulated controller instances (represents elevators)
		scheduler.elevatorControllers.add(generateElevatorController(10, true, false));
		scheduler.elevatorControllers.add(generateElevatorController(4, true, false));
		scheduler.elevatorControllers.add(generateElevatorController(6, true, false));
		scheduler.elevatorControllers.add(generateElevatorController(8, false, false));
		
		int result = -1;
		result = scheduler.findBestElevator(3);
		
		assertEquals(3, result); 
	}
	
	@Test
	@DisplayName("Correct choice for standby")
	public void stanbyCarChosen() {
		//Creating scheduler instance 
		scheduler = new Scheduler(true);
		
		//Adding simulated controller instances (represents elevators)
		scheduler.elevatorControllers.add(generateElevatorController(10, true, true));
		scheduler.elevatorControllers.add(generateElevatorController(4, true, false));
		scheduler.elevatorControllers.add(generateElevatorController(6, true, false));
		scheduler.elevatorControllers.add(generateElevatorController(8, false, false));
		
		int result = -1;
		result = scheduler.findBestElevator(3);
		
		assertEquals(0, result); 
	}
	
	@Test
	@DisplayName("Method still function if all elevators tied in priority")
	public void functionalWithCongruency() {
		//Creating scheduler instance 
		scheduler = new Scheduler(true);
		
		//Adding simulated controller instances (represents elevators)
		scheduler.elevatorControllers.add(generateElevatorController(10, true, true));
		scheduler.elevatorControllers.add(generateElevatorController(10, true, true));
		scheduler.elevatorControllers.add(generateElevatorController(10, true, true));
		scheduler.elevatorControllers.add(generateElevatorController(10, true, true));

		int result = -1;
		result = scheduler.findBestElevator(3);
		
		assertFalse(result == -1);
	}
	
	@Test
	@DisplayName("Method still returns best car with many more elevators")
	public void functionalWithLargeCarCount() {
		//Creating scheduler instance 
		scheduler = new Scheduler(true);
		
		//Adding simulated controller instances (represents elevators)
		scheduler.elevatorControllers.add(generateElevatorController(10, false, false));
		scheduler.elevatorControllers.add(generateElevatorController(6, true, false));
		scheduler.elevatorControllers.add(generateElevatorController(3, true, false));
		scheduler.elevatorControllers.add(generateElevatorController(15, false, false));
		scheduler.elevatorControllers.add(generateElevatorController(3, true, false));
		scheduler.elevatorControllers.add(generateElevatorController(7, false, false));
		scheduler.elevatorControllers.add(generateElevatorController(10, true, false));
		scheduler.elevatorControllers.add(generateElevatorController(5, false, false));

		int result = -1;
		result = scheduler.findBestElevator(12);
		
		assertEquals(6, result);
	}
	
	@Test
	@DisplayName("Standby car preceeds pletheroa of closer elevators")
	public void standbyChosenInsteadOfManyCloserElevators() {
		//Creating scheduler instance 
		scheduler = new Scheduler(true);
		
		//Adding simulated controller instances (represents elevators)
		scheduler.elevatorControllers.add(generateElevatorController(10, false, true));
		scheduler.elevatorControllers.add(generateElevatorController(6, true, true));
		scheduler.elevatorControllers.add(generateElevatorController(3, true, true));
		scheduler.elevatorControllers.add(generateElevatorController(15, false, false));
		scheduler.elevatorControllers.add(generateElevatorController(3, true, true));
		scheduler.elevatorControllers.add(generateElevatorController(7, false, true));
		scheduler.elevatorControllers.add(generateElevatorController(10, true, true));
		scheduler.elevatorControllers.add(generateElevatorController(5, false, true));

		int result = -1;
		result = scheduler.findBestElevator(1);
		
		assertEquals(7, result);
	}
	
	@Test
	@DisplayName("Method still behaves expectantly with only single car")
	public void functionalWithOneCar() {
		//Creating scheduler instance 
		scheduler = new Scheduler(true);
		
		//Adding simulated controller instances (represents elevators)
		scheduler.elevatorControllers.add(generateElevatorController(10, false, false));

		int result = -1;
		result = scheduler.findBestElevator(5);
		
		assertEquals(0, result);
	}
		
}