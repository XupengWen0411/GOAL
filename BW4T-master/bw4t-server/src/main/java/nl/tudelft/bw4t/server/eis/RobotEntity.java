package nl.tudelft.bw4t.server.eis;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import org.apache.log4j.Logger;
import org.omg.CORBA.Environment;

import eis.eis2java.annotation.AsAction;
import eis.eis2java.annotation.AsPercept;
import eis.eis2java.exception.TranslationException;
import eis.eis2java.translation.Filter;
import eis.eis2java.translation.Filter.Type;
import eis.eis2java.translation.Translator;
import eis.exceptions.ActException;
import eis.exceptions.AgentException;
import eis.exceptions.PerceiveException;
import eis.iilang.Action;
import eis.iilang.Parameter;
import nl.tudelft.bw4t.scenariogui.BotConfig;
import nl.tudelft.bw4t.server.eis.translators.BlockWithColorTranslator;
import nl.tudelft.bw4t.server.eis.translators.BoundedMovableObjectTranslator;
import nl.tudelft.bw4t.server.eis.translators.ColorTranslator;
import nl.tudelft.bw4t.server.eis.translators.EPartnerTranslator;
import nl.tudelft.bw4t.server.eis.translators.IdAndBooleanTranslator;
import nl.tudelft.bw4t.server.eis.translators.ObjectInformationTranslator;
import nl.tudelft.bw4t.server.eis.translators.PointTranslator;
import nl.tudelft.bw4t.server.eis.translators.ZoneTranslator;
import nl.tudelft.bw4t.server.environment.BW4TEnvironment;
import nl.tudelft.bw4t.server.model.BoundedMoveableInterface;
import nl.tudelft.bw4t.server.model.BoundedMoveableObject;
import nl.tudelft.bw4t.server.model.blocks.Block;
import nl.tudelft.bw4t.server.model.epartners.EPartner;
import nl.tudelft.bw4t.server.model.robots.AbstractRobot;
import nl.tudelft.bw4t.server.model.robots.NavigatingRobot;
import nl.tudelft.bw4t.server.model.robots.handicap.AbstractRobotDecorator;
import nl.tudelft.bw4t.server.model.robots.handicap.IRobot;
import nl.tudelft.bw4t.server.model.zone.BlocksArea;
import nl.tudelft.bw4t.server.model.zone.Corridor;
import nl.tudelft.bw4t.server.model.zone.DropZone;
import nl.tudelft.bw4t.server.model.zone.Area;
import nl.tudelft.bw4t.server.model.zone.Zone;
import nl.tudelft.bw4t.server.util.AreaLocator;
import nl.tudelft.bw4t.server.util.ZoneLocator;
import repast.simphony.context.Context;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.util.collections.IndexedIterable;

/**
 * EIS entity for a {@link AbstractRobot}. This puts EIS functionality on top of
 * the repast rpbot.
 */
public class RobotEntity implements EntityInterface {

	static {
		// Register our translators.
		Translator translator = Translator.getInstance();
		translator.registerJava2ParameterTranslator(new BlockWithColorTranslator());
		translator.registerJava2ParameterTranslator(new BoundedMovableObjectTranslator());
		translator.registerJava2ParameterTranslator(new ZoneTranslator());
		translator.registerJava2ParameterTranslator(new PointTranslator());
		translator.registerJava2ParameterTranslator(new ObjectInformationTranslator());
		translator.registerJava2ParameterTranslator(new ColorTranslator());
		translator.registerJava2ParameterTranslator(new EPartnerTranslator());
		translator.registerJava2ParameterTranslator(new IdAndBooleanTranslator());
	}

	/**
	 * The log4j logger, logs to the console.
	 */
	private static final Logger LOGGER = Logger.getLogger(RobotEntity.class);

	private final IRobot ourRobot;
	private final Context<Object> context;

	/**
	 * Here we store data that needs to be locked for a perception cycle. See
	 * {@link #initializePerceptionCycle()}.
	 */
	private Point2D ourRobotLocation;
	private Point2D spawnLocation;
	private Area ourRobotArea;

