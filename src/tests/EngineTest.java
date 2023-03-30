package tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import shared.Engine;


/**
 * @author Abdul
 * 
 * (Black box testing methodology used)
 */
public class EngineTest {
	
	Engine eng;

    @BeforeEach
    public void DefaultEngineTravel(){
    	eng = new Engine(10, 1, 3, 2, 1, 4);
    }
    
    @Test
    @DisplayName("Validating engine usability")
    public void usableCheck() {
    	double result = eng.traverseFloors(1, 5);
    	assertTrue(result != 0.0d);
    }
    
    @Test
    @DisplayName("Functional with decending paramenters")
    public void usableDescendingCheck() {
    	double result = eng.traverseFloors(5, 1);
    	assertTrue(result != 0.0d);
    }
    
    @Test
    @DisplayName("Ensuring dynamic results")
    public void seeIfRandom() {
    	double result1 = eng.traverseFloors(1, 2);
    	double result2 = eng.traverseFloors(1, 2);
    	assertNotEquals(result1, result2);
    }
    
    @Test
    @DisplayName("Higher distances yield higher delays")
    public void sensicalReturns() {
    	double oneFloor = eng.traverseFloors(4, 5);
    	double manyFloors = eng.traverseFloors(4, 12);
    	assertTrue(oneFloor < manyFloors);
    }
    
    @Test
    @DisplayName("Same trip distances give similiar results")
    public void congruentCalls() {
    	double lowAltitudeTrip = eng.traverseFloors(3, 9);
    	double highAltitudeTrip = eng.traverseFloors(12, 18);
    	int greaterThanInstances = 0;
    	
    	for (int trials = 0; trials < 50; trials++) {
    		lowAltitudeTrip = eng.traverseFloors(3, 9);
    		highAltitudeTrip = eng.traverseFloors(12, 18);
    		if (lowAltitudeTrip > highAltitudeTrip) {
    			greaterThanInstances += 1;
    		}
    	}
    	
    	if (greaterThanInstances >= 48) {fail();} //Means that one trip is consistently longer than the other which should not be 
    	assertTrue(true);
    }

    @Test 
    @DisplayName("Longer trips consistently take longer than shorter ones")
    public void longTriperLonger() {
    	double shortTrip = eng.traverseFloors(1,2);
        double longTrip = eng.traverseFloors(1,10);

        for (int trials = 0; trials < 50; trials++){
            shortTrip = eng.traverseFloors(1,2);
            longTrip = eng.traverseFloors(1,10);
            if (shortTrip > longTrip){fail();}
        }
        assertTrue(true);
    }

    @Test
    @DisplayName("Slightly shorter trips might take longer")
    public void occasionalLongerWait() {
    	double shortTrip = eng.traverseFloors(1,2);
        double longTrip = eng.traverseFloors(1,4);
        int counter = 0;
        
        for (int trials = 0; trials < 50; trials++){
            shortTrip = eng.traverseFloors(1,2);
            longTrip = eng.traverseFloors(1,3);
            if ((int)shortTrip > (int)longTrip){
            	counter++;
            }
        if (counter > 35) {fail();}
        assertTrue(true);
        } 
    }
    
    @Test
    @DisplayName("Differences of longer trips always higher than shorter ones")
    public void consistentDifferences() {
    	double shortA;
    	double shortB;
    	double longA;
    	double shortC;
    	
    	for (int trial = 0; trial < 50; trial++) {
    		shortA = eng.traverseFloors(6, 7);
    		shortB = eng.traverseFloors(8, 10);
    		shortC = eng.traverseFloors(3,2);
    		longA = eng.traverseFloors(1, 10);
    		if (Math.abs(shortA - shortB) > Math.abs(longA - shortC)){fail();}
    	}
    	assertTrue(true);
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
}