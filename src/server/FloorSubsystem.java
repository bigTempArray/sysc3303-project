package server;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
/**
 * Class for floor Subsystem
 * */
public class FloorSubsystem implements Runnable{
    private final Scheduler scheduler;
/**
 * Default constructor that creates a Floor Subsystem and contains a Scheduler as a shared object
 * @param scheduler is a shared object used between elevator and floor
* */
    public FloorSubsystem(Scheduler scheduler){
        this.scheduler=scheduler;

    }
/**
 * Method that is called when the method start() is invoked,
 * it will read the input from a file and if the elevator is avalaibkle it will send the first passenger over to the scheduler for the elevator
 * when the elevator arrives at the persons destionation, the floor will output a update
 *
 * */
    @Override
    public void run() {
        Queue<Passenger> passengers=new LinkedList<>();
        passengers=readFile();
        while(true){
            if(scheduler.isAvailable()) {
            	if(!passengers.isEmpty()) {
	                System.out.println("Passenger queued");
	                scheduler.makeFloorRequest(passengers.remove());
            	}
            }
            
            // if passenger queue empty and scheduler requires passenger then exit
            if(passengers.isEmpty() && scheduler.requiresPassengers()){
                System.exit(0);
            }
        }
    }
    /**
     * Method responsible for reading the input file and saving it into a queue of passsengers that will be sent to the scheduler
     * @return Queue<Passenger> object that contains a list of passengers
     *
     * */
    public Queue<Passenger> readFile() {
        String time;
        int floor;
        String floorButton;
        int carButton;
        String[] lines;
        Queue<Passenger> passengerList = new LinkedList<>();
        try {
            Scanner floorFile = new Scanner(new File("src/input.txt"));

            while (floorFile.hasNextLine()) {

                lines = floorFile.nextLine().split(" ");

                time = lines[0];
                floor = Integer.parseInt(lines[1]);
                floorButton = lines[2];
                carButton = Integer.parseInt(lines[3]);
                passengerList.add(new Passenger(time, floor, floorButton, carButton));

            }
            floorFile.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return passengerList;
    }

}
