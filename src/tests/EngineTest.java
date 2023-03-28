/**
 * 
 */
package tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import shared.Engine;


/**
 * @author abdul
 *
 */
public class EngineTest {

    @Test
    public void BasicEngineTravel(){
    	Engine eng =new Engine(10, 1, 3, 2, 1, 4);
        assertNotEquals(0,Math.floor(eng.traverseFloors(2, 4)));
    }
    

}
