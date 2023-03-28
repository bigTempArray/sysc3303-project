package scheduler;

public class ElevatorInfo {
	private int currentFloor;
	private boolean ascending; // true = up, false = down
	private boolean onStandby;
	private int portNumber;
	private boolean isDoorsBroken;
	private boolean isElevatorBroken;

	public ElevatorInfo() {
		currentFloor = 0;
		ascending = false;
		onStandby = true;
		isDoorsBroken = false;
		isElevatorBroken = false;
	}

	// Basic get and set methods
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

	public void setDoorsBroken(boolean isDoorsBroken) {
		this.isDoorsBroken = isDoorsBroken;
	}

	public boolean isDoorsBroken() {
		return this.isDoorsBroken;
	}

	public void setElevatorBroken(boolean isElevatorBroken) {
		this.isElevatorBroken = isElevatorBroken;
	}

	public boolean isElevatorBroken() {
		return this.isElevatorBroken;
	}
}
