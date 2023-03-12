import elevators.Elevator;
import server.FloorSubsystem;
import server.Scheduler;

public class Driver {
	
	public static void main(String[] args) {
		Scheduler scheduler = new Scheduler(false);

		Thread schedulerThread = new Thread(scheduler, "Scheduler");
		Thread floorSubsystem = new Thread(new FloorSubsystem(scheduler), "Floor subsystem");

		schedulerThread.start();
		floorSubsystem.start();
		
	}

}
