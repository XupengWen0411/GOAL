package nl.tudelft.bw4t.server.util;

import java.awt.geom.Point2D;

import nl.tudelft.bw4t.server.environment.BW4TEnvironment;
import nl.tudelft.bw4t.server.model.zone.BlocksArea;
import nl.tudelft.bw4t.server.model.zone.DropZone;
import nl.tudelft.bw4t.server.model.zone.Area;

/**
 * Utility class to locate in which rooms an object is.
 */
public final class AreaLocator {

    /**
     * Utility class, cannot be instantiated.
     */
    private AreaLocator() {
    }

    /**
     * Returns the {@link BlocksArea} the object is in. Note, {@link DropZone} is not a {@link BlocksArea}
     * 
     * @param x
     *            the x coordinate
     * @param y
     *            the y coordinate
     * @return The {@link BlocksArea} the given object is in or null if it is in the hall.
     */
    public static BlocksArea getAreaFor(double x, double y) {
        Area room = getAreaAt(x, y);
        if (room instanceof BlocksArea) {
            return (BlocksArea) room;
        }
        return null;
    }

    /**
     * Find {@link Area} containing given point. Both BlocksArea and DropZone are Area.
     * 
     * @param x
     *            the x coordinate
     * @param y
     *            the y coordinate
     * @return {@link Area} or null if no such room.
     */
    public static Area getAreaAt(double x, double y) {
        Point2D location = new Point2D.Double(x, y);
        Iterable<Object> rooms = BW4TEnvironment.getInstance().getContext().getObjects(Area.class);
        for (Object r : rooms) {
            Area room = (Area) r;

            if (room.getBoundingBox().contains(location)) {
                return room;
            }
        }
        return null;

    }

}
