package tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import elevators.Elevator;
import scheduler.Scheduler;
import shared.states.ElevatorState;

/**
 * @author abdul
 * 
 *(Validates that states correspond to elevator.
 * logic handling tests done with the ElevatorController) 
 */
class ElevatorTest {

    Scheduler scheduler = new Scheduler(true);
    Elevator defaultLift;

    @BeforeEach
    @DisplayName("Setting prerequisites")
    public void defaultSetup() {
        /**
         * Before each method's run before each other test case
         * used to reduce the amount of code needed from repeating lines of
         * instantiation.
         */
        defaultLift = new Elevator( 10, 25);
    }

    @Test
    @DisplayName("Elevator can correctly be created")
    public void basicElevatorCheck() {
        assertEquals(10, defaultLift.getNumberOfFloors());
    }

    @Test
    public void loadElevatorCheck() {
        defaultLift.loadElevator();
        assertEquals(ElevatorState.loading, defaultLift.getState());
    }

    @Test
    public void unloadElevatorCheck() {
        defaultLift.unLoadElevator();
        assertEquals(ElevatorState.unLoading, defaultLift.getState());
    }

    @Test
    public void elevatorStopCheck() {
        defaultLift.elevatorStop();
        assertEquals(ElevatorState.stopped, defaultLift.getState());
    }

    @Test
    public void traverseCheck() {
        defaultLift.traverse(2);
        assertEquals(ElevatorState.traversingUp, defaultLift.getState());
    }

    @Test
    public void elevatorAvailableCheck() {
        defaultLift.elevatorAvailable();
        assertEquals(ElevatorState.standBy, defaultLift.getState());
    }

}