	/**
	 * each item in messages is a list with two items: the sender and the
	 * messagetext.
	 */
	private List<ArrayList<String>> messages = new LinkedList<>();

	/**
	 * Creates a new {@link RobotEntity} that can be launched by an EIS
	 * compatible {@link Environment}.
	 * 
	 * @param robot
	 *            The {@link AbstractRobot} that this entity can put up for
	 *            controlling in EIS.
	 */
	public RobotEntity(IRobot robot) {
		this.ourRobot = robot;
		this.context = ourRobot.getContext();
	}

	/**
	 * used for logging and testing. Bit hacky, our robot should not be exposed.
	 * 
	 * @return our repast robot object
	 */
	public IRobot getRobotObject() {
		return ourRobot;
	}

	/**
	 * Connect robot to repast (to be called when an agent is connected to this
	 * entity)
	 */
	@Override
	public void connect() {
		spawnLocation = new Point2D.Double(ourRobot.getLocation().getX(), ourRobot.getLocation().getY());
		ourRobot.connect();
	}

	/**
	 * Disconnects the robot from repast.
	 */
	@Override
	public void disconnect() {
		if (ourRobot.isConnected()) {
			// reset before disconnect: reset moves bot to init position.
			reset();
			ourRobot.disconnect();
			ourRobot.removeFromContext();
		}
	}

	/**
	 * This function should be called before perception cycle is started, so
	 * that we can lock the relevant data from the environment.
	 */
	@Override
	public void initializePerceptionCycle() {
		ourRobotLocation = new Point2D.Double(ourRobot.getLocation().getX(), ourRobot.getLocation().getY());
		ourRobotArea = AreaLocator.getAreaAt(ourRobotLocation.getX(), ourRobotLocation.getY());
	}

	/**
	 * Percepts for the sizes of all the robots.
	 * 
	 * @return size of the robot
	 */
	@AsPercept(name = "robotSize", multiplePercepts = true, filter = Filter.Type.ON_CHANGE)
	public List<ObjectInformation> getSizes() {
		IndexedIterable<Object> allRobots = context.getObjects(IRobot.class);
		List<ObjectInformation> sizes = new ArrayList<>(allRobots.size());
		for (Object obj : allRobots) {
			IRobot robot = (IRobot) obj;
			sizes.add(new ObjectInformation(robot.getSize(), robot.getSize(), robot.getId()));
		}

		return sizes;
	}

	/**
	 * Percepts for the location of rooms (incl. dropzone), robots, epartners,
	 * and the blocks. Send on change
	 * 
	 * @return postitions
	 */
	@AsPercept(name = "position", multiplePercepts = true, filter = Filter.Type.ON_CHANGE)
	public List<ObjectInformation> getLocations() {
		List<ObjectInformation> objects = new LinkedList<>();

		// Add the dropzone
		DropZone dropZone = (DropZone) context.getObjects(DropZone.class).get(0);
		objects.add(new ObjectInformation(dropZone));

		// Add rooms
		IndexedIterable<Object> allAreas = context.getObjects(BlocksArea.class);
		for (Object object : allAreas) {
			Area r = (Area) object;
			objects.add(new ObjectInformation(r));
		}

		// Add blocks
		for (Block block : getVisible(Block.class)) {
			objects.add(new ObjectInformation(block));
		}

		// Add EPartners
		for (EPartner ep : getVisible(EPartner.class)) {
			objects.add(new ObjectInformation(ep));
		}

		// Add Robots
		for (IRobot ep : getVisible(IRobot.class)) {
			objects.add(new ObjectInformation(ep.getSuperParent()));
		}

		// #2830 add robots own position
		objects.add(new ObjectInformation(ourRobotLocation.getX(), ourRobotLocation.getY(), ourRobot.getId()));

		return objects;
	}

