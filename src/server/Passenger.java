package server;

import java.io.Serializable;

public class Passenger implements Serializable{

    private static final long serialVersionUID = 1L;
	private String time;
    private int floor;
    private String floorButton;
    private int CarButton;
/***
 * Default constructor to create a passenger
 * @param time string holding the time that the passenger arrived
 * @param floor int for the floor number the passenger is on
 * @param floorButton String for the direction the person is choosing
 * @param CarButton int for the destination floor
 */

    public Passenger(String time, int floor, String floorButton, int CarButton){
        this.setTime(time);
        this.setFloor(floor);
        this.setFloorButton(floorButton);
        this.setCarButton(CarButton);

    }
    /**
     * @return the time
     */
    public String getTime() {
        return time;
    }
    /**
     * @param time the time to set
     */
    public void setTime(String time) {
        this.time = time;
    }
    /**
     * @return the floorButton
     */
    public String getFloorButton() {
        return floorButton;
    }
    /**
     * @param floorButton the floorButton to set
     */
    public void setFloorButton(String floorButton) {
        this.floorButton = floorButton;
    }
    /**
     * @return the floor
     */
    public int getFloor() {
        return floor;
    }
    /**
     * @param floor the floor to set
     */
    public void setFloor(int floor) {
        this.floor = floor;
    }
    /**
     * @return the carButton
     */
    public int getCarButton() {
        return CarButton;
    }
    /**
     * @param carButton the carButton to set
     */
    public void setCarButton(int carButton) {
        CarButton = carButton;
    }
    public String toString() {
        return
            "Time: " + this.time +
            "\nFloor: " + this.floor +
            "\nFloor button: " + this.floorButton + 
            "\nCar button: " + this.CarButton +
            "\n-------------";

    }

}
