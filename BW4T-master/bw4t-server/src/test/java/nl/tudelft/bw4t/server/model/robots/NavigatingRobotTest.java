package nl.tudelft.bw4t.server.model.robots;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import nl.tudelft.bw4t.map.BlockColor;
import nl.tudelft.bw4t.map.NewMap;
import nl.tudelft.bw4t.map.Zone;
import nl.tudelft.bw4t.map.view.ViewBlock;
import nl.tudelft.bw4t.map.view.ViewEntity;
import nl.tudelft.bw4t.server.model.BW4TServerMap;
import nl.tudelft.bw4t.server.model.blocks.Block;
import nl.tudelft.bw4t.server.model.doors.Door;
import nl.tudelft.bw4t.server.model.robots.NavigatingRobot.State;
import nl.tudelft.bw4t.server.model.robots.handicap.GripperHandicap;
import nl.tudelft.bw4t.server.model.robots.handicap.Human;
import nl.tudelft.bw4t.server.model.robots.handicap.IRobot;
import nl.tudelft.bw4t.server.model.zone.Corridor;
import nl.tudelft.bw4t.server.model.zone.Area;
import repast.simphony.context.Context;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.grid.Grid;

@RunWith(MockitoJUnitRunner.class)
public class NavigatingRobotTest {

	@Mock
	private ContinuousSpace<Object> space;

	@Mock
	private Grid<Object> grid;

	@Mock
	private Context<Object> context;

	private nl.tudelft.bw4t.server.model.zone.Zone mockedZone = Mockito
			.mock(nl.tudelft.bw4t.server.model.zone.Zone.class);
	private nl.tudelft.bw4t.server.model.zone.Zone mockedOtherZone = Mockito
			.mock(nl.tudelft.bw4t.server.model.zone.Zone.class);

	private Door mockedDoor = Mockito.mock(Door.class);
	private Area mockedArea = Mockito.mock(Area.class);
	private Corridor mockedCorridor = Mockito.mock(Corridor.class);
	// private BW4TEnvironment mockedEnv = Mockito.mock(BW4TEnvironment.class);
	// private MoveType mockedMoveType = Mockito.mock(MoveType.class);

	private NavigatingRobot bot;
	private NavigatingRobot bot2;
	private NavigatingRobot bot3;
	private NavigatingRobot bot4;
	private NavigatingRobot bot5;
	private NavigatingRobot bot6;

	private Block b;
	@Mock
	private NewMap map;
	private BW4TServerMap smap;

	@Before
	public void setup() {
		smap = spy(new BW4TServerMap(map, context));
		when(smap.getContinuousSpace()).thenReturn(space);
		when(smap.getGridSpace()).thenReturn(grid);

		int cap = 2;
		int cap2 = 1;
		bot = new NavigatingRobot("Bot1", smap, true, cap);
		bot2 = new NavigatingRobot("Bot2", smap, false, cap2);
		bot3 = new NavigatingRobot("Bot3", smap, true, cap2);
		bot4 = null;
		bot5 = new NavigatingRobot("Bot5", smap, true, cap2);
		bot6 = new NavigatingRobot("Bot5", smap, true, cap2);

		b = new Block(BlockColor.BLUE, smap);
	}

	@Test
	public void getStateCollidedTest() {
		bot.setCollided(true);
		assertEquals(bot.getState(), State.COLLIDED);
	}

	@Test
	public void getStateTravelingTest() throws NoSuchFieldException,
			SecurityException, IllegalArgumentException, IllegalAccessException {
		double point = 0.5;
		NdPoint ppoint = new NdPoint(point);
		Field move = NavigatingRobot.class.getDeclaredField("currentMove");
		move.setAccessible(true);
		move.set(bot, ppoint);
		assertEquals(bot.getState(), State.TRAVELING);
	}

	@Test
	public void getStateOtherTest() {
		assertEquals(bot.getState(), State.ARRIVED);
	}

	@Test
	public void getNameTest() {
		assertEquals(bot.getName(), "Bot1");
	}

	@Test
	public void disconnectTest() {
		bot.disconnect();
		assertEquals(bot.isConnected(), false);
	}

	@Test
	public void equalseTest() {
		Zone z = new Zone();
		assertEquals(false, bot.equals(bot2));
		assertEquals(false, bot.equals(bot3));
		assertEquals(false, bot5.equals(bot));
		assertEquals(false, bot.equals(bot4));
		assertEquals(false, bot.equals(z));

		assertEquals(true, bot.equals(bot));
		assertEquals(true, bot5.equals(bot6));
	}

	@Test
	public void hashCodeTest() {
		assertEquals(bot.hashCode(), 1);
	}

	@Test
	public void isHoldingTest() {
		List<Block> emptyList = new ArrayList<Block>();
		;
		assertEquals(bot.getHolding().isEmpty(), emptyList.isEmpty());
	}

	@Test
	public void canPickUpTest() {
		assertEquals(bot.canPickUp(bot2), false);
		assertEquals(bot.canPickUp(b), true);
	}

