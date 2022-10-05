package nl.tudelft.bw4t.map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * The class <code>ZoneTest</code> contains tests for the class
 * <code>{@link Zone}</code>.
 */
public class ZoneTest {
	/**
	 * Run the Zone() constructor test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 25.05.14 16:25
	 */
	@Test
	public void testZone() throws Exception {

		Zone result = new Zone();
		result.setType(Zone.Type.ROOM);

		// add additional test code here
		assertNotNull(result);
		assertEquals(
				"Zone[null,java.awt.geom.Rectangle2D$Double[x=0.0,y=0.0,w=0.0,h=0.0],ROOM,[],[],[]]",
				result.toString());
		assertEquals(null, result.getName());
		assertEquals(null, result.getRenderOptions());
	}

	/**
	 * Run the Zone(String,Rectangle,Type) constructor test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 25.05.14 16:25
	 */
	@Test
	public void testZone_withParam() throws Exception {
		String nm = "AwesomeArea";
		Rectangle bbox = new Rectangle();
		Zone.Type t = Zone.Type.CORRIDOR;

		Zone result = new Zone(nm, bbox, t);

		// add additional test code here
		assertNotNull(result);
		assertEquals(nm, result.getName());
		assertNull(result.getRenderOptions());
		assertEquals(bbox, result.getBoundingbox());
	}

	/**
	 * Run the void addBlock(BlockColor) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 25.05.14 16:25
	 */
	@Test
	public void testAddBlock_1() throws Exception {
		Zone fixture = new Zone();

		assertEquals(0, fixture.getBlocks().size());

		List<BlockColor> list = new ArrayList<>(7);
		list.add(BlockColor.BLUE);
		list.add(BlockColor.GREEN);
		list.add(BlockColor.ORANGE);
		list.add(BlockColor.PINK);
		list.add(BlockColor.RED);
		list.add(BlockColor.WHITE);
		list.add(BlockColor.YELLOW);
		fixture.setBlocks(list);

		assertEquals(7, fixture.getBlocks().size());

		BlockColor block = BlockColor.BLUE;

		fixture.addBlock(block);

		assertEquals(8, fixture.getBlocks().size());
	}

	/**
	 * Run the void addDoor(Door) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 25.05.14 16:25
	 */
	@Test
	public void testAddDoor_1() throws Exception {
		Zone fixture = new Zone();

		assertEquals(0, fixture.getDoors().size());

		fixture.setDoors(new ArrayList<Door>(1));

		assertEquals(0, fixture.getDoors().size());

		Door door = new Door();

		fixture.addDoor(door);

		assertEquals(1, fixture.getDoors().size());
		assertTrue(fixture.getDoors().contains(door));
	}

	/**
	 * Run the void addNeighbour(Zone) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 25.05.14 16:25
	 */
	@Test
	public void testAddNeighbour_1() throws Exception {
		Zone fixture = new Zone();

		assertEquals(0, fixture.getNeighbours().size());

		fixture.setNeighbours(new ArrayList<Zone>(1));

		assertEquals(0, fixture.getNeighbours().size());

		Zone zone = new Zone();

		fixture.addNeighbour(zone);

		assertEquals(1, fixture.getNeighbours().size());
		assertTrue(fixture.getNeighbours().contains(zone));
	}

	/**
	 * Run the Rectangle getBoundingbox() method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 25.05.14 16:25
	 */
	@Test
	public void testGetBoundingbox() throws Exception {
		Zone fixture = new Zone();

		Rectangle result = fixture.getBoundingbox();
		assertEquals(0.0, result.getHeight(), 0.1);
		assertEquals(0.0, result.getX(), 0.1);
		assertEquals(0.0, result.getY(), 0.1);
		assertEquals(0.0, result.getWidth(), 0.1);

		fixture.setBoundingbox(new Rectangle(2, 4, 1, 5));

		result = fixture.getBoundingbox();
		assertEquals(5.0, result.getHeight(), 0.1);
		assertEquals(2.0, result.getX(), 0.1);
		assertEquals(4.0, result.getY(), 0.1);
		assertEquals(1.0, result.getWidth(), 0.1);
	}

	/**
	 * Run the List<Door> getDoors() method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 25.05.14 16:25
	 */
	@Test
	public void testGetDoors() throws Exception {
		Zone fixture = new Zone();
		fixture.setDoors(new ArrayList<Door>(0));

		List<Door> result = fixture.getDoors();

		// add additional test code here
		assertNotNull(result);
		assertEquals(0, result.size());
	}

	/**
	 * Run the String getName() method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 25.05.14 16:25
	 */
	@Test
	public void testGetName_1() throws Exception {
		Zone fixture = new Zone();

		assertEquals(null, fixture.getName());

		String name = "HALO";
		fixture.setName(name);

		assertEquals(name, fixture.getName());
	}

