package nl.tudelft.bw4t.map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import nl.tudelft.bw4t.map.Door.Orientation;

/**
 * The class <code>DoorTest</code> contains tests for the class
 * <code>{@link Door}</code>.
 */
public class DoorTest {
    /**
     * Run the Door() constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 25.05.14 13:12
     */
    @Test
    public void testDoor() throws Exception {
        Door result = new Door();

        // add additional test code here
        assertNotNull(result);
        assertEquals("Door[Point2D.Double[0.0, 0.0],HORIZONTAL]",
                result.toString());
    }

    /**
     * Run the Door(Point,Orientation) constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 25.05.14 13:12
     */
    @Test
    public void testDoor_withParam() throws Exception {
        Point pos = new Point(1.0, 1.0,1.0);
        Door.Orientation or = Door.Orientation.VERTICAL;

        Door result = new Door(pos, or);

        // add additional test code here
        assertNotNull(result);
        assertEquals("Door[Point2D.Double[1.0, 1.0],VERTICAL]",
                result.toString());
    }

    /**
     * Run the Door.Orientation getOrientation() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 25.05.14 13:12
     */
    @Test
    public void testGetOrientation_1() throws Exception {
        Door fixture = new Door(new Point(), Door.Orientation.HORIZONTAL);

        Door.Orientation result = fixture.getOrientation();

        // add additional test code here
        assertNotNull(result);
        assertEquals(Door.Orientation.HORIZONTAL, result);

        fixture.setOrientation(Orientation.VERTICAL);

        result = fixture.getOrientation();

        assertEquals(Door.Orientation.VERTICAL, result);
    }

    /**
     * Run the Point getPosition() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 25.05.14 13:12
     */
    @Test
    public void testGetPosition_1() throws Exception {
        Door fixture = new Door(new Point(), Door.Orientation.HORIZONTAL);

        Point result = fixture.getPosition();

        // add additional test code here
        assertNotNull(result);
        assertEquals(0.0, result.getX(), 0.1);
        assertEquals(0.0, result.getY(), 0.1);

        fixture.setPosition(new Point(1, 1, 1));

        result = fixture.getPosition();

        // add additional test code here
        assertNotNull(result);
        assertEquals(1.0, result.getX(), 0.1);
        assertEquals(1.0, result.getY(), 0.1);
    }

    /**
     * Launch the test.
     * 
     * @param args
     *            the command line arguments
     * 
     * @generatedBy CodePro at 25.05.14 13:12
     */
    public static void main(String[] args) {
        new org.junit.runner.JUnitCore().run(DoorTest.class);
    }
}