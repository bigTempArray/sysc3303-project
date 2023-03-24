/**
 * 
 */
package tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import scheduler.CarInstance;

/**
 * @author mayil
 *
 */
class CarInstanceTest {
	private CarInstance car;
	/**
	 * 
	 */
	@BeforeEach
	void setUp() throws Exception {
		 car = new CarInstance();
	}
	/**
	 * 
	 */
	@Test
	void testSetGetCurrentFloor() {
		car.setCurrentFloor(2);
		assertEquals(2,car.getCurrentFloor());
	}

	

	/**
	 * 
	 */
	@Test
	void testIsAscending() {
		assertFalse(car.isAscending());
		
	}

	/**
	 *
	 */
	@Test
	void testSetAscending() {
		car.setAscending(true);
		assertTrue(car.isAscending());
	}

	/**
	 * 
	 */
	@Test
	void testIsOnStandby() {
		assertTrue(car.isOnStandby());
		
	}

	/**
	 * 
	 */
	@Test
	void testSetOnStandby() {
		car.setOnStandby(true);
		assertTrue(car.isOnStandby());
	}

	
	

	/**
	 * 
	 */
	@Test
	void testSetPortNumber() {
		car.setPortNumber(10);
		assertEquals(10,car.getPortNumber());
	}

}
