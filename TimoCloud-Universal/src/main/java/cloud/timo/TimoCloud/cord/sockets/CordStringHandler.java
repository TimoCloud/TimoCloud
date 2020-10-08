package cloud.timo.TimoCloud.cord.sockets;

import cloud.timo.TimoCloud.common.protocol.Message;
import cloud.timo.TimoCloud.common.protocol.MessageType;
import cloud.timo.TimoCloud.common.sockets.BasicStringHandler;
import cloud.timo.TimoCloud.common.sockets.MessageTypeNotFoundExcpetion;
import cloud.timo.TimoCloud.cord.TimoCloudCord;
import cloud.timo.TimoCloud.cord.sockets.handler.CordApiDataHandler;
import cloud.timo.TimoCloud.cord.sockets.handler.CordEventFiredHandler;
import cloud.timo.TimoCloud.cord.sockets.handler.CordHandshakeSuccessHandler;
import cloud.timo.TimoCloud.cord.sockets.handler.CordPluginMessageHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;

@ChannelHandler.Sharable
public class CordStringHandler extends BasicStringHandler {

    public CordStringHandler() {
        addBasicHandlers();
    }

    @Override
    public void handleMessage(Message message, String originalMessage, Channel channel) {
        MessageType type = message.getType();
        try {
            getMessageHandlers(type).forEach(messageHandler -> {
                try {
                    messageHandler.execute(message, channel);
                } catch (Exception e) {
                    TimoCloudCord.getInstance().severe("Messagehandler " + messageHandler.getClass().getSimpleName() + " threw an exception: ");
                    TimoCloudCord.getInstance().severe(e.getMessage());
                    e.printStackTrace();
                }
            });
        } catch (MessageTypeNotFoundExcpetion messageTypeNotFoundExcpetion) {
            TimoCloudCord.getInstance().severe(messageTypeNotFoundExcpetion.getMessage());
        }
    }

    private void addBasicHandlers() {
        addHandler(new CordApiDataHandler());
        addHandler(new CordEventFiredHandler());
        addHandler(new CordHandshakeSuccessHandler());
        addHandler(new CordPluginMessageHandler());
    }
}
