package nl.tudelft.bw4t.server.model.zone;

import nl.tudelft.bw4t.map.Zone;
import nl.tudelft.bw4t.server.model.BW4TServerMap;
import repast.simphony.context.Context;

/**
 * Constructs a blockade for a room.
 */
public class Blockade extends BlocksArea {

    /**
     * Creates a blockade.
     * 
     * @param roomzone
     *          The {@link Zone} in which the blockade will be located.
     * @param context
     *          The {@link Context} in which the blockade will be located.
     */
    public Blockade(Zone roomzone, BW4TServerMap context) {
        super(roomzone, context);
    }

}
