package shared;
import java.lang.Math;
import java.util.Random;

/**
 * Engine class which represents the
 * motor and all the accompanying
 * Phenomena of an elevator motor.
 * Primarily the delay of moving between
 * floors. 
 *
 * @author Abdul Kazal
 */
public class Engine {
	
	private double firstFloorMedianDelay;
	private double firstFloorStd;
	private double topSpeedMedianDelay;
	private double topSpeedStd;
	private double rampingUpDelay;
	private int maxSpeedDelay;
	private Random rng = new Random();
	
	/**
	 * The constructor that specifies all the 
	 * specs of the engine's ability. The unit for all
	 * double parameters is in seconds and the byte parameter
	 * is in the unit of floors. 
	 * @param firstFloorD Median time to travel first floor. 
	 * @param firstFloorS Standard deviation of firstFloorD median. 
	 * @param maxDelay The number of floors it takes for the elevator to reach top speed.
	 * @param topSpeed Median time to travel when engine at top speed.
	 * @param topSpeedD Standard deviation for topSpeed.
	 * @Param rampUpD The median time it takes to travel between floors between start and max speed
	 * (All speeds and deviations are in the unit of seconds/floor)
	 * Note: 'D' stands for delay and 'S' stands for standard deviation.
	 */
	public Engine(double firstFloorD, double firstFloorS,
			int maxDelay, double topSpeed, double topSpeedD, double rampUpD) {
		firstFloorMedianDelay = firstFloorD;
		firstFloorStd = firstFloorS;
		maxSpeedDelay = maxDelay;
		topSpeedMedianDelay = topSpeed;
		topSpeedStd = topSpeedD;
		rampingUpDelay = rampUpD;
	}
	
	/**
	 * Method to add the real time delay of
	 * an elevator car traveling throughout 
	 * the building. 
	 * 
	 * @param startFloor which floor the car starts moving from
	 * @param destinationFloor the destination floor of the car
	 * 
	 * @return The value in seconds of the delay. (To be handled by elevator)
	 */
	public double traverseFloors(int startFloor, int destinationFloor) {
		double delayInSeconds = 0;
	
		//Getting difference 
		int floorGap = Math.abs(startFloor - destinationFloor);

		//Elevator will not hit top speed during this trip
		//First floor delay 
		delayInSeconds += generateDelay(firstFloorMedianDelay, firstFloorStd);
		//Speeding up floors delay
		for (int floor = 0; floor < maxSpeedDelay-1; floor++) { // -1 to account for the first floor delay since already acoounted for
			delayInSeconds += generateDelay(rampingUpDelay, 0); //Did not add standard deviation to acceleration factor due to complexity
		}
		
		//Returning if max speed cannot be achieved 
		if (floorGap - maxSpeedDelay <= 0 ) {
			return delayInSeconds;
		}
		
		//Max speed floors delay
		for (int floor = 0; floor < floorGap - maxSpeedDelay; floor++) {
			delayInSeconds += generateDelay(topSpeedMedianDelay, topSpeedStd);
		}
		return delayInSeconds;
	}
	
	//Helper functions 
	
	/**
	 * Calculates with randomization the delay of traversing 
	 * the first floor of the elevator trip. 
	 * @param the median measure to calculate the delay from
	 * @return a randomized time in seconds of passed measure time to traverse. 
	 */
	private double generateDelay(double measureMedian, double measureDeviation) {
		//Creating value
		double delay = 0;
		//Adding the baseline value
		delay += measureMedian;
		//Adding random standard deviation 
		double deviation = measureDeviation *  rng.nextGaussian() *  (int) Math.pow(-1, rng.nextInt(100));
		delay += deviation;
		return delay;
	}
	
	
	
	
}
