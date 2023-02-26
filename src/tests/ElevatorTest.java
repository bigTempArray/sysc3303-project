/**
 * 
 */
package tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import elevators.Elevator;
import elevators.State;
import server.Scheduler;

/**
 * @author abdul
 *
 */
class ElevatorTest {

	Scheduler scheduler = new Scheduler();
    Elevator defaultLift;

    @BeforeEach
    @DisplayName("Setting prerequisites")
    public void defaultSetup(){
        /**
         * Before each method's run before each other test case
         * used to reduce the amount of code needed from repeating lines of
         * instantiation.
         */
        defaultLift = new Elevator(scheduler, 10);
    }

    @Test
    @DisplayName("Elevator can correctly be created")
    public void basicElevatorCheck(){
        assertEquals(10, defaultLift.getNumberOfFloors());
    }
    @Test
    public void loadElevatorCheck(){
    	defaultLift.loadElevator();
        assertEquals(State.loading, defaultLift.getState());
    }
    @Test
    public void unloadElevatorCheck(){
    	defaultLift.unLoadElevator();
        assertEquals(State.unLoading, defaultLift.getState());
    }
    @Test
    public void elevatorStopCheck(){
    	defaultLift.elevatorStop();
        assertEquals(State.stopped, defaultLift.getState());
    }
    @Test
    public void traverseCheck(){
    	defaultLift.traverse(2);
        assertEquals(State.traversingUp, defaultLift.getState());
    }
    @Test
    public void elevatorAvailableCheck(){
    	defaultLift.elevatorAvailable();
        assertEquals(State.standBy, defaultLift.getState());
    }

}
