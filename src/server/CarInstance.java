package server;

public class CarInstance {
	private int currentFloor;
	private boolean ascending; //true = up, false = down
	private boolean onStandby;
	private int portNumber;
	
	public CarInstance() {
		currentFloor = 0;
		ascending = false;
		onStandby = true; 
	}
	
	//Basic get and set methods 
	public int getCurrentFloor() {
		return currentFloor;
	}

	public void setCurrentFloor(int currentFloor) {
		this.currentFloor = currentFloor;
	}

	public boolean isAscending() {
		return ascending;
	}

	public void setAscending(boolean ascending) {
		this.ascending = ascending;
	}

	public boolean isOnStandby() {
		return onStandby;
	}

	public void setOnStandby(boolean onStandby) {
		this.onStandby = onStandby;
	}

	public int getPortNumber() {
		return portNumber;
	}

	public void setPortNumber(int portNumber) {
		this.portNumber = portNumber;
	}
	
}
