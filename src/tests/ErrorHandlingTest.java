package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.Test;

import elevators.Elevator;
import scheduler.ElevatorController;
import scheduler.ElevatorInfo;
import scheduler.Scheduler;
import shared.FloorRequest;

public class ErrorHandlingTest {

    Scheduler scheduler;
    ElevatorController bestController;
    ElevatorInfo eInfo;
    Elevator elevator;
    //none 0 2 4
	// testing if ellevator receives one request and serve the passenger
	@Test
	public void testOnePassenge() {
		scheduler=new Scheduler(true);
        elevator=new Elevator(0, 30);
		scheduler.elevatorControllers.add(new ElevatorController(scheduler, 30, true));
		scheduler.floorRequests.add(new FloorRequest(4,2,0,"elevator"));
		int bestEle=scheduler.findBestElevator(0);
		bestController=scheduler.elevatorControllers.get(bestEle);
        eInfo=bestController.elevatorInfo;

		try {

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
            

            Thread.sleep(1000);           
            
            
            new Thread(new Runnable() {
                @Override
                public void run() {
                    bestController.run();
                }
            }).start();            
            
            Thread.sleep(5000);
            assertTrue(eInfo.isDoorsBroken());
            

        } catch (Exception e) {
            e.printStackTrace();
        }
        
        try {
            Thread.sleep(10000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertEquals(true, bestController.elevatorInfo.isElevatorBroken());
        assertEquals(0, bestController.todoList.size());

        
		
		
	}
}