	/**
	 * Percept for navpoints the robot is at. Send on change. If robot is in a
	 * {@link Zone}, that zone name is returned. If not, the nearest
	 * {@link Corridor} name is returned.
	 * 
	 * @return a list of blockID
	 * @throws PerceiveException
	 */
	@AsPercept(name = "at", multiplePercepts = false, filter = Filter.Type.ON_CHANGE)
	public String getAt() throws PerceiveException {

		Zone navpt = ZoneLocator.getNearestZone(ourRobot.getLocation());
		if (navpt == null) {
			throw new PerceiveException(
					"perceiving 'at' percept failed, because map has no suitable navpoint for position "
							+ ourRobotLocation);
		}
		return navpt.getName();
	}

	/**
	 * Percept for blocks the robot is at Send on change
	 * 
	 * @return a list of blockID of blocks within reach.
	 */
	@AsPercept(name = "atBlock", multiplePercepts = true, filter = Filter.Type.ON_CHANGE_NEG)
	public List<Long> getAtBlock() {
		List<Long> blocksInReach = new LinkedList<>();
		for (Object object : context.getObjects(Block.class)) {
			Block b = (Block) object;
			if (ourRobot.canPickUp(b)) {
				blocksInReach.add(b.getId());
			}
		}
		return blocksInReach;
	}

	/**
	 * Percept for the room that the player is in, null if not in a room. Send
	 * on change
	 * 
	 * @return room name
	 */
	@AsPercept(name = "in", multiplePercepts = false, filter = Filter.Type.ON_CHANGE_NEG)
	public String getArea() {

		if (ourRobotArea == null) {
			return null;
		}
		return ourRobotArea.getName();
	}

	/**
	 * Percept for the location of this robot Send on change
	 * 
	 * @return location
	 */
	@AsPercept(name = "location", multiplePercepts = false, filter = Filter.Type.ON_CHANGE)
	public Point2D getLocation() {
		return new Point2D.Double(ourRobotLocation.getX(), ourRobotLocation.getY());
	}

	/**
	 * Percept for the places in the world. Send at the beginning
	 * 
	 * @return rooms
	 */
	@AsPercept(name = "place", multiplePercepts = true, filter = Filter.Type.ONCE)
	public List<String> getAreas() {
		IndexedIterable<Object> objects = context.getObjects(Zone.class);
		List<String> places = new ArrayList<>(objects.size());
		for (Object o : objects) {
			Zone zone = (Zone) o;
			places.add(zone.getName());
		}

		return places;
	}

	/**
	 * how much the speed of the robot is multiplied by. Default is 0.5, which
	 * means the bot normally runs at 50% of the maximum speed.
	 * 
	 * @return id
	 */
	@AsPercept(name = "speed", filter = Filter.Type.ON_CHANGE)
	public double getSpeed() {
		return ourRobot.getSpeedMod();
	}

	/**
	 * Percept of the id of the robot Send at the beginning
	 * 
	 * @return id
	 */
	@AsPercept(name = "robot", filter = Filter.Type.ONCE)
	public long getRobot() {
		return ourRobot.getId();
	}

	/**
	 * The names of other players Send at the beginning. We are assuming that
	 * each agent controls one entity.
	 * 
	 * @return the names of the other players
	 */
	@AsPercept(name = "player", multiplePercepts = true, filter = Filter.Type.ON_CHANGE_NEG)
	public List<String> getPlayers() {
		BW4TEnvironment env = BW4TEnvironment.getInstance();
		List<String> agents = env.getAgents();
		List<String> result = new LinkedList<>();

		for (String agt : agents) {
			try {
				Set<String> entities = env.getAssociatedEntities(agt);
				if (!entities.contains(ourRobot.getName())) {
					result.addAll(entities);
				}
			} catch (AgentException e) {
				LOGGER.error("Ignoring an Agent's percept problem", e);
			}
		}
		return result;
	}

	/**
	 * The names of other players Send at the beginning
	 * 
	 * @return the names of the other players
	 */
	@AsPercept(name = "ownName", multiplePercepts = false, filter = Filter.Type.ONCE)
	public String getOwnName() {
		return ourRobot.getName();
	}

