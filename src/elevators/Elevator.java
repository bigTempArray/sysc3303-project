package elevators;
import server.Scheduler;

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
    private Object engine = new Object(); // Placeholder datatype for engine
    private Scheduler scheduler; // elevator's communication line with the scheduler

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

    /**
     * Constructor method sets the chosen configuration for the system
     * Future iterations buttonPressed may be a list for multiple floors chosen
     *
     * @param scheduler what the elevator will communicate with
     * @param highestFloor the size of the building with the highest floor
     * @param buttonPressed which button has been pressed within elevator
     *
     */
    public Elevator(Scheduler scheduler, int highestFloor, int buttonPressed){
        numberOfFloors = highestFloor;
        buttons = new boolean[numberOfFloors];
        lamps = new boolean[numberOfFloors];
        doors = true;

        //Simulating button press on class creation
        buttons[buttonPressed-1] = true;
        lamps[buttonPressed-1] = true;
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
    }
}
