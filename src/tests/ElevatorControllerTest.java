package tests;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Random;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import scheduler.ElevatorController;
import scheduler.ElevatorInfo;
import shared.FloorRequest;


/**
 * 
 * @author abdul
 * 
 * Tests will focus on the logic 
 * of finding the best elevator. 
 * 
 *
 */


public class ElevatorControllerTest {
	
	ElevatorController control;
	ElevatorInfo car;
		
	//Helper functions to reduce repeating code in unit tests
	/**
	 * The following parameters is for setting the attributes
	 * for the floor request object to be created. 
	 * 
	 * @param time 
	 * @param floor
	 * @param destination
	 * @return the request object created from imposed parameters
	 */
	public FloorRequest generateRequest(int time, int floor, int destination) {
		FloorRequest request = new FloorRequest(time, floor, destination);
		return request;
	}
	
	/**
	 * Helper function which creates new instance of control object.
	 * The following parameters are used to set the associated elevator 
	 * info attribute of the control object instance. 
	 * 
	 * @param currentFloor 
	 * @param ascending
	 * @param onStandby
	 * 
	 */
	public void igniteControl(int currentFloor, boolean ascending, boolean onStandby) {
		car = new ElevatorInfo();
		car.setCurrentFloor(currentFloor);
		car.setAscending(ascending);
		car.setOnStandby(onStandby);
		control = new ElevatorController(getRandomPortNumber(), car);
	}
	
	public int getRandomPortNumber() {
	    int min = 1025;
	    int max = 65536;
	    Random random = new Random();
	    return random.nextInt(max - min + 1) + min;
	}
	
	/**
	 * Function that populates the current control's toDo list with 
	 * floor requests. 
	 * @param startFloors a list of requests to be added. Represents the floor attribute of the request. 
	 */
	public void populateToDoList(int[] startFloors) {
		for (int index = 0; index < startFloors.length; index++) {
			FloorRequest call = new FloorRequest(0, startFloors[index], startFloors[index] + 5);
			control.todoList.add(call);
		}
	}
	
	@Test
	@DisplayName("Validating function usable")
	public void checkUsability() {
		igniteControl(1,true, true);
		int[] floors = {1,5,7};
		populateToDoList(floors);
		int result = -1;
		result = control.findClosestTask();
		assertFalse(result == -1);
	}

	@Test
	@DisplayName("Chooses task elevator is currently on")
	public void obviousChoice() {
		igniteControl(1,true, true);
		int[] floors = {1,5,7};
		populateToDoList(floors);
		int result = -1;
		result = control.findClosestTask();
		assertEquals(1, floors[result]);
	}
	
	@Test
	@DisplayName("Same task chosen from middle of list")
	public void obviousChoiceMiddle() {
		igniteControl(5,true, true);
		int[] floors = {1,5,7};
		populateToDoList(floors);
		int result = -1;
		result = control.findClosestTask();
		assertEquals(5, floors[result]);
	}

	@Test
	@DisplayName("Same task chosen from end of list")
	public void obviousChoiceend() {
		igniteControl(7,true, true);
		int[] floors = {1,5,7};
		populateToDoList(floors);
		int result = -1;
		result = control.findClosestTask();
		assertEquals(7, floors[result]);
	}
	
	@Test 
	@DisplayName("Best task chosen: Basic")
	public void optimalTask() {
		igniteControl(8,true, true);
		int[] floors = {1,5,10, 2, 4, 17};
		populateToDoList(floors);
		int result = -1;
		result = control.findClosestTask();
		assertEquals(10, floors[result]);
	}
	
	@Test 
	@DisplayName("Best task chosen: Directionality factor A") //As in considers elevator ascending
	public void optimalTaskAscending() {
		igniteControl(8,true, true);
		int[] floors = {1,7,10, 2, 4, 17};
		populateToDoList(floors);
		int result = -1;
		result = control.findClosestTask();
		assertEquals(10, floors[result]);
	}
	
	@Test 
	@DisplayName("Best task chosen: Directionality factor B") //As in considers elevator ascending
	public void optimalTaskDescending() {
		igniteControl(8,false, true);
		int[] floors = {1,5,9, 2, 4, 17};
		populateToDoList(floors);
		int result = -1;
		result = control.findClosestTask();
		assertEquals(5, floors[result]);
	}
	
	@Test
	@DisplayName("Still able to choose task with one remaining")
	public void lastTaskLeft() {
		igniteControl(8,false, true);
		int[] floors = {17};
		populateToDoList(floors);
		int result = -1;
		result = control.findClosestTask();
		assertEquals(17, floors[result]);
	}
	
}
