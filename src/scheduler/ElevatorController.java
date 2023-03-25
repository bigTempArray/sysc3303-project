package scheduler;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;

import shared.FloorRequest;

public class ElevatorController implements Runnable {
    private Scheduler scheduler;
    public int elevatorPort;
    public ElevatorInfo elevatorInfo;
    public int controllerPort;
    public ArrayList<FloorRequest> todoList;

    private DatagramSocket socket;
    private DatagramPacket sendPacket, receivePacket;

    public ElevatorController(Scheduler scheduler, int elevatorPort, ElevatorInfo elevatorInfo) {
        this.scheduler = scheduler;
        this.elevatorPort = elevatorPort;
        this.elevatorInfo = elevatorInfo;
        this.controllerPort = elevatorPort + 10;
        this.todoList = new ArrayList<>();

        try {
            this.socket = new DatagramSocket(controllerPort);
        } catch (SocketException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void sendToElevator(byte[] sendBytes) {
        try {
            this.sendPacket = new DatagramPacket(sendBytes, sendBytes.length, InetAddress.getLocalHost(), this.elevatorPort);
            this.receivePacket = new DatagramPacket(new byte[0], 0);

            this.socket.send(this.sendPacket);
            System.out.println("[ElevatorController Port " + this.elevatorPort + "]: sending floor request to elevator");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void trackLocation(int origin, int end) {
        try {
            byte[] receiveBytes = new byte[1];
            this.receivePacket = new DatagramPacket(receiveBytes, receiveBytes.length);
    
            while (this.elevatorInfo.getCurrentFloor() != end) {
                this.socket.receive(this.receivePacket);
                int location = (byte) receiveBytes[0];
                System.out.println("[ElevatorController]: elevator's current position is: " + location);
                this.elevatorInfo.setCurrentFloor(location);
            }   
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                boolean hasFloorRequests = !this.todoList.isEmpty();
                if (hasFloorRequests) {
                    FloorRequest floorRequest = this.todoList.remove(0);
                    
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    ObjectOutput objectOutput = new ObjectOutputStream(outputStream);
                    objectOutput.writeObject(floorRequest);
                    objectOutput.close();
                    byte[] sendBytes = outputStream.toByteArray();
                    this.sendToElevator(sendBytes);

                    // track progress as it reaches passenger floor
                    this.trackLocation(this.elevatorInfo.getCurrentFloor(), floorRequest.getFloor());            

                    // // track progress as it reaches destination
                    this.trackLocation(this.elevatorInfo.getCurrentFloor(), floorRequest.getDestination());            
                }
    
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
