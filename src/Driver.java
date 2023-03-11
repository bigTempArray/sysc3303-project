import elevators.Elevator;
import server.FloorSubsystem;
import server.Scheduler;

public class Driver {
	
	public static void main(String[] args) {
		Scheduler scheduler = new Scheduler();

		Thread schedulerThread = new Thread(scheduler, "Scheduler");
		Thread floorSubsystem = new Thread(new FloorSubsystem(scheduler), "Floor subsystem");
		Thread elevatorThread = new Thread(new Elevator(scheduler, 20, 25));

		schedulerThread.start();
		floorSubsystem.start();
		elevatorThread.start();
	}

}
