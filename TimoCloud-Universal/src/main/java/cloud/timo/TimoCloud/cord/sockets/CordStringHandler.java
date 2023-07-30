package cloud.timo.TimoCloud.cord.sockets;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.implementations.TimoCloudMessageAPIBasicImplementation;
import cloud.timo.TimoCloud.api.messages.objects.AddressedPluginMessage;
import cloud.timo.TimoCloud.bukkit.TimoCloudBukkit;
import cloud.timo.TimoCloud.common.protocol.Message;
import cloud.timo.TimoCloud.common.protocol.MessageType;
import cloud.timo.TimoCloud.common.sockets.BasicStringHandler;
import cloud.timo.TimoCloud.common.utils.PluginMessageSerializer;
import cloud.timo.TimoCloud.cord.TimoCloudCord;
import cloud.timo.TimoCloud.cord.sockets.handler.APIDataHandler;
import cloud.timo.TimoCloud.cord.sockets.handler.EventFiredHandler;
import cloud.timo.TimoCloud.cord.sockets.handler.HandshakeSuccessHandler;
import cloud.timo.TimoCloud.cord.sockets.handler.PluginMessageHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;

import java.util.Map;

@ChannelHandler.Sharable
public class CordStringHandler extends BasicStringHandler {

    @Override
    public void registerHandlers() {
        addHandler(new APIDataHandler());
        addHandler(new EventFiredHandler());
        addHandler(new HandshakeSuccessHandler());
        addHandler(new PluginMessageHandler());
    }

    @Override
    public void handleMessage(Message message, String originalMessage, Channel channel) {
        if (message == null) {
            TimoCloudBukkit.getInstance().severe("Error while parsing json (json is null): " + originalMessage);
            return;
        }

        if(!handleMessageInternal(message, originalMessage, channel)) {
            TimoCloudBukkit.getInstance().severe("Error: Could not categorize json message: " + message);
        }
    }
}
