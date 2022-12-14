package nl.tudelft.bw4t.client.message;

import java.util.StringTokenizer;

public interface StringToMessageCommand {

    BW4TMessage getMessage(String message);

}

class CommandType implements StringToMessageCommand {
    MessageType type;

    public CommandType(MessageType type) {
        this.type = type;
    }

    @Override
    public BW4TMessage getMessage(String message) {
        return new BW4TMessage(type);
    }
}

class CommandArea implements StringToMessageCommand {

    MessageType type;

    public CommandArea(MessageType type) {
        this.type = type;
    }

    @Override
    public BW4TMessage getMessage(String message) {
        return new BW4TMessage(type, MessageTranslator.findAreaId(message), MessageTranslator.findColorId(message), Integer.MAX_VALUE);
    }
}

class CommandColor implements StringToMessageCommand {

    MessageType type;

    public CommandColor(MessageType type) {
        this.type = type;
    }

    @Override
    public BW4TMessage getMessage(String message) {
        return new BW4TMessage(type, MessageTranslator.findAreaId(message), MessageTranslator.findColorId(message), Integer.MAX_VALUE);
    }
}

class CommandAreaColor implements StringToMessageCommand {

    MessageType type;

    public CommandAreaColor(MessageType type) {
        this.type = type;
    }

    @Override
    public BW4TMessage getMessage(String message) {
        return new BW4TMessage(type, MessageTranslator.findAreaId(message), MessageTranslator.findColorId(message),
                Integer.MAX_VALUE);
    }
}

class CommandAreaColorPlayer implements StringToMessageCommand {

    String message;
    MessageType type;
    String playerId;

    public CommandAreaColorPlayer(MessageType type) {
        this.type = type;
    }

    @Override
    public BW4TMessage getMessage(String message) {
        playerId = new StringTokenizer(message, ", ").nextToken();
        return new BW4TMessage(type, MessageTranslator.findAreaId(message), MessageTranslator.findColorId(message),
                playerId);
    }
}

class CommandAreaColorNumber implements StringToMessageCommand {

    MessageType type;

    public CommandAreaColorNumber(MessageType type) {
        this.type = type;
    }

    @Override
    public BW4TMessage getMessage(String message) {
        return new BW4TMessage(type, MessageTranslator.findAreaId(message), MessageTranslator.findColorId(message),
                MessageTranslator.findNumber(message));
    }
}

class CommandContainsBy implements StringToMessageCommand {

    MessageType type;
    String playerId;

    public CommandContainsBy(MessageType type) {
        this.type = type;
    }

    @Override
    public BW4TMessage getMessage(String message) {
        if (message.contains("by")) {
            String[] splitMessage = message.split(" ");
            playerId = splitMessage[splitMessage.length - 1];
        }

        return new BW4TMessage(type, MessageTranslator.findAreaId(message), MessageTranslator.findColorId(message), playerId);
    }
}

class CommandContains implements StringToMessageCommand {
    
    private MessageType type;

    public CommandContains() {
        
    }

    @Override
    public BW4TMessage getMessage(String message) {

        int number = MessageTranslator.findNumber(message);
        if (number == Integer.MAX_VALUE) {
            type = MessageType.ROOMCONTAINS;
        } else {
            type = MessageType.ROOMCONTAINSAMOUNT;
        }

        return new BW4TMessage(type, MessageTranslator.findAreaId(message), MessageTranslator.findColorId(message),
                number);
    }
}
