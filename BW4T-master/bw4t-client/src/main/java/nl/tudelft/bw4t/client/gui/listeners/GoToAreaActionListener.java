package nl.tudelft.bw4t.client.gui.listeners;

import java.awt.event.ActionEvent;

import org.apache.log4j.Logger;

import eis.exceptions.ActException;
import eis.iilang.Identifier;
import eis.iilang.Percept;
import nl.tudelft.bw4t.client.controller.ClientController;

/**
 * ActionListener that performs the goTo action when that command is pressed in
 * the pop up menu
 */
public class GoToAreaActionListener extends AbstractClientActionListener {
    /** ID of the room to goTo when this listener is fired. */
    private final String id;
    /** Logger to report error messages to. */
    private static final Logger LOGGER = Logger.getLogger(GoToAreaActionListener.class);

    /**
     * @param id - ID of the room to goTo when this listener is fired.
     * @param controller - The {@link ClientController} to listen to and interact with.
     */
    public GoToAreaActionListener(String id, ClientController controller) {
        super(controller);
        this.id = id;
    }

    @Override
    protected void actionWithHumanAgent(ActionEvent arg0) {
        try {
            getController().getHumanAgent().goTo(id);
        } catch (ActException e1) {
            // Also catch NoServerException. Nothing we can do really.
            LOGGER.error(e1); 
        }
    }

    @Override
    protected void actionWithGoalAgent(ActionEvent arg0) {
        Percept percept = new Percept("goTo", new Identifier(id));
        getController().addToBePerformedAction(percept);
    }
}
