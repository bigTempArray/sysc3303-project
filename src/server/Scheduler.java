package server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.Queue;

import states.SchedulerState;

/**
 * 
 * @author ben, Abdul
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
	private LinkedList<CarInstance> elevatorList;
	public boolean isTest;
	/**
	 * Datagram packets for sending and receiving
	 * Dagram socket for sending and receiving
	 */

	 DatagramPacket sendPacket, receivePacket, responsePacket;
	 DatagramSocket sendSocket, receiveSocket;

	 DatagramSocket sendReceiveSocket; // socket for sending and receiving to/from elevator

	public Scheduler(boolean test) {
		inProcess = false;
		isAvailable = true;
		floorRequests = new LinkedList<>();
		elevatorList = new LinkedList<CarInstance>();
		elevatorLocation = 0;
		destination = -1;
		onDestination = false;
		requiresPassengers = true;
		curState = SchedulerState.AVAILABLE;
		isTest=test;
		// Construct a datagram socket and bind it to port 23
		// on the local host machine. This socket will be used to
		// receive UDP Datagram packets.
		for (int i = 30; i < 33; i++) {
			CarInstance elevatorInfo = new CarInstance();
			elevatorInfo.setPortNumber(i);
			elevatorInfo.setCurrentFloor(0);
			this.elevatorList.add(elevatorInfo);
		}
		if(!isTest) {
		try {
			
			sendSocket = new DatagramSocket();
			receiveSocket = new DatagramSocket(23);

			sendReceiveSocket = new DatagramSocket(24);

			

		} catch (SocketException e) {
			e.printStackTrace();
		}
		}}
	

	// [Floor Thread]
	public void makeFloorRequest(Passenger request) {

		this.requiresPassengers = false;
		floorRequests.add(request);
		System.out.println("Testing size: " + floorRequests.size());
		onDestination = false;
		
		int bestElevatorPort = this.findBestElevator(destination);

		for (int i = 0;i<this.elevatorList.size();i++) {
			if (i == bestElevatorPort) {
				
				elevatorList.get(i).setAscending(request.getCarButton() > elevatorList.get(i).getCurrentFloor());
				elevatorList.get(i).setOnStandby(false);
				if(!isTest) {
				ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
				ObjectOutput objectOutput;
				try {
					objectOutput = new ObjectOutputStream(byteStream);
					objectOutput.writeObject(request);
					byte[] sendBytes = byteStream.toByteArray();
					objectOutput.close();
					this.sendPacket = new DatagramPacket(sendBytes, sendBytes.length, InetAddress.getLocalHost(), elevatorList.get(i).getPortNumber());
					System.out.println(bestElevatorPort);
					this.sendReceiveSocket.send(this.sendPacket);

					// listen for elevator updates as it reaches the location of passenger
					this.receiveElevatorUpdates(bestElevatorPort, request.getFloor());
					System.out.println("Elevator reached passenger");
				} catch (Exception e) {
					System.err.println(e);
				}
			}}
		}
	}

	/**
	 * [Elevator Thread]: the function returns the passenger request to elevator and
	 * starts the process
	 */
	public synchronized Passenger getNextRequest() {
		// while elevator is still available means there is no request that have been
		// made
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

	public synchronized void waitForReachedDestination(int elevatorLocation, int destination) {
		// listen for elevator updates until it reaches destination
		if(!isTest) {
		this.receiveElevatorUpdates(elevatorLocation, destination);
		try {
			// Wait for elevator to reach destination
			this.receivePacket = new DatagramPacket(new byte[1], 1);
			this.sendReceiveSocket.receive(receivePacket);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// set elevator on standby
		for (CarInstance carInstance : this.elevatorList) {
			if (carInstance.getPortNumber() == this.receivePacket.getPort()) {
				carInstance.setOnStandby(true);
			}
		}
		}
		this.elevatorLocation = this.destination;
		changeState();

		if (this.floorRequests.isEmpty()) {
			this.requiresPassengers = true;
		}

		System.out.println("Passenger has reached its destination");
	}

	/**
	 * Method that checks elevatorList for all potential
	 * elevators to send a request to and returns the most
	 * suitable one for the given job. Calculates based on
	 * availability, distance, and direction.
	 * 
	 * @param targetFloor the floor to which the chosen elevator
	 *                    will be requested to go to.
	 * @return the chosen elevator's index within the elevatorList.
	 */
	public int findBestElevator(int targetFloor) {
		int[] eligibilityTable = new int[elevatorList.size()];

		// Iterating through the list of candidates
		for (int elevator = 0; elevator < elevatorList.size(); elevator++) {
			// Scoring priority amount of car priority (the lower the better)
			int priority = 0;

			// Standby skips further evaluation as its the better candidate for a request
			if (elevatorList.get(elevator).isOnStandby()) {
				eligibilityTable[elevator] = priority;
				continue;
			}

			// If too close to destination (to prevent race conditions)
			if (Math.abs(targetFloor - elevatorList.get(elevator).getCurrentFloor()) < 2) {
				priority += 20; // Ideally should be half of highest floor number
			}
			priority += Math.abs(targetFloor - elevatorList.get(elevator).getCurrentFloor());

			// Evaluating if elevator is matching the direction of the target floor
			if (targetFloor - elevatorList.get(elevator).getCurrentFloor() > 0) { // if target is on top of current car
				if (elevatorList.get(elevator).isAscending()) { // car is already going up towards the target
					priority += 4;
				} else {
					priority += 8;
				}
			} else {
				if (elevatorList.get(elevator).isAscending()) {
					priority += 8;
				} else {
					priority += 4;
				}
			}
			eligibilityTable[elevator] = priority; // Adding the priority value
		}
		// Iterating through array and returning the smallest element's
		// index which is the best suited car for the next request.
		int index = 0;
		int min = eligibilityTable[index];
		for (int element = 1; element < eligibilityTable.length; element++) {
			if (eligibilityTable[element] <= min) {
				min = eligibilityTable[element];
				index = element;
			}
		}
		return index;
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
	 * 
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public Passenger floorReceiver() throws IOException, ClassNotFoundException {
		byte data[] = new byte[200];
		receivePacket = new DatagramPacket(data, data.length);

		// Block until a datagram packet is received from receiveSocket.
		try {
			System.out.println("Scheduler: Waiting.....\n");
			; // so we know we're waiting
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
				makeFloorRequest(passenger);
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

	public void floorReply(byte[] res) {
		sendPacket = new DatagramPacket(res, res.length,
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

	private void receiveElevatorUpdates(int elevatorPort, int destination) {
		byte[] receiveBytes = new byte[1];
		this.receivePacket = new DatagramPacket(receiveBytes, receiveBytes.length);

		while (true) {
			try {
				this.sendReceiveSocket.receive(receivePacket);
				if (receiveBytes.length == 1) {
					int location = (byte) receiveBytes[0];

					for (CarInstance elevatorInfo : this.elevatorList) {
						if (elevatorInfo.getPortNumber() == elevatorPort) {
							elevatorInfo.setCurrentFloor(location);
						}
					}

					if (location == destination)
						break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void run() {

		while (true) {
			try {
				Passenger passenger = floorReceiver();
				waitForReachedDestination(passenger.getFloor(), passenger.getCarButton());
			} catch (ClassNotFoundException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			floorReply("ACKNOWLEGMENT".getBytes());
		}
		// makeFloorRequest();
		// try {
		// floorReceiver();
		// } catch (ClassNotFoundException | IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// floorReply("ACKNOWLEGMENT".getBytes());
	}

}