import elevators.Elevator;
import server.FloorSubsystem;
import server.Scheduler;

public class Driver {
	
	public static void main(String[] args) {
		Scheduler scheduler = new Scheduler();

		Thread schedulerThread = new Thread(scheduler, "Scheduler");
		Thread floorSubsystem = new Thread(new FloorSubsystem(scheduler), "Floor subsystem");

		Thread elevatorThread1 = new Thread(new Elevator(scheduler, 20, 30));
		Thread elevatorThread2 = new Thread(new Elevator(scheduler, 20, 31));
		Thread elevatorThread3 = new Thread(new Elevator(scheduler, 20, 32));

		schedulerThread.start();
		floorSubsystem.start();
		elevatorThread1.start();
		elevatorThread2.start();
		elevatorThread3.start();
	}

}
