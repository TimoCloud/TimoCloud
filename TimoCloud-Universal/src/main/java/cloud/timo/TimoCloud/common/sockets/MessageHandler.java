package cloud.timo.TimoCloud.common.sockets;

import cloud.timo.TimoCloud.common.protocol.Message;
import cloud.timo.TimoCloud.common.protocol.MessageType;
import io.netty.channel.Channel;

public abstract class MessageHandler {
    private final MessageType messageType;
    private final boolean handShake;

    public MessageHandler(MessageType messageType) {
        this(messageType, false);
    }

    public MessageHandler(MessageType messageType, boolean handShake) {
        this.messageType = messageType;
        this.handShake = handShake;
    }

    public abstract void execute(Message message, Channel channel);

    public MessageType getMessageType() {
        return messageType;
    }

    public boolean isHandShake() {
        return handShake;
    }
}
