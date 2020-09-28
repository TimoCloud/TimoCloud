package cloud.timo.TimoCloud.core.sockets.handlers;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.messages.objects.AddressedPluginMessage;
import cloud.timo.TimoCloud.bukkit.api.TimoCloudUniversalAPIBukkitImplementation;
import cloud.timo.TimoCloud.common.protocol.Message;
import cloud.timo.TimoCloud.common.protocol.MessageType;
import cloud.timo.TimoCloud.common.sockets.MessageHandler;
import cloud.timo.TimoCloud.common.utils.PluginMessageSerializer;
import cloud.timo.TimoCloud.core.TimoCloudCore;
import io.netty.channel.Channel;

import java.util.Map;

public class CoreSendPluginMessageHandler extends MessageHandler {
    public CoreSendPluginMessageHandler() {
        super(MessageType.SEND_PLUGIN_MESSAGE);
    }

    @Override
    public void execute(Message message, Channel channel) {
        AddressedPluginMessage addressedPluginMessage = PluginMessageSerializer.deserialize((Map) message.getData());
        TimoCloudCore.getInstance().getPluginMessageManager().onMessage(addressedPluginMessage);
    }
}
