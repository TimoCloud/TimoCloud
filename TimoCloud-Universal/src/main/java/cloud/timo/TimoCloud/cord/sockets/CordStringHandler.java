package cloud.timo.TimoCloud.cord.sockets;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.events.EventType;
import cloud.timo.TimoCloud.api.implementations.TimoCloudMessageAPIBasicImplementation;
import cloud.timo.TimoCloud.api.implementations.TimoCloudUniversalAPIBasicImplementation;
import cloud.timo.TimoCloud.api.implementations.managers.EventManager;
import cloud.timo.TimoCloud.api.messages.objects.AddressedPluginMessage;
import cloud.timo.TimoCloud.api.utils.EventUtil;
import cloud.timo.TimoCloud.common.protocol.Message;
import cloud.timo.TimoCloud.common.protocol.MessageType;
import cloud.timo.TimoCloud.common.sockets.BasicStringHandler;
import cloud.timo.TimoCloud.common.utils.EnumUtil;
import cloud.timo.TimoCloud.common.utils.PluginMessageSerializer;
import cloud.timo.TimoCloud.cord.TimoCloudCord;
import cloud.timo.TimoCloud.cord.api.TimoCloudUniversalAPICordImplementation;
import cloud.timo.TimoCloud.cord.sockets.handler.CordApiDataHandler;
import cloud.timo.TimoCloud.cord.sockets.handler.CordEventFiredHandler;
import cloud.timo.TimoCloud.cord.sockets.handler.CordHandshakeSuccessHandler;
import cloud.timo.TimoCloud.cord.sockets.handler.CordPluginMessageHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;

import java.util.Map;

@ChannelHandler.Sharable
public class CordStringHandler extends BasicStringHandler {

    public CordStringHandler() {
        addBasicHandlers();
    }

    @Override
    public void handleMessage(Message message, String originalMessage, Channel channel) {
        MessageType type = message.getType();
        getMessageHandlers(type).forEach(messageHandler -> messageHandler.execute(message, channel));
    }

    private void addBasicHandlers() {
        addHandler(new CordApiDataHandler());
        addHandler(new CordEventFiredHandler());
        addHandler(new CordHandshakeSuccessHandler());
        addHandler(new CordPluginMessageHandler());
    }
}