	/**
	 * Percept for the colors of all blocks that are visible. Send always. They
	 * are visible only when inside a room.
	 * 
	 * @return color
	 */
	@AsPercept(name = "color", multiplePercepts = true, filter = Filter.Type.ALWAYS)
	public List<BlockColor> getColor() {
		Set<Block> blocks = getVisible(Block.class);
		boolean isColorBlind = isColorBlind();

		List<BlockColor> colors = new ArrayList<>(blocks.size());
		for (Block block : blocks) {
			colors.add(new BlockColor(block, isColorBlind));
		}

		return colors;
	}

	/**
	 * Percept if the robot is holding something. Send if it becomes true, and
	 * send negated if it becomes false again.
	 * 
	 * @return holding block
	 */
	@AsPercept(name = "holding", multiplePercepts = true, filter = Filter.Type.ON_CHANGE_NEG)
	public List<Long> getHolding() {
		Stack<Block> holding = ourRobot.getHolding();
		List<Long> holds = new ArrayList<>(holding.size());
		for (Block b : holding) {
			holds.add(b.getId());
		}
		return holds;
	}

	/**
	 * Percept if the robot is holding something. Sent when the blocks that the
	 * robot holds changes.
	 * 
	 * @return holding block
	 */
	@AsPercept(name = "holdingblocks", filter = Filter.Type.ON_CHANGE)
	public List<Long> getHoldingBlocks() {
		// stack.toArray gives stack with top=LAST element. Need to reverse.
		// to reverse, we need to make copy first of the array.
		// Notice that collections.reverse is modifying the provided array!
		List<Block> blockstack = new ArrayList<>(ourRobot.getHolding());
		Collections.reverse(blockstack);
		List<Long> holds = new ArrayList<>(blockstack.size());
		for (Block b : blockstack) {
			holds.add(b.getId());
		}
		return holds;
	}

	/**
	 * Actual gripper capacity
	 * 
	 * @return max number of blocks the bot can hold. 0 if bot has gripper
	 *         handicap.
	 */
	@AsPercept(name = "gripperCapacity", filter = Filter.Type.ON_CHANGE)
	public Integer gripperCapacity() {
		return ourRobot.getGripperCapacity();
	}

	/**
	 * The sequence in which the blocks should be returned. Send at the
	 * beginning
	 * 
	 * @return sequence of blocks
	 */
	@AsPercept(name = "sequence", filter = Filter.Type.ONCE)
	public List<nl.tudelft.bw4t.map.BlockColor> getSequence() {
		DropZone dropZone = (DropZone) context.getObjects(DropZone.class).get(0);
		return dropZone.getSequence();
	}

	/**
	 * The index of the block that needs to be brought back now. Send on change
	 * 
	 * @return sequence index
	 */
	@AsPercept(name = "sequenceIndex", filter = Filter.Type.ON_CHANGE)
	public Integer getSequenceIndex() {
		DropZone dropZone = (DropZone) context.getObjects(DropZone.class).get(0);
		return dropZone.getSequenceIndex();
	}

	/**
	 * occupied percept, tells which rooms are occupied by robot. Send if true
	 * and send negated if no longer true
	 * 
	 * @return list of occupied room IDs
	 */
	@AsPercept(name = "occupied", multiplePercepts = true, filter = Filter.Type.ON_CHANGE_NEG)
	public List<String> getOccupied() {
		List<String> rooms = new LinkedList<>();
		for (Object r : context.getObjects(Area.class)) {
			Area room = (Area) r;
			if (room.getOccupier() != null) {
				rooms.add(room.getName());
			}
		}

		return rooms;

	}

	/**
	 * navpoint percept, tells which {@link Zone}s there are in the world and
	 * their neighbours. Send at the beginning
	 * 
	 * @return list of navpoints
	 */
	@AsPercept(name = "zone", multiplePercepts = true, filter = Filter.Type.ONCE)
	public List<Zone> getNavPoints() {
		IndexedIterable<Object> objects = context.getObjects(Zone.class);
		List<Zone> zones = new ArrayList<>(objects.size());
		for (Object o : objects ) {
			Zone zone = (Zone) o;
			zones.add(zone);
		}

		return zones;
	}

