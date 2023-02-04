/**
 * 
 */
package tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import elevators.Elevator;
import server.Scheduler;

/**
 * @author eric
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

}
