package elevators;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import server.Scheduler;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for elevator class
 * These tests check the integrity
 * and correctness of the Elevator as a class
 * rather than a thread. ALl thread related tests
 * are handled by the main or scheduler test class.
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
        defaultLift = new Elevator(scheduler, 10, 1);
    }

    @Test
    @DisplayName("Elevator can correctly be created")
    public void basicElevatorCheck(){
        assertEquals(10, defaultLift.getNumberOfFloors());
    }

}