	/**
	 * Returns all messages received by the player, Send on change
	 * 
	 * @return the messages that were received
	 */
	@AsPercept(name = "message", multiplePercepts = true, filter = Filter.Type.ALWAYS)
	public List<ArrayList<String>> getMessages() {
		List<ArrayList<String>> msg = messages;
		messages = new LinkedList<>();
		return msg;
	}

	/**
	 * The current state of the robot. See {@link NavigatingRobot#getState()}.
	 * Send on change
	 * 
	 * @return state
	 */
	@AsPercept(name = "state", filter = Filter.Type.ON_CHANGE)
	public String getState() {
		return ourRobot.getState().toString().toLowerCase();
	}

	/**
	 * The battery level of the robot. The battery can be re-charged by moving
	 * through a charge zone. The battery use depends on the size and speed of
	 * the robot as set in the configuration. By default this is off. See also
	 * {@link BotConfig#calculateDischargeRate(int, int)}.
	 * 
	 * 
	 * @return batterypercentage
	 */
	@AsPercept(name = "battery", multiplePercepts = false, filter = Filter.Type.ON_CHANGE)
	public double getBatteryPercentage() {
		return ourRobot.getBattery().getPercentage();
	}

	/**
	 * The battery level of the robot. The battery can be re-charged by moving
	 * through a charge zone. The battery use depends on the size and speed of
	 * the robot as set in the configuration. By default this is off. See also
	 * {@link BotConfig#calculateDischargeRate(int, int)}.
	 * 
	 * 
	 * @return batterypercentage
	 */
	@AsPercept(name = "colorblind", multiplePercepts = false, filter = Filter.Type.ONCE)
	public boolean isColorBlind() {
		return ourRobot.getHandicapsList().contains("ColorBlind");
	}

	/**
	 * Instructs the robot to move to the given location.
	 * 
	 * @param x
	 *            The X coordinate of the location.
	 * @param y
	 *            The Y coordinate of the location.
	 */
	@AsAction(name = "goTo")
	public void goTo(double x, double y) {
		ourRobot.setTargetLocation(new NdPoint(x, y));
	}

	/**
	 * Instructs the robot to move to the given object. Only works if we are in
	 * the room containing the block - the block must be visible.
	 * 
	 * @param targetid
	 *            is the target object id. This can be the id of any object in
	 *            the map (not a free x,y location)
	 */
	@AsAction(name = "goToBlock")
	public void goTo(long targetid) {
		BoundedMoveableObject target = getBlock(targetid);

		ourRobot.setTarget(target);
	}

	/**
	 * Instructs the robot to move to the given navpoint
	 * 
	 * @param navPoint
	 *            , the navpoint the robot should move to
	 */
	@AsAction(name = "goTo")
	public void goTo(String navPoint) {
		Zone target = ZoneLocator.getZone(navPoint);
		if (target == null) {
			throw new IllegalArgumentException("unknown place " + navPoint);
		}
		ourRobot.setTarget(target);
	}

	/**
	 * Instructs the robot to pick up a block.
	 */
	@AsAction(name = "pickUp")
	public void pickUp(long blockId) {
		LOGGER.debug(String.format("%s is trying to pick up a block.", ourRobot.getName()));
		Block block = getBlock(blockId);

		LOGGER.debug(String.format("%s will pickup block %d.", ourRobot.getName(), block.getId()));
		if (!ourRobot.canPickUp(block))
			return;
		ourRobot.pickUp(block);
	}

	/**
	 * Instruct the robot to navigate the current obstacles.
	 */
	@AsAction(name = "navigateObstacles")
	public void navigateObstacles() {
		LOGGER.debug(String.format("%s is trying to navigate the following obstacles: ", ourRobot.getName()));
		for (BoundedMoveableObject obj : ourRobot.getObstacles()) {
			LOGGER.debug(obj + " at " + obj.getBoundingBox());
		}

		if (ourRobot instanceof NavigatingRobot) {
			NavigatingRobot navbot = (NavigatingRobot) ourRobot;
			navbot.navigateObstacles();
		} else if (ourRobot instanceof AbstractRobotDecorator) {
			IRobot robotEarliestParent = ourRobot.getEarliestParent();
			if (robotEarliestParent != null && robotEarliestParent instanceof NavigatingRobot) {
				NavigatingRobot navbot = (NavigatingRobot) robotEarliestParent;
				navbot.navigateObstacles();
			}
		}
	}

