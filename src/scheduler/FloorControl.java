package scheduler;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import shared.FloorRequest;

public class FloorControl implements Runnable {
    private Scheduler scheduler;
    private DatagramSocket socket;
    private DatagramPacket receivePacket;
    private DatagramPacket sendPacket;

    public FloorControl(Scheduler scheduler) throws Exception {
        this.scheduler = scheduler;
        this.socket = new DatagramSocket(24);
    }

    public FloorRequest receiveFloorRequest() {
        try {
            byte data[] = new byte[200];
            this.receivePacket = new DatagramPacket(data, data.length);
            this.socket.receive(this.receivePacket);

            ObjectInputStream iStream;
            FloorRequest passenger;
            iStream = new ObjectInputStream(new ByteArrayInputStream(data));
            
            passenger = (FloorRequest) iStream.readObject();
            iStream.close();

            System.out.println("---------------------");
            System.out.println("Received passenger: \n" + passenger);
            
            this.sendPacket = new DatagramPacket(new byte[0], 0, InetAddress.getLocalHost(), this.receivePacket.getPort());
            System.out.println("sending acknowledgement");
            this.socket.send(this.sendPacket);

            return passenger;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void run() {
        while (true) {
            FloorRequest floorRequest = this.receiveFloorRequest();
            System.out.println("Received a passenger");
            this.scheduler.floorRequests.add(floorRequest);
        }
    }
}
