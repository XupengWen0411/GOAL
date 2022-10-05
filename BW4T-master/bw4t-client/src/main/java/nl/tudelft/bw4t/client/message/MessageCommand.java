package nl.tudelft.bw4t.client.message;

import eis.iilang.Function;
import eis.iilang.Identifier;
import eis.iilang.Numeral;
import eis.iilang.Parameter;

/**
 * The MessageCommand interface defines the functions a command should have
 *
 */
public interface MessageCommand {
    /**
     * Function to translate a message to parameters
     * @param message
     *          The message being translated
     * @param entityId 
     * @return the translated message in parameters
     */
    Parameter getParam(BW4TMessage message, String entityId);
    
    /**
     * Function to translate a BW4TMessage to String
     * @param message
     *          The message to be translated
     * @return The message as a string
     */
    String getString(BW4TMessage message);
}

class CommandGoingToArea implements MessageCommand {

    @Override
    public String getString(BW4TMessage message) {
        if (message.getColor() == null) {
            return "I am going to room " + message.getArea();
        } else {
            return "I am going to room " + message.getArea() + " to get a "
                    + message.getColor() + "  block";
        }
    }

    @Override
    public Parameter getParam(BW4TMessage message, String entityId) {
        return new Function("imp", new Function("in", new Identifier(
                entityId), new Identifier(message.getArea())));
    }
}

class CommandHasColor implements MessageCommand {

    @Override
    public Parameter getParam(BW4TMessage message, String entityId) {
        if (message.getArea() != null) {
            return new Function("pickedUpFrom", new Identifier(entityId),
                    new Identifier(message.getColor()), new Identifier(
                            message.getArea()));
        } else {
            return new Function("holding", new Identifier(entityId),
                    new Identifier(message.getColor()));
        }
    }

    @Override
    public String getString(BW4TMessage message) {
        if (message.getArea() == null) {
            return "I have a " + message.getColor() + " block";
        } else {
            return "I have a " + message.getColor() + " block from room "
                    + message.getArea();
        }
    }
}

class CommandWeNeed implements MessageCommand {

    @Override
    public Parameter getParam(BW4TMessage message, String entityId) {
        return new Function("need", new Identifier(message.getColor()));
    }

    @Override
    public String getString(BW4TMessage message) {
        return "We need a " + message.getColor() + " block";
    }
}

class CommandLookingFor implements MessageCommand {

    @Override
    public Parameter getParam(BW4TMessage message, String entityId) {
        return new Function("imp", new Function("found", new Identifier(
                entityId), new Identifier(message.getColor())));
    }

    @Override
    public String getString(BW4TMessage message) {
        return "I am looking for a " + message.getColor() + " block";
    }
}

class CommandAmGettingColor implements MessageCommand {

    @Override
    public Parameter getParam(BW4TMessage message, String entityId) {
        return new Function("imp", new Function("pickedUpFrom",
                new Identifier(entityId),
                new Identifier(message.getColor()), new Identifier(
                        message.getArea())));
    }

    @Override
    public String getString(BW4TMessage message) {
        if (message.getArea() == null) {
            return "I am getting a " + message.getColor() + " block";
        } else {
            return "I am getting a " + message.getColor()
                    + " block from room " + message.getArea();
        }
    }
}

class CommandWillGetColor implements MessageCommand {

    @Override
    public Parameter getParam(BW4TMessage message, String entityId) {
        return new Function("imp", new Function("holding", new Identifier(
                entityId), new Identifier(message.getColor())));
    }

    @Override
    public String getString(BW4TMessage message) {
         if (message.getArea() == null) {
             return "I will get a " + message.getColor() + " block";
         } else {
             return "I will get a " + message.getColor()
                     + " block from room " + message.getArea();
         }
    }
}

class CommandAreaContains implements MessageCommand {

    @Override
    public Parameter getParam(BW4TMessage message, String entityId) {
         return new Function("at", new Identifier(message.getColor()),
                 new Identifier(message.getArea()));
    }

    @Override
    public String getString(BW4TMessage message) {
        return "room " + message.getArea() + " contains a "
                + message.getColor() + " block";        
    }
}

class CommandAreaIsEmpty implements MessageCommand {

