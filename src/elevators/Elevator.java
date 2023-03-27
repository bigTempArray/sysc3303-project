package elevators;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import shared.FloorRequest;
import shared.states.ElevatorState;

/**
 * Elevator class which represents the
 * cars and all the accompanying
 * functionality of system.
 *
 * @author Abdul Kazal
 */
public class Elevator implements Runnable {

    // Configurable attributes (lamps and buttons)
    // that can be configured during thread creation
    private int numberOfFloors = 0;
    private int carLocation; // Where the elevator is situated currently
    private boolean buttons[]; // true means pressed, false otherwise
    private boolean doorsOpen; // true is open, false is closed
    private Engine motor;
    // private Scheduler scheduler; // elevator's communication line with the
    // scheduler
    private ElevatorState state; // Contains the current state of the elevator in state machine fashion

    private DatagramPacket sendPacket, receivePacket;
    private DatagramSocket socket;
    private int port;
    private int controllerPort;

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

    // Basic getters for some essential elevator attributes
    public int getNumberOfFloors() {
        return numberOfFloors;
    }

    public boolean doorStatus() {
        return doorsOpen;
    }

    public int getCarFloorLocation() {
        return carLocation;
    }

    public ElevatorState getState() {
        return state;
    }

    /**
     * Constructor method sets the chosen configuration for the system
     * Future iterations buttonPressed may be a list for multiple floors chosen
     *
     * @param scheduler     what the elevator will communicate with
     * @param highestFloor  the size of the building with the highest floor
     * @param buttonPressed which button has been pressed within elevator
     *
     */
    public Elevator(int highestFloor, int port) {
        // this.scheduler = scheduler;
        this.numberOfFloors = highestFloor;
        this.buttons = new boolean[numberOfFloors];
        this.doorsOpen = true;
        // Instantiating engine specification
        this.motor = new Engine(10, 1.1, 3, 2, 0.3, 4); // See engine class for parameter details
        this.state = ElevatorState.standBy;
        this.carLocation = 1;
        this.port = port;
        this.controllerPort = port + 10;

        try {
            this.socket = new DatagramSocket(port);
        } catch (SocketException e) {
            System.err.println(e);
        }
        elevatorAvailable();
    }

    // event for loading elevator
    public void loadElevator() {
        doorsOpen = true;
        state = ElevatorState.loading;
    }

    // event for unloading elevator
    public void unLoadElevator() {
        doorsOpen = true;
        state = ElevatorState.unLoading;
    }

    // event for stopping elevator
    public void elevatorStop() {
        doorsOpen = false;
        state = ElevatorState.stopped;
    }

    // event for traversing up or down
    public void traverse(int floor) {
        doorsOpen = false;
        if (carLocation < floor) {
            state = ElevatorState.traversingUp;
        } else {
            state = ElevatorState.traversingDown;
        }
    }

    // event for elevator being on standby or free
    public void elevatorAvailable() {
        doorsOpen = true;
        state = ElevatorState.standBy;
    }

