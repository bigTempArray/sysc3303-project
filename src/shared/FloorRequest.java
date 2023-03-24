package shared;

import java.io.Serializable;

public class FloorRequest implements Serializable {

    private static final long serialVersionUID = 1L;
    private int time;
    private int floor;
    private int destination;

    /***
     * Default constructor to create a passenger
     * 
     * @param time        string holding the time that the passenger arrived
     * @param floor       int for the floor number the passenger is on
     * @param destination   int for the destination floor
     */

    public FloorRequest(int time, int floor, int destination) {
        this.setTime(time);
        this.setFloor(floor);
        this.setDestination(destination);

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
    public int getDestination() {
        return destination;
    }

    /**
     * @param carButton the carButton to set
     */
    public void setDestination(int carButton) {
        destination = carButton;
    }

    public String toString() {
        return "{" + "\n" +
                "   Time: " + this.time + ",\n" +
                "   Floor: " + this.floor + ",\n" +
                "   Car button: " + this.destination + "\n" +
                "}";
    }

}