    @Override
    public Parameter getParam(BW4TMessage message, String entityId) {
        return new Function("empty", new Identifier(message.getArea()));
    }

    @Override
    public String getString(BW4TMessage message) {
        return "room " + message.getArea() + " is empty";
    }
}

class CommandInArea implements MessageCommand {

    @Override
    public Parameter getParam(BW4TMessage message, String entityId) {
        return new Function("in", new Identifier(entityId), new Identifier(
                message.getArea()));
    }

    @Override
    public String getString(BW4TMessage message) {
        return "I am in room " + message.getArea();
    }
}

class CommandAboutToDropOffBlock implements MessageCommand {

    @Override
    public Parameter getParam(BW4TMessage message, String entityId) {
        return new Function("imp", new Function("putDown", new Identifier(
                entityId)));
    }

    @Override
    public String getString(BW4TMessage message) {
        return "I am about to drop off a " + message.getColor() +  " block";
    }
}

class CommandDroppedOffBlock implements MessageCommand {

    @Override
    public Parameter getParam(BW4TMessage message, String entityId) {
        if (message.getColor() == null) {
            return new Function("putDown", new Identifier(entityId));
        } else {
            return new Function("putDown", new Identifier(entityId),
                    new Identifier(message.getColor()));
        }
    }

    @Override
    public String getString(BW4TMessage message) {
        if (message.getColor() == null) {
            return "I just dropped off a block";
        } else {
            return "I just dropped off a " + message.getColor() + " block";
        }
    }
}

class CommandAmWaitingOutsideArea implements MessageCommand {
    
    private final static String RET = "I am waiting outside room ";

    @Override
    public Parameter getParam(BW4TMessage message, String entityId) {
        return new Function("waitingOutside", new Identifier(entityId),
                new Identifier(message.getArea()));
    }

    @Override
    public String getString(BW4TMessage message) {
        if (message.getColor() != null) {
         return RET + message.getArea()
                 + " with a " + message.getColor() + " block";
        } else {
            return RET + message.getArea();
        }
    }
}

class CommandYes implements MessageCommand {

    @Override
    public Parameter getParam(BW4TMessage message, String entityId) {
        return new Identifier("yes");
    }

    @Override
    public String getString(BW4TMessage message) {
        return "yes";
    }
}

class CommandNo implements MessageCommand {

    @Override
    public Parameter getParam(BW4TMessage message, String entityId) {
        return new Identifier("no");
    }

    @Override
    public String getString(BW4TMessage message) {
        return "no";
    }
}

class CommandIDoNotKnow implements MessageCommand {

    @Override
    public Parameter getParam(BW4TMessage message, String entityId) {
        return new Identifier("dontknow");
    }

    @Override
    public String getString(BW4TMessage message) {
        return "I don't know";
    }
}

class CommandOk implements MessageCommand {

    @Override
    public String getString(BW4TMessage message) {
        if (message.getArea() == null) {
            return "OK";
        } else if (message.getArea() != null) { 
            return "OK, room " + message.getArea();
        } else {
          return null;
        }
    }

    @Override
    public Parameter getParam(BW4TMessage message, String entityId) {
        if (message.getArea() == null) {
            return new Identifier("ok");
        } else {
            return new Function("ok", new Identifier(message.getArea()));
        }
    }
}

class CommandIDo implements MessageCommand {

    @Override
    public Parameter getParam(BW4TMessage message, String entityId) {
        return new Identifier("ido");
    }

    @Override
    public String getString(BW4TMessage message) {
        return "I do";
    }
}

class CommandIDoNot implements MessageCommand {

    @Override
    public Parameter getParam(BW4TMessage message, String entityId) {
        return new Identifier("idont");
    }

    @Override
    public String getString(BW4TMessage message) {
        return "I don't";
    }
}

class CommandWait implements MessageCommand {

    @Override
    public Parameter getParam(BW4TMessage message, String entityId) {
        return new Identifier("wait");
    }

    @Override
    public String getString(BW4TMessage message) {
        return "wait";
    }
}

class CommandOnTheWay implements MessageCommand {

    @Override
    public Parameter getParam(BW4TMessage message, String entityId) {
        return new Identifier("ontheway");
    }

    @Override
    public String getString(BW4TMessage message) {
        return "I am on the way";
    }
}

