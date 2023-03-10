package server;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.Queue;

import states.SchedulerState;

/**
 * 
 * @author ben
 */
public class Scheduler implements Runnable {

	/**
	 * Scheduler states
	 * AVAILABLE is when elevator is not in process
	 * IN_PROCESS is elevator is processing the request
	 */

	private boolean inProcess, isAvailable, onDestination, requiresPassengers; // true means elevator is in process,
																				// initially is false
	// until it gets request from floor system
	private Queue<Passenger> floorRequests; // requests from floor system
	private int elevatorLocation, destination; // where elevator is and where is going to
	private SchedulerState curState;
	
	/**
     * Datagram packets for sending and receiving
     *  Dagram socket for sending and receiving
     */
	
	DatagramPacket sendPacket, receivePacket, responsePacket;
    DatagramSocket sendSocket, receiveSocket;
    
    DatagramSocket sendReceiveSocket;		// socket for sending and receiving to/from elevator

	public Scheduler() {
		inProcess = false;
		isAvailable = true;
		floorRequests = new LinkedList<>();
		elevatorLocation = 0;
		destination = -1;
		onDestination = false;
		requiresPassengers = true;
		curState = SchedulerState.AVAILABLE;
		
		// Construct a datagram socket and bind it to port 23
        // on the local host machine. This socket will be used to
        // receive UDP Datagram packets.
        try {
        	sendSocket = new DatagramSocket();
			receiveSocket = new DatagramSocket(23);
			
			sendReceiveSocket = new DatagramSocket();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// [Floor Thread]
	public void makeFloorRequest() {
		this.requiresPassengers = false;
		floorRequests.add(request);
		onDestination = false;		
		
		notifyAll(); // notify elevator

	}

	/**
	 * [Elevator Thread]: the function returns the passenger request to elevator and starts the process
	 */
	public synchronized Passenger getNextRequest() {
		// while elevator is still available means there is no request that have been made
		while (inProcess || floorRequests.isEmpty()) {
			try {
				wait();
			} catch (InterruptedException e) {
				System.err.println(e);
			}
		}

		System.out.println("Elevator: Getting the next floor request");
		Passenger nextPassenger = floorRequests.remove();

		destination = nextPassenger.getCarButton();
		changeState();
		return nextPassenger;
	}

	// keeps track where the elevator is, during the process

	public synchronized void reachedDestination() {
		this.elevatorLocation = this.destination;
		changeState();

		if (this.floorRequests.isEmpty()) {
			this.requiresPassengers = true;
		}

		System.out.println("Passenger has reached its destination");
	}

	/**
	 * this function changes state according to current state
	 */
	public void changeState() {
		switch (curState) {
			case AVAILABLE:
				inProcess = true;
				curState = SchedulerState.IN_PROCESS;
				break;
			case IN_PROCESS:
				onDestination = true;
				inProcess = false;
				destination = -1;
				curState = SchedulerState.AVAILABLE;
				break;
		}
	}

	// sends the location of elevator(the floor level)
	public synchronized int getElevatorLocation() {
		return elevatorLocation;
	}

	// returns true when elevator reached the destination
	public synchronized boolean isOnDestination() {
		return onDestination;
	}

	public synchronized int getDestination() {
		return destination;
	}

	// returns true when elevator is available
	public synchronized boolean isAvailable() {
		return isAvailable;
	}

	public synchronized boolean isInProcess() {
		return inProcess;
	}

	public synchronized SchedulerState getCurState() {
		return curState;
	}

	public synchronized boolean requiresPassengers() {
		return requiresPassengers;
	}
	
	/**
	 * receiver() receives messages over the network
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	public Passenger floorReceiver() throws IOException, ClassNotFoundException {
		byte data[] = new byte[200];
        receivePacket = new DatagramPacket(data, data.length);

        // Block until a datagram packet is received from receiveSocket.
        try {
        	System.out.println("Scheduler: Waiting.....\n");; // so we know we're waiting
            receiveSocket.receive(receivePacket);
        } catch (IOException e) {
            System.out.print("IO Exception: likely:");
            System.out.println("Receive Socket Timed Out.\n" + e);
            e.printStackTrace();
            System.exit(1);
        }
        
     // Process the received datagram.
        System.out.println("Scheduler: Packet received:");
        System.out.println("From : " + receivePacket.getAddress());
        System.out.println("Source port: " + receivePacket.getPort());
        int len = receivePacket.getLength();
        System.out.println("Length: " + len);
        System.out.print("Containing: ");
            	
        ObjectInputStream iStream;
        Passenger passenger;
		try {
			iStream = new ObjectInputStream(new ByteArrayInputStream(data));
			try {
				passenger = (Passenger) iStream.readObject();
				System.out.println(passenger.toString());
				System.out.println("\n");
				return passenger;
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	iStream.close();
	    	
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return null;
	}
	
	public void floorReply(byte [] res) {
		sendPacket = new DatagramPacket(res,res.length,
                receivePacket.getAddress(), receivePacket.getPort());

        System.out.println("Floor subsystem: Sending packet:");
        System.out.println("To Client: " + sendPacket.getAddress());
        System.out.println("Destination Client port: " + sendPacket.getPort());
        int len = sendPacket.getLength();
        System.out.println("Length: " + len);
        System.out.print("Containing: ");
        System.out.println(new String(sendPacket.getData(), 0, len));
        System.out.println();
        // Send the datagram packet to the client via the send socket.
        try {
            sendSocket.send(sendPacket);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
	}
	
	

	@Override
	public void run() {
		
		while (true) {
			try {
				floorReceiver();
			} catch (ClassNotFoundException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			floorReply("ACKNOWLEGMENT".getBytes());
		}
//		makeFloorRequest();
//		try {
//			floorReceiver();
//		} catch (ClassNotFoundException | IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		floorReply("ACKNOWLEGMENT".getBytes());
	}

}