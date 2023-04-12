package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
    
    @Test
	public void doorErrorTest() {
		scheduler=new Scheduler(true);
        elevator=new Elevator(0, 30);
		scheduler.elevatorControllers.add(new ElevatorController(scheduler, 30, true));
		scheduler.floorRequests.add(new FloorRequest(4,2,0,"doors"));
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

        assertFalse(eInfo.isDoorsBroken());
        assertEquals(0, bestController.todoList.size());       
		
		
	}

    //none 0 2 4
	// elevator error handling test
	@Test
	public void elevatorErrorTest() {
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
            

            
            
            new Thread(new Runnable() {
                @Override
                public void run() {
                    bestController.run();
                }
            }).start();            
            
            Thread.sleep(9000);           
            assertTrue(eInfo.isElevatorBroken());
            

        } catch (Exception e) {
            e.printStackTrace();
        }
        
        try {
            Thread.sleep(10000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // assertEquals(true, bestController.elevatorInfo.isElevatorBroken());
        assertEquals(0, bestController.todoList.size());       
		
		
	}

   
}