class CommandAlmostThere implements MessageCommand {

    @Override
    public Parameter getParam(BW4TMessage message, String entityId) {
        return new Identifier("almostthere");
    }

    @Override
    public String getString(BW4TMessage message) {
        return "I am almost there";
    }
}

class CommandFarAway implements MessageCommand {

    @Override
    public Parameter getParam(BW4TMessage message, String entityId) {
        return new Identifier("faraway");
    }

    @Override
    public String getString(BW4TMessage message) {
        return "I am far away";
    }
}

class CommandDelayed implements MessageCommand {

    @Override
    public Parameter getParam(BW4TMessage message, String entityId) {
        return new Identifier("delayed");
    }

    @Override
    public String getString(BW4TMessage message) {
        return "I am delayed";
    }
}

class CommandIsAnybodyGoingToArea implements MessageCommand {

    @Override
    public Parameter getParam(BW4TMessage message, String entityId) {
        return new Function("int", new Function("imp", new Function("in",
                new Identifier("unknown"),
                new Identifier(message.getArea()))));
    }

    @Override
    public String getString(BW4TMessage message) {
        return "Is anybody going to room " + message.getArea() + "?";
    }
}

class CommandWhoHasABlock implements MessageCommand {

    @Override
    public Parameter getParam(BW4TMessage message, String entityId) {
        return new Function("int", new Function("holding", new Identifier(
                "unknown"), new Identifier(message.getColor())));
    }

    @Override
    public String getString(BW4TMessage message) {
        return "Who has a " + message.getColor() + " block?";
    }
}

class CommandWhereIsColor implements MessageCommand {

    @Override
    public Parameter getParam(BW4TMessage message, String entityId) {
        return new Function("int", new Function("at", new Identifier(
                message.getColor()), new Identifier("unknown")));
    }

    @Override
    public String getString(BW4TMessage message) {
        return "Where is a " + message.getColor() +  " block?";
    }
}

class CommandWhatIsInArea implements MessageCommand {

    @Override
    public Parameter getParam(BW4TMessage message, String entityId) {
        return new Function("int", new Function("at", new Identifier(
                "unknown"), new Identifier(message.getArea())));
    }

    @Override
    public String getString(BW4TMessage message) {
        return "What is in room " + message.getArea() + "?";
    }
}

class CommandHasAnybodyCheckedArea implements MessageCommand {

    @Override
    public Parameter getParam(BW4TMessage message, String entityId) {
        return new Function("int", new Function("checked", new Identifier(
                "unknown"), new Identifier(message.getArea())));
    }

    @Override
    public String getString(BW4TMessage message) {
        return "Has anybody checked room " + message.getArea() + "?";
    }
}

class CommandWhoIsInArea implements MessageCommand {

    @Override
    public Parameter getParam(BW4TMessage message, String entityId) {
        return new Function("int", new Function("in", new Identifier(
                "unknown"), new Identifier(message.getArea())));
    }

    @Override
    public String getString(BW4TMessage message) {
        return "Who is in room " + message.getArea() + "?";
    }
}

class CommandAreYouClose implements MessageCommand {

    @Override
    public Parameter getParam(BW4TMessage message, String entityId) {
        return new Function("int", new Function("areClose", new Identifier(
                message.getPlayerId())));
    }

    @Override
    public String getString(BW4TMessage message) {
        return message.getPlayerId() + ", are you close?";
    }
}

class CommandWillYouBeLong implements MessageCommand {

    @Override
    public Parameter getParam(BW4TMessage message, String entityId) {
        return new Function("int", new Function("willBeLong",
                new Identifier(message.getPlayerId())));
    }

    @Override
    public String getString(BW4TMessage message) {
        return message.getPlayerId() + ", will you be long?";
    }
}

class CommandWhereShouldIGo implements MessageCommand {

    @Override
    public Parameter getParam(BW4TMessage message, String entityId) {
        return new Function("int", new Function("imp", new Function("in",
                new Identifier(entityId), new Identifier("unknown"))));
    }

    @Override
    public String getString(BW4TMessage message) {
        return "Where should I go?";
    }
}

class CommandWhatColorShouldIGet implements MessageCommand {