	/**
	 * Run the RenderOptions getRenderOptions() method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 25.05.14 16:25
	 */
	@Test
	public void testGetRenderOptions_1() throws Exception {
		Zone fixture = new Zone();

		assertNull(fixture.getRenderOptions());

		RenderOptions ro = new RenderOptions();
		fixture.setRenderOptions(ro);

		RenderOptions result = fixture.getRenderOptions();

		// add additional test code here
		assertNotNull(result);
		assertEquals(ro, result);
	}

	/**
	 * Run the Zone.Type getType() method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 25.05.14 16:25
	 */
	@Test(expected = IllegalStateException.class)
	public void testGetType_1() throws Exception {
		Zone fixture = new Zone("Area", null, Zone.Type.ROOM);

		assertEquals(Zone.Type.ROOM, fixture.getType());

		fixture.setType(Zone.Type.CORRIDOR);
	}

	/**
	 * Run the void setBlocks(List<BlockColor>) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 25.05.14 16:25
	 */
	@Test
	public void testSetBlocks_1() throws Exception {
		Zone fixture = new Zone();

		assertEquals(0, fixture.getBlocks().size());

		List<BlockColor> list = new ArrayList<>(7);
		list.add(BlockColor.BLUE);
		list.add(BlockColor.GREEN);
		list.add(BlockColor.ORANGE);
		list.add(BlockColor.PINK);
		list.add(BlockColor.RED);
		list.add(BlockColor.WHITE);
		list.add(BlockColor.YELLOW);
		fixture.setBlocks(list);

		assertEquals(7, fixture.getBlocks().size());
		assertEquals(list, fixture.getBlocks());

		List<BlockColor> blocks = new ArrayList<>(7);
		blocks.add(BlockColor.BLUE);
		blocks.add(BlockColor.GREEN);
		blocks.add(BlockColor.ORANGE);
		blocks.add(BlockColor.PINK);
		blocks.add(BlockColor.RED);
		blocks.add(BlockColor.WHITE);
		blocks.add(BlockColor.YELLOW);

		fixture.setBlocks(blocks);

		assertEquals(7, fixture.getBlocks().size());
		assertEquals(blocks, fixture.getBlocks());
	}

	/**
	 * Run the String toString() method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 25.05.14 16:25
	 */
	@Test
	public void testToString_1() throws Exception {
		Zone fixture = new Zone("", new Rectangle(), Zone.Type.CORRIDOR);
		fixture.setDoors(new ArrayList<Door>(0));
		List<BlockColor> list = new ArrayList<>(7);
		list.add(BlockColor.BLUE);
		list.add(BlockColor.GREEN);
		list.add(BlockColor.ORANGE);
		list.add(BlockColor.PINK);
		list.add(BlockColor.RED);
		list.add(BlockColor.WHITE);
		list.add(BlockColor.YELLOW);
		fixture.setBlocks(list);
		fixture.setRenderOptions(new RenderOptions());
		fixture.setNeighbours(new ArrayList<Zone>(0));

		String result = fixture.toString();

		// add additional test code here
		assertEquals(
				"Zone[,java.awt.geom.Rectangle2D$Double[x=0.0,y=0.0,w=0.0,h=0.0],CORRIDOR,[],[BLUE, GREEN, ORANGE, PINK, RED, WHITE, YELLOW],[]]"
						.toLowerCase(), result.toLowerCase());
	}

	/**
	 * Run the String toString() method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 25.05.14 16:25
	 */
	@Test
	public void testToString_2() throws Exception {
		Zone fixture = new Zone("Corridor", null, Zone.Type.CORRIDOR);
		fixture.addNeighbour(new Zone("AreaB", new Rectangle(), Zone.Type.ROOM));

		String result = fixture.toString();

		// add additional test code here
		assertEquals("Zone[Corridor,null,CORRIDOR,[],[],[AreaB]]", result);
	}

	/**
	 * Perform pre-test initialization.
	 * 
	 * @throws Exception
	 *             if the initialization fails for some reason
	 * 
	 * @generatedBy CodePro at 25.05.14 16:25
	 */
	@Before
	public void setUp() throws Exception {
		// add additional set up code here
	}

	/**
	 * Perform post-test clean-up.
	 * 
	 * @throws Exception
	 *             if the clean-up fails for some reason
	 * 
	 * @generatedBy CodePro at 25.05.14 16:25
	 */
	@After
	public void tearDown() throws Exception {
		// Add additional tear down code here
	}

	/**
	 * Launch the test.
	 * 
	 * @param args
	 *            the command line arguments
	 * 
	 * @generatedBy CodePro at 25.05.14 16:25
	 */
	public static void main(String[] args) {
		new org.junit.runner.JUnitCore().run(ZoneTest.class);
	}
}