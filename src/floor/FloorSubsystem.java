package floor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.ObjectInputStream;
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

import shared.Passenger;
/**
 * Class for floor Subsystem
 * */
public class FloorSubsystem {
    /**
     * Datagram packets for sending and receiving
     *  Dagram socket for both sending and receiving
     */
    private DatagramPacket sendPacket, receivePacket;
    private  DatagramSocket sendReceiveSocket;
    
/**
 * Default constructor that creates a Floor Subsystem and contains a Scheduler as a shared object
* */
    public FloorSubsystem(){
        
        try {
            // send and receive UDP Datagram packets.
            sendReceiveSocket = new DatagramSocket();
            sendReceiveSocket.setSoTimeout(1000);
        } catch (SocketException se) {   // Can't create the socket.
            se.printStackTrace();
            System.exit(1);
        }

    }
/**
 * it will read the input from a file and if the elevator is avalaibkle it will send the first passenger over to the scheduler for the elevator
 * when the elevator arrives at the persons destionation, the floor will output a update
 *
 * */
    public void start() throws Exception {
        Queue<Passenger> passengers= readFile();
        while(true){
            if(!passengers.isEmpty()) {
                Passenger passenger = passengers.remove();
                Thread.sleep(passenger.getTime() * 1000);
                System.out.println("---------------------");
                System.out.println("Passenger queued");

                ByteArrayOutputStream bStream = new ByteArrayOutputStream();
                ObjectOutput oo;
                
                oo = new ObjectOutputStream(bStream);
                oo.writeObject(passenger);
                byte[] serializedMessage = bStream.toByteArray();
                oo.close();
                send(serializedMessage);
            }
        }
    }
    /**
     * Method responsible for reading the input file and saving it into a queue of passsengers that will be sent to the scheduler
     * @return Queue<Passenger> object that contains a list of passengers
     *
     * */
    public Queue<Passenger> readFile() {
        int time;
        int floor;
        int carButton;
        String[] lines;
        Queue<Passenger> passengerList = new LinkedList<>();
        try {
            Scanner floorFile = new Scanner(new File("src/input.txt"));

            while (floorFile.hasNextLine()) {

                lines = floorFile.nextLine().split(" ");

                time = Integer.parseInt(lines[0]);
                floor = Integer.parseInt(lines[1]);
                carButton = Integer.parseInt(lines[2]);
                passengerList.add(new Passenger(time, floor, carButton));

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
        try {
            sendPacket = new DatagramPacket(buffer, buffer.length, InetAddress.getLocalHost(), 24);
            ObjectInputStream iStream = new ObjectInputStream(new ByteArrayInputStream(buffer));
            // Passenger passenger = (Passenger) iStream.readObject();
            iStream.close();
            this.receivePacket = new DatagramPacket(new byte[0], 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
		
        sendReceiveSocket.send(sendPacket);
        System.out.println("sending Passenger info to the floor control");

        while (true) {
            try {
                System.out.println("waiting for acknowledgement");
                this.sendReceiveSocket.receive(this.receivePacket);
                break;
            } catch (SocketTimeoutException e) {
                this.sendReceiveSocket.send(this.sendPacket);
                System.out.println("sending a passenger to the floor control");
            }
        }

        System.out.println("received acknowledgement");
    }
    
    
    public static void main(String[] args) throws Exception {
        FloorSubsystem floorSubsystem = new FloorSubsystem();
        floorSubsystem.start();
    }    
}
