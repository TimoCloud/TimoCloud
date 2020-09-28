package cloud.timo.TimoCloud.bungeecord.sockets;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.events.EventType;
import cloud.timo.TimoCloud.api.implementations.TimoCloudMessageAPIBasicImplementation;
import cloud.timo.TimoCloud.api.implementations.TimoCloudUniversalAPIBasicImplementation;
import cloud.timo.TimoCloud.api.implementations.managers.EventManager;
import cloud.timo.TimoCloud.api.messages.objects.AddressedPluginMessage;
import cloud.timo.TimoCloud.api.utils.EventUtil;
import cloud.timo.TimoCloud.bungeecord.TimoCloudBungee;
import cloud.timo.TimoCloud.bungeecord.api.TimoCloudUniversalAPIBungeeImplementation;
import cloud.timo.TimoCloud.bungeecord.sockets.handler.*;
import cloud.timo.TimoCloud.common.protocol.Message;
import cloud.timo.TimoCloud.common.protocol.MessageType;
import cloud.timo.TimoCloud.common.sockets.BasicStringHandler;
import cloud.timo.TimoCloud.common.utils.EnumUtil;
import cloud.timo.TimoCloud.common.utils.PluginMessageSerializer;
import cloud.timo.TimoCloud.common.utils.network.InetAddressUtil;
import io.netty.channel.Channel;

import java.net.InetSocketAddress;
import java.util.Map;

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
        getMessageHandlers(type).forEach(messageHandler -> messageHandler.execute(message, channel));
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
