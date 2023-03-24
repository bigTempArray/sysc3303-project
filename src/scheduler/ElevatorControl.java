package scheduler;

import java.net.DatagramSocket;
import java.net.SocketException;

import shared.FloorRequest;

public class ElevatorControl implements Runnable {
    private Scheduler scheduler;
    private int elevatorPort;
    private DatagramSocket socket;

    public ElevatorControl(Scheduler scheduler, int elevatorPort) {
        this.scheduler = scheduler;
        this.elevatorPort = elevatorPort;

        try {
            this.socket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (true) {
            FloorRequest floorRequest = this.scheduler.floorRequests.get(0);

            // TODO: synchronize this
            if (this.scheduler.findBestElevator(floorRequest.getFloor()) == this.elevatorPort) {
                // remove floor request (synchronized)
            } else {
                
            }
        }
    }
}
