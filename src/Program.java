import elevators.Elevator;
import floor.FloorSubsystem;
import scheduler.Scheduler;

public class Program {
    public static void main(String[] args) {
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Elevator.main(args);
                }
            }).start();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    FloorSubsystem.main(args);
                }
            }).start();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    Scheduler.main(args);
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
