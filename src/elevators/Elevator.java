package elevators;
import server.Scheduler;
import server.Passenger;

/**
 * Elevator class which represents the
 * cars and all the accompanying
 * functionality of system.
 *
 * @author Abdul Kazal
 */
public class Elevator implements Runnable{

    //Configurable attributes (lamps and buttons)
    //that can be configured during thread creation
    private int numberOfFloors = 0;
    private boolean buttons[]; // true means pressed, false otherwise
    private boolean lamps[];  // true means lights on, false otherwise
    private boolean doors;   // true is open, false is closed
    private Object motor = new Object(); // Placeholder data type for engine
    private Scheduler scheduler; // elevator's communication line with the scheduler
    private State elevatorState; // Contains the current state of the elevator in state machine fashion
    
    /**
     * Collection of all possible elevator states 
     * as enumerations. Each state is as described:
     * 
     * standBy: 
     * The initial state of the class. This is when there
     * is no active objective for the elevator, thus is will remain 
     * on whatever floor it's in with the doors indefinitely opened. 
     * Goes into standby after finishing all requests. 
     * 
     * traversing:
     * This state is active when the elevators speed is not 
     * zero. Whether up or down, retrieving passengers or 
     * delivering them, the state of the elevator being in 
     * motion is denoted by these states. 
     * 
     * loading: 
     * When the elevator reaches a floor and is accepting
     * passengers to enter the car, this is the state that 
     * the elevator will be in.
     * 
     * unLoading:
     * When an elevator reaches a floor desired by the passenger,
     * and now the passengers are exiting the car. (Even in the real 
     * world, if there are passengers exiting and entering on a single 
     * floor, this usually happens sequentially and not concurrently. 
     * 
     * stopped:
     * An intermediary state that occur in between larger operations. 
     * This is whenever the elevator is not moving or loading, but 
     * not in standBy as well. This could be due to multiple causes
     * such as before / after closing doors. Right before and after 
     * traversal to a destination, an emergency seizure in the midst
     * of operations, etc. 
     * 
     */
    enum State {
    	standBy,
    	traversingUp,
    	traversingDown,
    	loading,
    	unLoading,
    	stopped
    }

    //Basic getters for some essential elevator attributes
    public int getNumberOfFloors() {
        return numberOfFloors;
    }

    public boolean[] getLamps() {
        return lamps;
    }

    public boolean doorStatus() {
        return doors;
    }
    
    public State getState() {
    	return elevatorState;
    }

    /**
     * Constructor method sets the chosen configuration for the system
     * Future iterations buttonPressed may be a list for multiple floors chosen
     *
     * @param scheduler what the elevator will communicate with
     * @param highestFloor the size of the building with the highest floor
     * @param buttonPressed which button has been pressed within elevator
     *
     */
    public Elevator(Scheduler scheduler, int highestFloor){
        this.scheduler = scheduler;
        numberOfFloors = highestFloor;
        buttons = new boolean[numberOfFloors];
        lamps = new boolean[numberOfFloors];
        doors = true;
        elevatorState = State.standBy;
    }

    /**
     * Thread runnable, upon start() it will use the constructed object's
     * parameters to send the scheduler the designated floor that was pressed.
     * Then once it receives the signal back from the scheduler with
     * the 'go-ahead' on which floor to go to first, then the elevator will
     * then execute methods to reach given floor. (Future iteration)
     */
    public void run(){
        // Call will depend on scheduler's class. Needs to be filled out.
        //scheduler.sendLamps(boolean lamps);
    	while (true) {
    		Passenger person = scheduler.getNextRequest();
    		//Once the request is taken all the related information
    		// of the destination is taken from the passenger class
    		
    		int buttonPressed = person.getCarButton();
    		
            //Simulating button press on class creation
            buttons[buttonPressed-1] = true;
            lamps[buttonPressed-1] = true;
            
            // Get's to target floor then gives update to the scheduler
            scheduler.sendElevatorUpdates(buttonPressed);
            
    	}
    }
}
