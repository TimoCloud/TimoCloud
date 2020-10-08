package cloud.timo.TimoCloud.common.sockets;

import cloud.timo.TimoCloud.common.global.logging.TimoCloudLogger;
import cloud.timo.TimoCloud.common.protocol.Message;
import cloud.timo.TimoCloud.common.protocol.MessageType;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class BasicStringHandler extends SimpleChannelInboundHandler<String> {
    protected Map<MessageType, Set<MessageHandler>> messageHandlers;

    public BasicStringHandler() {
        messageHandlers = new HashMap<>();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String message) throws Exception {
        Channel channel = ctx.channel();
        try {
            handleMessage(Message.createFromJsonString(message), message, channel);
        } catch (Throwable e) {
            TimoCloudLogger.getLogger().severe("Error while parsing JSON message: " + message);
            TimoCloudLogger.getLogger().severe(e);
        }
    }

    public abstract void handleMessage(Message message, String originalMessage, Channel channel);

    public Set<MessageHandler> getMessageHandlers(MessageType messageType) throws MessageTypeNotFoundExcpetion {
        Set<MessageHandler> returnedMessageHandlers = messageHandlers.getOrDefault(messageType, new HashSet<MessageHandler>());

        if (returnedMessageHandlers.isEmpty())
            throw new MessageTypeNotFoundExcpetion(messageType);

        return returnedMessageHandlers;
    }

    public void addHandler(MessageHandler messageHandler) {
        Set<MessageHandler> existingMessageHandlers;

        existingMessageHandlers = messageHandlers.getOrDefault(messageHandler.getMessageType(), new HashSet<>());

        existingMessageHandlers.add(messageHandler);
        messageHandlers.put(messageHandler.getMessageType(), existingMessageHandlers);
    }

    public void removeHandler(MessageHandler messageHandler) {
        Set<MessageHandler> existingMessageHandlers = messageHandlers.get(messageHandler.getMessageType());
        existingMessageHandlers.remove(messageHandler);
        messageHandlers.put(messageHandler.getMessageType(), existingMessageHandlers);
    }

    public void closeChannel(Channel channel) {
        channel.close();
    }

}
