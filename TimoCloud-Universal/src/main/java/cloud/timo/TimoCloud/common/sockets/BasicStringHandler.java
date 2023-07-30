package cloud.timo.TimoCloud.common.sockets;

import cloud.timo.TimoCloud.common.global.logging.TimoCloudLogger;
import cloud.timo.TimoCloud.common.protocol.Message;
import cloud.timo.TimoCloud.common.protocol.MessageType;
import cloud.timo.TimoCloud.common.sockets.handler.MessageHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.HashMap;

public abstract class BasicStringHandler extends SimpleChannelInboundHandler<String> {

    private HashMap<MessageType, MessageHandler> handlers = new HashMap<MessageType, MessageHandler>();

    public BasicStringHandler() {
        registerHandlers();
    }

    public abstract void registerHandlers();

    public void addHandler(MessageHandler messageHandler) {
        handlers.put(messageHandler.getMessageType(), messageHandler);
    }

    public void removeHandler(MessageHandler messageHandler) {
        handlers.remove(messageHandler.getMessageType(), messageHandler);
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

    public boolean handleMessageInternal(Message message, String originalMessage, Channel channel) {
        if (!handlers.containsKey(message.getType())) {
            return false;
        }

        handlers.get(message.getType()).execute(message, channel);
        return true;
    }

    public void closeChannel(Channel channel) {
        channel.close();
    }


    public HashMap<MessageType, MessageHandler> getHandlers() {
        return handlers;
    }

}