	/**
	 * Instructs the robot to send a message
	 * 
	 * @param message
	 *            , the message that should be sent
	 * @param receiver
	 *            , the receiver of the message (can be all or the id of another
	 *            robot
	 * @throws ActException
	 *             if the action fails
	 */
	@AsAction(name = "sendMessage")
	public void sendMessage(String receiver, String message) throws ActException {
		ourRobot.getAgentRecord().addSentMessage();

		// Translate the message into parameters
		Parameter[] parameters = new Parameter[2];
		try {
			parameters[0] = Translator.getInstance().translate2Parameter(ourRobot.getName())[0];
			parameters[1] = Translator.getInstance().translate2Parameter(message)[0];
		} catch (TranslationException e) {
			throw new ActException("translating of message failed:" + message, e);
		}

		// Send to all other entities (except self)
		if ("all".equals(receiver)) {
			for (String entity : BW4TEnvironment.getInstance().getEntities()) {
				BW4TEnvironment.getInstance().performClientAction(entity, new Action("receiveMessage", parameters));
			}
			// Send to a single entity
		} else {
			BW4TEnvironment.getInstance().performClientAction(receiver, new Action("receiveMessage", parameters));
		}
	}

	/**
	 * Instructs the robot to receive a certain message, should only be used
	 * internally in the server environment and therefore is not documented in
	 * the manual.
	 * 
	 * @param message
	 *            , the message to be received
	 * @param sender
	 *            , the sender of the message
	 */
	@AsAction(name = "receiveMessage")
	public void receiveMessage(String sender, String message) {
		// Add message to messageArray
		ArrayList<String> messageArray = new ArrayList<>(2);
		messageArray.add(sender);
		messageArray.add(message);

		messages.add(messageArray);
	}

	/**
	 * Instructs the robot to drop the block it is holding.
	 */
	@AsAction(name = "putDown")
	public void putDown() {
		ourRobot.drop();
	}

	/**
	 * Hack. Seems we need to get context through an entity... #1955
	 * 
	 * @return true if sequence is finished.
	 */
	@AsAction(name = "checkSequenceDone")
	public boolean checkSequenceDone() {
		DropZone dropzone = (DropZone) context.getObjects(DropZone.class).get(0);
		if (dropzone == null) {
			return false;
		}
		return dropzone.sequenceComplete();
	}

	/**
	 * Give the robot a list of all the EPartners in the zone.
	 * 
	 * @return the list of EPartners
	 */
	@AsPercept(name = "epartner", multiplePercepts = true, filter = Type.ALWAYS)
	public List<EPartner> getEPartners() {
		List<EPartner> eps = new LinkedList<>();
		for (EPartner ep : getVisible(EPartner.class)) {
			eps.add(ep);
		}
		EPartner mine = ourRobot.getEPartner();
		if (mine != null && !eps.contains(mine)) {
			eps.add(mine);
		}
		return eps;
	}

	/**
	 * Only available for the human: picks up the e-Partner.
	 */
	@AsAction(name = "pickUpEPartner")
	public void pickUpEPartner() {
		LOGGER.debug(String.format("%s is trying to pick up an e-partner.", ourRobot.getName()));

		EPartner nearest = getClosest(EPartner.class);
		if (nearest == null) {
			LOGGER.debug(String.format("%s can not pickup any e-partners.", ourRobot.getName()));
			return;
		}
		LOGGER.debug(String.format("%s will pickup e-partner %d.", ourRobot.getName(), nearest.getId()));
		ourRobot.pickUpEPartner(nearest);
	}

	/**
	 * Only available for the human: drops the e-Partner they are currently
	 * holding.
	 */
	@AsAction(name = "putDownEPartner")
	public void dropEPartner() {
		if (ourRobot.isHuman()) {
			ourRobot.dropEPartner();
		}
	}

