package nl.tudelft.bw4t.client.gui.listeners;

import java.awt.event.ActionEvent;

import org.apache.log4j.Logger;

import eis.iilang.Percept;
import nl.tudelft.bw4t.client.controller.ClientController;
import nl.tudelft.bw4t.client.gui.BW4TClientGUI;

/**
 * ActionListener that performs the pick up action when that command is pressed in the pop up menu
 */
public class NavigateObstaclesActionListener extends AbstractClientActionListener {
    
    /**
     * The log4j Logger which displays logs on console
     */
    private static final Logger LOGGER = Logger.getLogger(BW4TClientGUI.class);

    public NavigateObstaclesActionListener(ClientController controller) {
        super(controller);
    }
    
    @Override
    protected void actionWithHumanAgent(ActionEvent arg0) {
        try {
            getController().getHumanAgent().navigateObstacles();
        } catch (Exception e1) {
            LOGGER.error("Could tell the agent to perform a navigateObstacles action.", e1);
        }
    }
    
    @Override
    protected void actionWithGoalAgent(ActionEvent arg0) {
        Percept percept = new Percept("navigateObstacles");
        getController().addToBePerformedAction(percept);
    }
}
