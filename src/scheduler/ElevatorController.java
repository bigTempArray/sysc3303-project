package scheduler;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import shared.Engine;
import shared.FloorRequest;

public class ElevatorController implements Runnable {
    public int elevatorPort;
    public ElevatorInfo elevatorInfo;
    public int controllerPort;
    public ArrayList<FloorRequest> todoList;
    private Engine mockEngine;
    
    private DatagramSocket socket;
    private DatagramPacket sendPacket, receivePacket;

    public ElevatorController(int elevatorPort, ElevatorInfo elevatorInfo) {
        this.elevatorPort = elevatorPort;
        this.elevatorInfo = elevatorInfo;
        this.controllerPort = elevatorPort + 10;
        this.todoList = new ArrayList<>();
        this.mockEngine = new Engine(10, 1.1, 3, 2, 0.3, 4);

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
            // System.out.println("[" + this.getName() + "]: sending floor request to elevator");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void trackLocation(int origin, int end) {
        try {
            byte[] receiveBytes = new byte[1];
            this.receivePacket = new DatagramPacket(receiveBytes, receiveBytes.length);
            
            // TODO: set socket timeout = floor delay + some latency time here
    
            while (this.elevatorInfo.getCurrentFloor() != end) {
                try {
                    this.socket.receive(this.receivePacket);
                } catch (SocketTimeoutException e) {
                    // elevator took too long
                }
                int location = (byte) receiveBytes[0];
                // System.out.println("[" + this.getName() + "]: elevator's current position is: " + location);
                this.elevatorInfo.setCurrentFloor(location);
            }   

            // TODO: set socket timeout to indefinite here
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Scans the todo list of floors to
     * visit and chooses the most suitable floor
     * to traverse to based on the elevator placement
     * and current direction.
     * @return the element index of the best to do
     */
    private synchronized int findClosestTask() {
    	int[] eligibilityTable = new int[todoList.size()];
    	
    	//Iterating through list of candidates 
    	for (int task = 0; task < todoList.size(); task++) {
    		int priority = 0;
    		
    		//Checking direction compatibility
    		if (elevatorInfo.isAscending()) {
    			if (todoList.get(task).getFloor() < elevatorInfo.getCurrentFloor()){
    				priority += 20;
    			}
    		}else {
    			if (todoList.get(task).getFloor() > elevatorInfo.getCurrentFloor()) {
    				priority += 20;
    			}
    		}
    		
    		//Gauging distance from the current floor 
    		priority += Math.abs(elevatorInfo.getCurrentFloor() - todoList.get(task).getFloor());	    
            eligibilityTable[task] = priority;
    	}
    	
		// Iterating through array and returning the smallest element's
		// index which is the best suited task for the next request.
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

    // private String getName() {
    //     return "ElevatorController-" + this.elevatorPort;
    // }

    @Override
    public void run() {
        try {
            while (true) {
                boolean hasFloorRequests = !this.todoList.isEmpty();
                if (hasFloorRequests) {
                    FloorRequest floorRequest = this.todoList.remove(this.findClosestTask());
                    
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    ObjectOutput objectOutput = new ObjectOutputStream(outputStream);
                    objectOutput.writeObject(floorRequest);
                    objectOutput.close();

                    byte[] sendBytes = outputStream.toByteArray();
                    this.sendToElevator(sendBytes);

                    // track progress as it reaches passenger floor
                    this.elevatorInfo.setAscending(this.elevatorInfo.getCurrentFloor() > floorRequest.getFloor());
                    this.elevatorInfo.setOnStandby(false);
                    this.trackLocation(this.elevatorInfo.getCurrentFloor(), floorRequest.getFloor());            
                    // System.out.println("[" + this.getName() + "]: elevator picked up passengers");

                    // // track progress as it reaches destination
                    this.elevatorInfo.setAscending(this.elevatorInfo.getCurrentFloor() > floorRequest.getDestination());
                    this.trackLocation(this.elevatorInfo.getCurrentFloor(), floorRequest.getDestination());            
                    // System.out.println("[" + this.getName() + "]: elevator reached destination");

                    this.elevatorInfo.setOnStandby(true);
                }
    
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
