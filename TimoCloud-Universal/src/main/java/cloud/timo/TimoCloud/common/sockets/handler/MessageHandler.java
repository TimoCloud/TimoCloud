package cloud.timo.TimoCloud.common.sockets.handler;

import cloud.timo.TimoCloud.common.protocol.Message;
import cloud.timo.TimoCloud.common.protocol.MessageType;
import io.netty.channel.Channel;

public abstract class MessageHandler {
    private MessageType messageType;

    public MessageHandler(MessageType messageType) {
        this.messageType = messageType;
    }

    public abstract void execute(Message message, Channel channel);

    public MessageType getMessageType() {
        return messageType;
    }
}