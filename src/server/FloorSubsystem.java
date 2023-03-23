package server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
/**
 * Class for floor Subsystem
 * */
public class FloorSubsystem implements Runnable{
    private final Scheduler scheduler;
    
    /**
     * Datagram packets for sending and receiving
     *  Dagram socket for both sending and receiving
     */
    private DatagramPacket sendPacket, receivePacket;
    private  DatagramSocket sendReceiveSocket;
    
/**
 * Default constructor that creates a Floor Subsystem and contains a Scheduler as a shared object
 * @param scheduler is a shared object used between elevator and floor
* */
    public FloorSubsystem(Scheduler scheduler){
        this.scheduler=scheduler;
        
        try {
            // send and receive UDP Datagram packets.
            sendReceiveSocket = new DatagramSocket();
        } catch (SocketException se) {   // Can't create the socket.
            se.printStackTrace();
            System.exit(1);
        }

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
            if(!passengers.isEmpty()) {
                System.out.println("Passenger queued");
//	                scheduler.makeFloorRequest(passengers.remove());
                // Serialize to a byte array
                ByteArrayOutputStream bStream = new ByteArrayOutputStream();
                ObjectOutput oo;
                try {
                    oo = new ObjectOutputStream(bStream);
                    oo.writeObject(passengers.remove());
                    byte[] serializedMessage = bStream.toByteArray();
                    oo.close();
                    sender(serializedMessage);
                    receiver();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
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
    
    
    
    /**
     * Sender() helps to send provided message to the specified port
     * @param buffer is message to be sent in form of bytes
     */
    
    public void sender(byte[] buffer){

        try {
            sendPacket = new DatagramPacket(buffer, buffer.length,
                    InetAddress.getLocalHost(), 23);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            System.exit(1);
        }

        System.out.println("FLoor subsystem: Sending packet:");
        System.out.println("To : " + sendPacket.getAddress());
        System.out.println("Destination  port: " + sendPacket.getPort());
        int len = sendPacket.getLength();
        System.out.println("Length: " + len);
        System.out.print("Containing: ");
        
        ObjectInputStream iStream;
		try {
			iStream = new ObjectInputStream(new ByteArrayInputStream(buffer));
			Passenger passenger;
			try {
				passenger = (Passenger) iStream.readObject();
				System.out.println(passenger.toString());
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	iStream.close();
	    	
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
        // Send the datagram packet to the server via the send/receive socket.

        try {
            sendReceiveSocket.send(sendPacket);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        System.out.println("Floor Subsystem: Packet sent.\n");

    }
    
    /**
     * receiver() is used to receive message over the network
     */

    public void receiver(){

        byte data[] = new byte[100];
        receivePacket = new DatagramPacket(data, data.length);

        try {
            // Block until a datagram is received via sendReceiveSocket.
            sendReceiveSocket.receive(receivePacket);
        } catch(IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        // Process the received datagram.
        System.out.println("FLoor subsystem: Packet received:");
        System.out.println("From : " + receivePacket.getAddress());
        System.out.println("SOurce port: " + receivePacket.getPort());
        int len = receivePacket.getLength();
        System.out.println("Length: " + len);
        System.out.print("Containing: ");
        System.out.println(new String(receivePacket.getData(), 0, len));
        System.out.println("\n --------------------------- \n");

    }
    
    
}
