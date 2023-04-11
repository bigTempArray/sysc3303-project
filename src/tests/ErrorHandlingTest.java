package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

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

    /**
        Testing fault handling of doors 
    */
    
    @Test
    @DisplayName("Handling doors fualt ")
	public void doorErrorTest() {
		scheduler=new Scheduler(true);
        elevator=new Elevator(0, 20);
		scheduler.elevatorControllers.add(new ElevatorController(scheduler, 30, true));

		scheduler.floorRequests.add(new FloorRequest(4,2,0,"doors")); // passing floor request with fault type of doors
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
            
            /*
             * Testing if doors is brokem
             */
            assertTrue(eInfo.isDoorsBroken());
            

        } catch (Exception e) {
            e.printStackTrace();
        }
        
        try {
            Thread.sleep(10000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        /*
         * testing if doors is fixed 
         */
        
        assertFalse(eInfo.isDoorsBroken());
		
		
	}

    /**
        Testing fault handling of elevator 
    */
	@Test
    @DisplayName("Handling elevator fualt ")
	public void elevatorErrorTest() {
		scheduler=new Scheduler(true);
        elevator=new Elevator(0, 30);
		scheduler.elevatorControllers.add(new ElevatorController(scheduler, 30, true));

		scheduler.floorRequests.add(new FloorRequest(4,2,0,"elevator"));  // passing floor request with fault type of elevator 

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

            /*
             * Testing if elevator is broken
             */
            assertTrue(eInfo.isElevatorBroken());
            

        } catch (Exception e) {
            e.printStackTrace();
        }
        
        try {
            Thread.sleep(10000);
        } catch (Exception e) {
            e.printStackTrace();
        }
		
	}

   
}
