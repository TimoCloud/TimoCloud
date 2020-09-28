package cloud.timo.TimoCloud.core.sockets;

import cloud.timo.TimoCloud.common.protocol.Message;
import cloud.timo.TimoCloud.common.protocol.MessageType;
import cloud.timo.TimoCloud.common.sockets.BasicStringHandler;
import cloud.timo.TimoCloud.common.sockets.MessageHandler;
import cloud.timo.TimoCloud.common.sockets.MessageTypeNotFoundExcpetion;
import cloud.timo.TimoCloud.core.TimoCloudCore;
import cloud.timo.TimoCloud.core.objects.Proxy;
import cloud.timo.TimoCloud.core.objects.Server;
import cloud.timo.TimoCloud.core.sockets.handlers.*;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;

@ChannelHandler.Sharable
public class CoreStringHandler extends BasicStringHandler {
    public CoreStringHandler() {
        addBasicHandlers();
    }

    @Override
    public void handleMessage(Message message, String originalMessage, Channel channel) {
        MessageType type = message.getType();
        Communicatable sender = TimoCloudCore.getInstance().getSocketServerHandler().getCommunicatable(channel);
        String targetId = message.getTarget();
        Server server = TimoCloudCore.getInstance().getInstanceManager().getServerByIdentifier(targetId);
        Proxy proxy = TimoCloudCore.getInstance().getInstanceManager().getProxyByIdentifier(targetId);
        String baseName = (String) message.get("base");
        String cordName = (String) message.get("cord");
        Communicatable target = null;
        if (server != null) target = server;
        else if (proxy != null) target = proxy;
        else if (baseName != null)
            target = TimoCloudCore.getInstance().getInstanceManager().getBaseByIdentifier(baseName);
        else if (cordName != null) target = TimoCloudCore.getInstance().getInstanceManager().getCord(cordName);
        if (target == null) target = TimoCloudCore.getInstance().getSocketServerHandler().getCommunicatable(channel);

        boolean handshake = false;
        try {
            for (MessageHandler messageHandler : messageHandlers) {
                if (messageHandler.getMessageType().toString().contains("HANDSHAKE")) {
                    handshake = true;
                    messageHandler.execute(message, channel);
                }
            }
        } catch (MessageTypeNotFoundExcpetion e) {
            target.onMessage(message, sender);
        }

        // No Handshake, so we have to check if the channel is registered
        if (!handshake && (sender == null && channel != null)) { // If channel is null, the message is internal (sender is core)
            closeChannel(channel);
            TimoCloudCore.getInstance().severe("Unknown connection from " + channel.remoteAddress() + ", blocking. Please make sure to block the TimoCloudCore socket port (" + TimoCloudCore.getInstance().getSocketPort() + ") in your firewall to avoid this.");
            return;
        }

    }


    private void addBasicHandlers() {
        addHandler(new BaseProxyLogEntryHandler());
        addHandler(new CoreApiDataHandler());
        addHandler(new CoreBaseCheckDeletableHandler());
        addHandler(new CoreBaseHandshakeHandler());
        addHandler(new CoreCordHandshakeHandler());
        addHandler(new CoreFireEventHandler());
        addHandler(new CoreParseCommandHandler());
        addHandler(new CoreProxyHandshakeHandler());
        addHandler(new CoreSendPluginMessageHandler());
        addHandler(new CoreServerHandshakeHandler());
        addHandler(new CoreServerLogEntryHandler());
        addHandler(new ProxyTemplateRequestHandler());
        addHandler(new ServerTemplateRequestHandler());
    }
}
