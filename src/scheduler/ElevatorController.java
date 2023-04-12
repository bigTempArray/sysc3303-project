package scheduler;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;

import shared.Engine;
import shared.FloorRequest;

public class ElevatorController implements Runnable {
    public Scheduler scheduler;
    public int elevatorPort;
    public ElevatorInfo elevatorInfo;
    public int controllerPort;
    public ArrayList<FloorRequest> todoList;
    public Stack<FloorRequest> journey;
    private Engine mockEngine;

    private int doorsTimeout;
    
    public DatagramSocket socket;
    private DatagramPacket sendPacket, receivePacket;

    public ElevatorController(Scheduler scheduler, int elevatorPort,  boolean isTest) {
        this.scheduler = scheduler;
        this.elevatorPort = elevatorPort;
        this.controllerPort = elevatorPort + 10;
        this.todoList = new ArrayList<>();
        this.journey = new Stack<>();
        this.mockEngine = new Engine(10, 1.1, 3, 2, 0.3, 4);

        this.doorsTimeout = 3000;

        // create the elevator info object
        this.elevatorInfo = new ElevatorInfo();
        this.elevatorInfo.setPortNumber(elevatorPort);
        this.elevatorInfo.setCurrentFloor(0);

        

        try {
            this.socket = new DatagramSocket(controllerPort);
            this.socket.connect(InetAddress.getLocalHost(), elevatorPort);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        
    }

    private void sendToElevator(byte[] sendBytes) {
        this.receivePacket = new DatagramPacket(new byte[1], 1);
        
        try {
            // this.sendPacket = new DatagramPacket(sendBytes, sendBytes.length, InetAddress.getLocalHost(), elevatorPort);
            this.sendPacket = new DatagramPacket(sendBytes, sendBytes.length);
            this.socket.setSoTimeout(1000);
            this.socket.send(this.sendPacket);
            // System.out.println("[" + this.getName() + "]: sending floor request to elevator");

            while (true) {
                try {
                    this.socket.receive(this.receivePacket);
                    break;
                } catch (SocketTimeoutException e) {
                    this.socket.send(this.sendPacket);
                }
            }

            this.socket.setSoTimeout(0);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void trackDoors() throws Exception {
        byte[] receiveBytes = new byte[1];
        this.receivePacket = new DatagramPacket(receiveBytes, receiveBytes.length);

        
        this.socket.setSoTimeout(this.doorsTimeout);
        try {
            this.socket.receive(this.receivePacket);
        } catch (SocketTimeoutException e1) {
            this.elevatorInfo.setDoorsBroken(true);
            System.out.println(new Exception("[" + this.getName() + "]: doors appear broken, waiting for them to fix"));

            // wait another 5 seconds, if they don't fix it, then it must mean the elevator is broken
            this.socket.setSoTimeout(5000);
            try {
                this.socket.receive(receivePacket);
            } catch (SocketTimeoutException e2) {
                this.elevatorInfo.setElevatorBroken(true);
                this.socket.disconnect();
                for (FloorRequest request : this.todoList) {
                    this.scheduler.floorRequests.add(request);
                    System.out.println("[" + this.getName() + "]: elevator is broken, returning request to scheduler");
                }
                throw new Exception("[" + this.getName() + "]: Elevator broken indefinitely");
            }
            this.elevatorInfo.setDoorsBroken(false);
        }
        

        boolean isDoorsOpen = receiveBytes[0] == 1;
        this.elevatorInfo.setDoorsOpen(isDoorsOpen);
        // System.out.println("[" + this.getName() + "]: doors are now " + (isDoorsOpen ? "open" : "closed"));
    }

    private void trackLocation(int origin, int end) throws Exception {
        long singleFloorDelay = estimateSingleFloorDelay(origin, end);
        this.socket.setSoTimeout((int) singleFloorDelay);
        
        byte[] receiveBytes = new byte[1];
        this.receivePacket = new DatagramPacket(receiveBytes, receiveBytes.length);

        while (!journey.isEmpty() && !this.elevatorInfo.isElevatorBroken()) {
            // get location of elevator

            System.out.println("Controller single floor delay: " + singleFloorDelay);
            try {
                this.socket.receive(this.receivePacket);
            } catch (SocketTimeoutException e) {
                // elevator took too long, therefore broken
                this.elevatorInfo.setElevatorBroken(true);
                this.socket.disconnect();
                throw new Exception("[" + this.getName() + "]: Elevator broken indefinitely (after custom timeout)");
            }
            int location = (byte) receiveBytes[0];
            this.elevatorInfo.setCurrentFloor(location);
            // System.out.println("[" + this.getName() + "]: elevator's current position is: " + location);

            // check if there is a task that we can take, then adjust timeout
            System.out.println("Todo list size: " + this.todoList.size());
            FloorRequest newTask = this.findTaskBeforeDestination(journey.peek().getDestination());
            if (newTask != null) {
                // System.out.println("Added new task to journey");
                this.todoList.remove(newTask);
                this.journey.add(newTask);
                singleFloorDelay = estimateSingleFloorDelay(this.elevatorInfo.getCurrentFloor(), this.journey.peek().getDestination());
                this.socket.setSoTimeout((int) singleFloorDelay);
            }

            // send current destination to elevator
            // System.out.println("Current destination: " + this.journey.peek().getFloor());
            byte[] sendBytes = new byte[] { (byte) this.journey.peek().getFloor() };
            try {
                this.sendPacket = new DatagramPacket(sendBytes, sendBytes.length);
                this.socket.send(this.sendPacket);
            } catch (Exception e) {
                System.out.println(e);
            }
            
            // check if we reached the destination
            boolean reachedDestination = this.elevatorInfo.getCurrentFloor() == journey.peek().getDestination();
            if (reachedDestination) {
                journey.pop();
                Thread.sleep(100);
            }
        }
        // while (!journey.isEmpty()) {
        //     boolean atDestination = this.elevatorInfo.getCurrentFloor() == end;
        //     boolean isBroken = this.elevatorInfo.isDoorsBroken();
        //     if (atDestination || isBroken) break;
            
        //     boolean 
        //     try {
        //         this.socket.receive(this.receivePacket);
        //     } catch (SocketTimeoutException e) {
        //         // elevator took too long, therefore broken
        //         this.elevatorInfo.setElevatorBroken(true);
        //         this.socket.disconnect();
        //         throw new Exception("[" + this.getName() + "]: Elevator broken indefinitely (after custom timeout)");
        //     }
        //     int location = (byte) receiveBytes[0];
        //     // System.out.println("[" + this.getName() + "]: elevator's current position is: " + location);
        //     this.elevatorInfo.setCurrentFloor(location);
        // }   

        // Set timeout back to infinite
        this.socket.setSoTimeout(0);
    }

    private void completedTask() {
        journey.pop();
        // increment something in scheduler
    }

    private long estimateSingleFloorDelay(int origin, int end) {
        int floorDifference = Math.abs(origin - end);
        long tripDelay = (long) this.mockEngine.traverseFloors(origin, end) * 1000; // in milliseconds
        if (floorDifference > 0) {
            long singleFloorDelay = ((tripDelay) / floorDifference) * 3;
            return singleFloorDelay;
        } else {
            return 3000;
        }
    }

    private FloorRequest findTaskBeforeDestination(int destination) {
        if (this.todoList.size() > 0) {
            int elevatorLocation = this.elevatorInfo.getCurrentFloor();
            // int destination = this.journey.firstElement().getDestination();
            boolean isAscending = this.elevatorInfo.getCurrentFloor() < destination;
    
            for (FloorRequest floorRequest: this.todoList) {
                int passengerDestination = floorRequest.getDestination();
                int passengerLocation = floorRequest.getFloor();    
    
                if (isAscending) {
                    if (passengerDestination >= elevatorLocation && passengerDestination <= destination) {
                        boolean isWithinRange = isAscending ?
                            passengerLocation > elevatorLocation && passengerLocation < destination :
                            passengerLocation < elevatorLocation && passengerLocation > destination;
                        
                        if (isWithinRange) {
                            return floorRequest;
                        }
                    }
    
                } else {
                    if (passengerDestination <= elevatorLocation && passengerDestination >= destination) {
                        boolean isWithinRange = isAscending ?
                            passengerLocation > elevatorLocation && passengerLocation < destination :
                            passengerLocation < elevatorLocation && passengerLocation > destination;
                        
                        if (isWithinRange) {
                            return floorRequest;
                        }
                    }
                }
            }
        }

        return null;
    }

    /**
     * Scans the todo list of floors to
     * visit and chooses the most suitable floor
     * to traverse to based on the elevator placement
     * and current direction.
     * @return the element index of the best to do
     */
    public synchronized int findClosestTask() {
    	int[] eligibilityTable = new int[todoList.size()];
    	
    	//Iterating through list of candidates 
    	for (int task = 0; task < eligibilityTable.length; task++) {
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

    private String getName() {
        return "ElevatorController-" + this.elevatorPort;
    }

    @Override
    public void run() {
        try {
            while (this.elevatorInfo.isElevatorBroken() == false) {
                boolean hasFloorRequests = !this.todoList.isEmpty();
                if (hasFloorRequests) {
                    FloorRequest floorRequest = this.todoList.remove(this.findClosestTask());
                    this.journey.add(floorRequest);
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    ObjectOutput objectOutput = new ObjectOutputStream(outputStream);
                    objectOutput.writeObject(floorRequest);
                    objectOutput.close();

                    byte[] sendBytes = outputStream.toByteArray();
                    this.sendToElevator(sendBytes);

                    // track progress as it reaches passenger floor
                    this.elevatorInfo.setAscending(this.elevatorInfo.getCurrentFloor() > floorRequest.getFloor());
                    this.elevatorInfo.setOnStandby(false);
                    this.trackDoors();
                    this.trackLocation(this.elevatorInfo.getCurrentFloor(), floorRequest.getFloor());            
                    this.trackDoors();
                    // System.out.println("[" + this.getName() + "]: elevator picked up passengers");

                    // track progress as it reaches destination
                    this.elevatorInfo.setAscending(this.elevatorInfo.getCurrentFloor() > floorRequest.getDestination());
                    this.trackDoors();
                    this.trackLocation(this.elevatorInfo.getCurrentFloor(), floorRequest.getDestination());            
                    this.trackDoors();
                    // System.out.println("[" + this.getName() + "]: elevator reached destination");

                    this.elevatorInfo.setOnStandby(true);
                }
    
                Thread.sleep(100);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
