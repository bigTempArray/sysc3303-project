package shared;

import java.io.Serializable;

public class Passenger implements Serializable{

    private static final long serialVersionUID = 1L;
	private int time;
    private int floor;
    private int CarButton;
/***
 * Default constructor to create a passenger
 * @param time string holding the time that the passenger arrived
 * @param floor int for the floor number the passenger is on
 * @param floorButton String for the direction the person is choosing
 * @param CarButton int for the destination floor
 */

    public Passenger(int time, int floor, int CarButton){
        this.setTime(time);
        this.setFloor(floor);
        this.setCarButton(CarButton);

    }
    /**
     * @return the time
     */
    public int getTime() {
        return time;
    }
    /**
     * @param time the time to set
     */
    public void setTime(int time) {
        this.time = time;
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
            "{" + "\n" +
            "   Time: " + this.time + ",\n" +
            "   Floor: " + this.floor + ",\n" +
            "   Car button: " + this.CarButton + "\n" + 
            "}";
    }

}