    /**
     * Thread runnable, upon start() it will use the constructed object's
     * parameters to send the scheduler the designated floor that was pressed.
     * Then once it receives the signal back from the scheduler with
     * the 'go-ahead' on which floor to go to first, then the elevator will
     * then execute methods to reach given floor. (Future iteration)
     */
    @Override
    public void run() {
        try {
            while (true) {
                byte[] receiveBytes = new byte[200];
                this.receivePacket = new DatagramPacket(receiveBytes, receiveBytes.length);
                this.socket.receive(this.receivePacket);
                FloorRequest floorRequest = this.decodePassenger(receiveBytes);
                System.out.println("[" + this.getName() + "]: received a floor request (" + this.carLocation + " -> " + floorRequest.getFloor() + " -> " + floorRequest.getDestination() + ")");

                // go pick up passenger
                System.out.println("[" + this.getName() + "]: going to pick up passenger");
                this.doorsOpen = false;
                int passengerFloor = floorRequest.getFloor();
                if (passengerFloor > carLocation) {
                    this.state = ElevatorState.traversingUp;
                } else if (passengerFloor < carLocation) {
                    this.state = ElevatorState.traversingDown;
                }
                this.traverseFloor(this.carLocation, passengerFloor);
                this.state = ElevatorState.stopped;
                this.carLocation = passengerFloor;

                // load passenger onto elevator
                this.doorsOpen = true;
                this.state = ElevatorState.loading;
                System.out.println("[" + this.getName() + "]: picked up passengers");

                // simulate button press on class creation
                int destination = floorRequest.getDestination();
                this.buttons[destination - 1] = true;

                // take passenger to destination
                this.doorsOpen = false;
                if (destination > carLocation) {
                    this.state = ElevatorState.traversingUp;
                } else if (destination < carLocation) {
                    this.state = ElevatorState.traversingDown;
                }
                this.traverseFloor(this.carLocation, destination);
                
                // reached destination
                this.state = ElevatorState.stopped;
                this.carLocation = destination;
                this.doorsOpen = true;
                this.state = ElevatorState.unLoading;
                System.out.println("[" + this.getName() + "]: reached destination");

                // send udp of having reached destination

                // on standby now
                this.state = ElevatorState.standBy;
                this.buttons[destination - 1] = false;

                // Perhaps once in standby from the scheduler's requests elevator should check
                // that there are no more buttons[] pressed by checking if any buttons are still
                // pressed, and if they are it calls a function to traverse to that floor in
                // which
                // it drops off the remaining passengers...
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Simulates the traversal of the elevator. Adds the
     * delay and announces its current floor positions
     * throughout the trip.
     * (Also for relaying its position to the scheduler)
     * 
     * @param startingFloor    the floor in which the car starts at
     * @param destinationFloor the floor the car needs to stop at
     */
    private void traverseFloor(int startingFloor, int destinationFloor) {
        int floorDifference = destinationFloor - startingFloor;

        if (floorDifference == 0) return;

        // Calculating total trip delay
        long tripDelay = (long) motor.traverseFloors(startingFloor, destinationFloor) * 1000; // Converting to
                                                                                              // milliseconds
        long singleFloorDelay = tripDelay / floorDifference; // Crude representation of time each floor car will be at

        System.out.println("[" + this.getName() + "]: On floor " + startingFloor);
        System.out.println("[" + this.getName() + "]: Traversing to floor " + destinationFloor);
        int currentFloor = startingFloor;

        for (int floorsTraversed = 0; floorsTraversed < floorDifference; floorsTraversed++) {
            try {
                Thread.sleep(singleFloorDelay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // Increments or decrements based on which direction the elevator is moving
            currentFloor = (this.getState() == ElevatorState.traversingUp) ? currentFloor + 1 : currentFloor - 1;
            System.out.println("[" + this.getName() + "]: Reached floor " + currentFloor);
            /**
             * Perhaps this is where for the UDP implementation you send a datagram to the
             * scheduler to let it know that this elevator is now on the current floor it is
             * at.
             * Then if it receives a reply or anything of the sort (for stopping somewhere
             * in the middle
             * of the trip) then it calls this function again (or make another function) to
             * make the
             * extra stop before continuing on its way. (Maybe use the buttonsPressed[]
             * array to
             * keep track of all the floors it needs to go to drop off the passengers)
             */

            byte[] sendBytes = new byte[] { (byte) currentFloor };
            try {
                this.sendPacket = new DatagramPacket(sendBytes, sendBytes.length, InetAddress.getLocalHost(), this.port + 10);
                this.socket.send(this.sendPacket);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String getName() {
        return "Elevator-" + this.port;
    }

    private FloorRequest decodePassenger(byte[] bytes) {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        ObjectInputStream objectStream;
        try {
            objectStream = new ObjectInputStream(inputStream);
            return (FloorRequest) objectStream.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void main(String[] args) {
        for (int i = 30; i < 33; i++) {
            Thread elevatorThread = new Thread(new Elevator(20, i));
            elevatorThread.start();
        }
    }
}
