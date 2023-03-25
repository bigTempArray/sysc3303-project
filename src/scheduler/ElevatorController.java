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
            System.out.println("[ElevatorController]: sending floor request to elevator");
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

    /**
     * Scans the todo list of floors to
     * visit and chooses the most suitable floor
     * to traverse to based on the elevator placement
     * and current direction.
     * @return the element index of the best to do
     */
    private int findClosestTask() {
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
                    this.trackLocation(this.elevatorInfo.getCurrentFloor(), floorRequest.getFloor());            
                    System.out.println("Elevator picked up passengers");

                    // // track progress as it reaches destination
                    this.elevatorInfo.setAscending(this.elevatorInfo.getCurrentFloor() > floorRequest.getDestination());
                    this.trackLocation(this.elevatorInfo.getCurrentFloor(), floorRequest.getDestination());            
                    System.out.println("Elevator reached destination");
                }
    
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
