package cloud.timo.TimoCloud.common.sockets;

import cloud.timo.TimoCloud.common.protocol.MessageType;

public class MessageTypeNotFoundExcpetion extends Exception {
    public MessageTypeNotFoundExcpetion(MessageType messageType) {
        super("Could not find any message handlers for the message type: " + messageType);
    }
}
