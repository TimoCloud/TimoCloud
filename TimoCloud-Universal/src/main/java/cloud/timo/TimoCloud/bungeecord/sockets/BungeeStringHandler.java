package cloud.timo.TimoCloud.bungeecord.sockets;

import cloud.timo.TimoCloud.bungeecord.TimoCloudBungee;
import cloud.timo.TimoCloud.bungeecord.sockets.handler.*;
import cloud.timo.TimoCloud.common.protocol.Message;
import cloud.timo.TimoCloud.common.protocol.MessageType;
import cloud.timo.TimoCloud.common.sockets.BasicStringHandler;
import cloud.timo.TimoCloud.common.sockets.MessageTypeNotFoundExcpetion;
import io.netty.channel.Channel;

public class BungeeStringHandler extends BasicStringHandler {

    public BungeeStringHandler() {
        addBasicHandlers();
    }

    @Override
    public void handleMessage(Message message, String originalMessage, Channel channel) {
        if (message == null) {
            TimoCloudBungee.getInstance().severe("Error while parsing json (json is null): " + originalMessage);
            return;
        }
        MessageType type = message.getType();
        try {
            getMessageHandlers(type).forEach(messageHandler -> {
                try {
                    messageHandler.execute(message, channel);
                } catch (Exception e) {
                    TimoCloudBungee.getInstance().severe("Messagehandler " + messageHandler.getClass().getSimpleName() + " threw an exception: ");
                    TimoCloudBungee.getInstance().severe(e.getMessage());
                    e.printStackTrace();
                }
            });
        } catch (MessageTypeNotFoundExcpetion messageTypeNotFoundExcpetion) {
            TimoCloudBungee.getInstance().severe(messageTypeNotFoundExcpetion.getMessage());
        }
    }

    private void addBasicHandlers() {
        addHandler(new ProxyAddServerHandler());
        addHandler(new ProxyApiDataHandler());
        addHandler(new ProxyCordSetIPHandler());
        addHandler(new ProxyCoreMessageToSenderHandler());
        addHandler(new ProxyEventFiredHandler());
        addHandler(new ProxyExecuteCommandHandler());
        addHandler(new ProxyHandshakeSuccessHandler());
        addHandler(new ProxyPluginMessageHandler());
        addHandler(new ProxyRemoveServerHandler());
        addHandler(new ProxyStopHandler());
    }
}
