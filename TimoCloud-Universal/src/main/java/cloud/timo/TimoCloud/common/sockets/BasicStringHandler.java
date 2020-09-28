package cloud.timo.TimoCloud.common.sockets;

import cloud.timo.TimoCloud.common.global.logging.TimoCloudLogger;
import cloud.timo.TimoCloud.common.protocol.Message;
import cloud.timo.TimoCloud.common.protocol.MessageType;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.ArrayList;
import java.util.List;

public abstract class BasicStringHandler extends SimpleChannelInboundHandler<String> {
    protected List<MessageHandler> messageHandlers;

    public BasicStringHandler() {
        messageHandlers = new ArrayList<>();
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

    public List<MessageHandler> getMessageHandlers(MessageType messageType) {
        List<MessageHandler> returnedMessageHandlers = new ArrayList<>();

        for(MessageHandler messageHandler : messageHandlers) {
            if(messageHandler.getMessageType().equals(messageType))
                returnedMessageHandlers.add(messageHandler);
        }

        if(returnedMessageHandlers.isEmpty())
            throw new MessageTypeNotFoundExcpetion(messageType);

        return returnedMessageHandlers;
    }

    public void addHandler(MessageHandler messageHandler) {
        messageHandlers.add(messageHandler);
    }

    public void removeHandler(MessageHandler messageHandler) {
        messageHandlers.remove(messageHandler);
    }

    public void closeChannel(Channel channel) {
        channel.close();
    }

}