	/**
	 * Send the robot a bumped percept, informing it that the path is blocked by
	 * the given robot.
	 * 
	 * @return Bump percept with the name of the robot in the way, if any.
	 */
	@AsPercept(name = "bumped", multiplePercepts = true, filter = Type.ON_CHANGE_NEG)
	public List<String> getBumped() {
		List<String> bumpedList = new LinkedList<>();
		if (ourRobot.isCollided() && !ourRobot.getObstacles().isEmpty()) {
			for (BoundedMoveableObject obj : ourRobot.getObstacles()) {
				if (obj instanceof IRobot) {
					IRobot bot = (IRobot) obj;
					bumpedList.add(bot.getName());
				}
			}
		}
		return bumpedList;
	}

	/**
	 * Percepts for when this robot has a target that is unreachable/reachable
	 * (on change).
	 * 
	 * @return The percept in the form of an object with an id (long) and a
	 *         boolean.
	 */
	@AsPercept(name = "oldTargetUnreachable", multiplePercepts = false, filter = Filter.Type.ON_CHANGE)
	public IdAndBoolean getOldTargetUnreachable() {
		return new IdAndBoolean(ourRobot.getId(), ourRobot.isDestinationUnreachable());
	}

	/********************* support funcs *********************/

	/**
	 * @return All bounded moveable objects of class T that are visible to the
	 *         robot. Excluding the one the robot is holding.
	 * @param <T>
	 * @param type
	 */
	private <T extends BoundedMoveableInterface> Set<T> getVisible(Class<T> type) {
		Set<T> set = new HashSet<>();

		if (context == null) {
			return set;
		}

		// Add all objects in the same room as the robot.
		Iterable<Object> allObjects = context.getObjects(type);
		Zone zone = ZoneLocator.getZoneAt(ourRobotLocation.getX(), ourRobotLocation.getY());
		for (Object b : allObjects) {
			@SuppressWarnings("unchecked")
			T aObject = (T) b;
			Double p = new Point2D.Double(aObject.getLocation().getX(), aObject.getLocation().getY());
			if (zone != null && zone.getBoundingBox().contains(p)) {
				set.add(aObject);
			}
		}

		return set;
	}

	/**
	 * @param targetid
	 * @return block that has given target ID
	 * @throws IllegalArgumentException
	 *             if no such block.
	 */
	private Block getBlock(long targetid) {
		for (Block b : getVisible(Block.class)) {
			if (b.getId() == targetid) {
				return b;
			}
		}
		throw new IllegalArgumentException("there is no block with id=" + targetid);
	}

	/**
	 * Reset the robot's location and should set it to its default spawn state.
	 * We try to have the bot drop all the blocks before it exists.
	 */
	protected void reset() {
		while (!ourRobot.getHolding().isEmpty()) {
			ourRobot.drop();
		}
		ourRobot.moveTo(this.spawnLocation.getX(), this.spawnLocation.getY());
	}

	/**
	 * Find the closest {@link BoundedMoveableObject} that can be picked up by
	 * the Robot.
	 * 
	 * @param type
	 *            the type of {@link BoundedMoveableObject} we are looking for
	 * @param <T>
	 * @return null if non were found, otherwise the closest
	 */
	private <T extends BoundedMoveableObject> T getClosest(Class<T> type) {
		Iterable<Object> allBlocks = context.getObjects(type);
		T nearest = null;
		double nearestDistance = Integer.MAX_VALUE;
		for (Object o : allBlocks) {
			@SuppressWarnings("unchecked")
			T aBlock = (T) o;
			double distance = ourRobot.distanceTo(aBlock);

			LOGGER.trace(String.format("%s is %f units away from %d.", ourRobot.getName(), distance, aBlock.getId()));
			if (ourRobot.canPickUp(aBlock)) {
				LOGGER.trace(String.format("%s can pick up %d checking distance.", ourRobot.getName(), aBlock.getId()));
				if (nearest == null || distance < nearestDistance) {
					nearest = aBlock;
					nearestDistance = distance;
				}
			}
		}
		return nearest;
	}

}