    @Override
    public Parameter getParam(BW4TMessage message, String entityId) {
        return new Function("int", new Function("imp", new Function(
                "holding", new Identifier(entityId), new Identifier(
                        "unknown"))));
    }

    @Override
    public String getString(BW4TMessage message) {
        return "What color should I get?";
    }
}

class CommandGotoArea implements MessageCommand {

    @Override
    public Parameter getParam(BW4TMessage message, String entityId) {
         return new Function("imp", new Function("in", new Identifier(
                 message.getPlayerId()), new Identifier(message.getArea())));
    }

    @Override
    public String getString(BW4TMessage message) {
        return message.getPlayerId() + ", go to room " + message.getArea();
    }
}

class CommandFindColor implements MessageCommand {

    @Override
    public Parameter getParam(BW4TMessage message, String entityId) {
         return new Function("imp", new Function("found", new Identifier(
                 message.getPlayerId()), new Identifier(message.getColor())));
    }

    @Override
    public String getString(BW4TMessage message) {
         return message.getPlayerId() + ", find a " + message.getColor() + " block";
    }
}

class CommandGetColorFromArea implements MessageCommand {

    @Override
    public Parameter getParam(BW4TMessage message, String entityId) {
        return new Function("imp", new Function("pickedUpFrom",
                new Identifier(message.getPlayerId()), new Identifier(
                        message.getColor()), new Identifier(
                        message.getArea())));
    }

    @Override
    public String getString(BW4TMessage message) {
        return message.getPlayerId() + ", get the " + message.getColor()
                + " from room " + message.getArea();
    }
}

class CommandAtBox implements MessageCommand {

    @Override
    public Parameter getParam(BW4TMessage message, String entityId) {
         return new Function("atBox", new Identifier(message.getColor()));
    }

    @Override
    public String getString(BW4TMessage message) {
        return "I am at a " + message.getColor() +  " block";
    }
}

class CommandCouldNot implements MessageCommand {

    @Override
    public Parameter getParam(BW4TMessage message, String entityId) {
        return new Identifier("couldnot");
    }

    @Override
    public String getString(BW4TMessage message) {
        return "I couldn't";
    }
}

class CommandPutDown implements MessageCommand {

    @Override
    public Parameter getParam(BW4TMessage message, String entityId) {
        return new Function("imp", new Function("putDown", new Identifier(
                message.getPlayerId())));
    }

    @Override
    public String getString(BW4TMessage message) {
        return message.getPlayerId() + ", put down the block you are holding";
    }
}

class CommandAreaContainsAmount implements MessageCommand {

    @Override
    public Parameter getParam(BW4TMessage message, String entityId) {
         return new Function("at", new Numeral(message.getNumber()),
                 new Identifier(message.getColor()), new Identifier(
                         message.getArea()));
    }

    @Override
    public String getString(BW4TMessage message) {
        return "room " + message.getArea() + " contains "
                + message.getNumber() + " " + message.getColor()
                + " blocks";
    }
}

class CommandChecked implements MessageCommand {

    @Override
    public String getString(BW4TMessage message) {
        if (message.getPlayerId() == null) {
            return "room " + message.getArea() + " has been checked";
        } else {
            return "room " + message.getArea() + " has been checked by "
                    + message.getPlayerId();
        }
    }
    @Override
    public Parameter getParam(BW4TMessage message, String entityId) {
        if (message.getPlayerId() == null) {
            return new Function("checked",
                    new Identifier(message.getArea()));
        } else {
            return new Function("checked", new Identifier(
                    message.getPlayerId()), new Identifier(
                    message.getArea()));
        }
    }
}

class CommandEPartnerIWantToGo implements MessageCommand {

    @Override
    public Parameter getParam(BW4TMessage message, String entityId) {
        return new Identifier("IWantToGoTo TODO");
    }

    @Override
    public String getString(BW4TMessage message) {
        return "I want to go to " + message.getArea();
    }
    
}

class CommandEPartnerYouForgotMe implements MessageCommand {

    @Override
    public Parameter getParam(BW4TMessage message, String entityId) {
        return new Identifier("YouForgotMe TODO");
    }

    @Override
    public String getString(BW4TMessage message) {
        return "You forgot me in " + message.getArea();
    }
    
}
