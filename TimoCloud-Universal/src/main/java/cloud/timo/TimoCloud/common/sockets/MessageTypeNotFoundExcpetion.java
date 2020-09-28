package cloud.timo.TimoCloud.common.sockets;

import cloud.timo.TimoCloud.common.protocol.MessageType;

public class MessageTypeNotFoundExcpetion extends RuntimeException{
    public MessageTypeNotFoundExcpetion(MessageType messageType) {
        super("Could not categorize message type: " + messageType);
    }
}
