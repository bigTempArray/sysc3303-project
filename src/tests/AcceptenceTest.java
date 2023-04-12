package tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.Test;

import elevators.Elevator;
import scheduler.ElevatorController;
import scheduler.Scheduler;
import shared.FloorRequest;

public class AcceptenceTest {
	Scheduler scheduler;
	

	//none 0 2 4
	// testing if ellevator receives one request and serve the passenger
	@Test
	public void testOnePassenge() {
		scheduler=new Scheduler(true);
		scheduler.elevatorControllers.add(new ElevatorController(scheduler, 30, true));
		scheduler.floorRequests.add(new FloorRequest(4,2,0,"none"));
		int bestEle=scheduler.findBestElevator(0);
		Elevator elevator = new Elevator(20, 30);
		ElevatorController bestController=scheduler.elevatorControllers.get(bestEle);

		new Thread(new Runnable() {
			@Override
			public void run() {
				elevator.run();
			}
		}).start();

		new Thread(new Runnable() {
			@Override
			public void run() {
				scheduler.start();
			}
		}).start();

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		assertEquals(1 , bestController.todoList.size());

		new Thread(new Runnable() {
			@Override
			public void run() {
				bestController.run();
			}
		}).start();

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		assertEquals(0 , bestController.todoList.size());
		
		
	}


	
}