	@Test
	public void pickUpTest() {
		assertEquals(b.isFree(), true);
		bot.pickUp(b);
		assertEquals(b.getHeldBy(), bot);
	}

	@Test
	public void checkZoneAccessTest() {
		MoveType type = bot.checkZoneAccess(mockedZone, mockedZone, mockedDoor);
		assertEquals(type, MoveType.SAME_AREA);

		type = bot.checkZoneAccess(mockedZone, mockedOtherZone, mockedDoor);
		assertEquals(type, MoveType.ENTERING_FREESPACE);

		type = bot.checkZoneAccess(mockedZone, mockedArea, mockedDoor);
		assertEquals(type, MoveType.HIT_CLOSED_DOOR);

		type = bot.checkZoneAccess(mockedZone, mockedArea, null);
		assertEquals(type, MoveType.HIT_WALL);

		when(mockedArea.containsMeOrNothing(bot)).thenReturn(true);
		type = bot.checkZoneAccess(mockedZone, mockedArea, mockedDoor);
		assertEquals(type, MoveType.ENTERING_ROOM);

		type = bot.checkZoneAccess(mockedZone, mockedCorridor, mockedDoor);
		assertEquals(type, MoveType.HIT_OCCUPIED_ZONE);

		when(mockedCorridor.containsMeOrNothing(bot)).thenReturn(true);
		type = bot.checkZoneAccess(mockedZone, mockedCorridor, mockedDoor);
		assertEquals(type, MoveType.ENTER_CORRIDOR);
	}

	@Test
	public void clearCollidedTest() {
		bot.setCollided(true);
		assertEquals(bot.isCollided(), true);

		bot.clearCollided();
		assertEquals(bot.isCollided(), false);
	}

	@Test
	public void isOneBotPerZoneTest() {
		assertEquals(true, bot.isOneBotPerZone());
		assertEquals(false, bot2.isOneBotPerZone());
	}

	@Test
	public void getSizeTest() {
		bot.setSize(2);
		assertEquals(bot.getSize(), 2);
	}

	@Test
	public void getViewTest() {
		when(space.getLocation(bot)).thenReturn(new NdPoint(1, 1));
		Stack<nl.tudelft.bw4t.map.view.ViewBlock> bs = new Stack<ViewBlock>();
		bs.add(b.getView());

		ViewEntity ent = new ViewEntity(bot.getId(), "Bot1", 1, 1, bs,
				bot.getSize());

		bot.pickUp(b);
		assertEquals(bot.getView().getId(), ent.getId());
		assertEquals(bot.getView().getName(), ent.getName());
		assertEquals(bot.getView().getLocation(), ent.getLocation());
		assertEquals(bot.getView().getHolding(), ent.getHolding());
		assertEquals(bot.getView().getRobotSize(), ent.getRobotSize());
	}

	@Test
	public void getAgentRecordTest() {
		AgentRecord record = new AgentRecord("Bot1");
		assertEquals(bot.getAgentRecord().getName(), record.getName());
		assertEquals(bot.getAgentRecord().getGoodDrops(), record.getGoodDrops());
		assertEquals(bot.getAgentRecord().getNMessages(), record.getNMessages());
		assertEquals(bot.getAgentRecord().getNAreasEntered(),
				record.getNAreasEntered());
		assertEquals(bot.getAgentRecord().getTotalStandingStillMillis(),
				record.getTotalStandingStillMillis());
		assertEquals(bot.getAgentRecord().getWrongDrops(),
				record.getWrongDrops());
	}

	@Test
	public void getBatteryTest() {
		Battery testBattery = new Battery(100, 100, 0);
		bot.setBattery(testBattery);
		assertEquals(bot.getBattery(), testBattery);
	}

	@Test
	public void getParentTest() {
		assertNull(bot.getParent());
	}

	@Test
	public void getHandicapsListTest() {
		assertEquals(bot.getHandicapsList().size(), 0);
		IRobot r = new GripperHandicap(bot);
		assertTrue(r.getHandicapsList().contains("Gripper"));
		assertEquals(bot.getHandicapsList().size(), 1);
	}

	@Test
	public void getGripperCapacityTest() {
		bot.setGripperCapacity(5);
		assertEquals(bot.getGripperCapacity(), 5);
	}

	@Test
	public void getSpeedModTest() {
		bot.setSpeedMod(0.821);
		assertEquals(0.821, bot.getSpeedMod(), 0.01);
	}

	@Test
	public void isHumanTest() {
		assertFalse(bot.isHuman());
		IRobot r = new Human(bot);
		assertTrue(r.getHandicapsList().contains("Human"));
		assertTrue(bot.isHuman());
	}

	@Test
	public void getEPartnerTest() {
		assertNull(bot.getEPartner());
	}

	@Test
	public void isHoldingEPartnerTest() {
		assertFalse(bot.isHoldingEPartner());
	}

	@Test
	public void distanceToTest() {
		assertEquals(0.0, bot.distanceTo(bot2), 0.01);
	}
}
