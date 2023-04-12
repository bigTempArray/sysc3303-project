package floor;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import shared.FloorRequest;

/**
 * Class for floor Subsystem
 * */
public class FloorSubsystem {
    /**
     * Datagram packets for sending and receiving
     *  Dagram socket for both sending and receiving
     */
    private DatagramPacket sendPacket, receivePacket;
    private  DatagramSocket socket;
    
/**
 * Default constructor that creates a Floor Subsystem and contains a Scheduler as a shared object
* */
    public FloorSubsystem() {
        try {
            // send and receive UDP Datagram packets.
            socket = new DatagramSocket();
            socket.setSoTimeout(50);
        } catch (SocketException se) {
            se.printStackTrace();
            System.exit(1);
        }
    }
/**
 * it will read the input from a file and if the elevator is avalaibkle it will send the first passenger over to the scheduler for the elevator
 * when the elevator arrives at the persons destionation, the floor will output a update
 *
 * */
    public void start() {
        try {
            Queue<FloorRequest> floorRequests= readFile();
            while(true){
                if(!floorRequests.isEmpty()) {
                    FloorRequest floorRequest = floorRequests.remove();
                    Thread.sleep(floorRequest.getTime() * 1000);
                    // System.out.println("[Floor subsystem]: FloorRequest queued");
    
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    ObjectOutput objectOutput = new ObjectOutputStream(outputStream);
                    objectOutput.writeObject(floorRequest);
                    objectOutput.close();
                    byte[] sendBytes = outputStream.toByteArray();
                    send(sendBytes);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Method responsible for reading the input file and saving it into a queue of passsengers that will be sent to the scheduler
     * @return Queue<Passenger> object that contains a list of passengers
     *
     * */
    public Queue<FloorRequest> readFile() {
        int time;
        int floor;
        int destination;
        String[] lines;
        String faultType;
        Queue<FloorRequest> passengerList = new LinkedList<>();
        try {
            Scanner floorFile = new Scanner(new File("src/input-files/no-faults.txt"));

            while (floorFile.hasNextLine()) {

                lines = floorFile.nextLine().split(" ");

                time = Integer.parseInt(lines[1]);
                floor = Integer.parseInt(lines[2]);
                destination = Integer.parseInt(lines[3]);
                faultType = lines[0];
                passengerList.add(new FloorRequest(time, floor, destination, faultType));

            }
            floorFile.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return passengerList;
    }
    
    
    
    /**
     * Sender() helps to send provided message to the specified port
     * @param buffer is message to be sent in form of bytes
     */
    
    public void send(byte[] buffer) throws Exception {
        this.sendPacket = new DatagramPacket(buffer, buffer.length, InetAddress.getLocalHost(), 24);
        this.receivePacket = new DatagramPacket(new byte[0], 0);
		
        this.socket.send(sendPacket);
        // System.out.println("[Floor sub]: sending Passenger info to the floor control");

        while (true) {
            try {
                // System.out.println("[Floor sub]: waiting for acknowledgement");
                this.socket.receive(this.receivePacket);
                break;
            } catch (SocketTimeoutException e) {
                this.socket.send(this.sendPacket);
                // System.out.println("[Floor sub]: sending a passenger to the floor control");
            }
        }

        // System.out.println("[Floor sub]: received acknowledgement");
    }
    
    
    public static void main(String[] args) {
        FloorSubsystem floorSubsystem = new FloorSubsystem();
        floorSubsystem.start();
    }    
}
