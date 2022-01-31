package cloud.timo.TimoCloud.core.sockets.handler;

import cloud.timo.TimoCloud.api.messages.objects.AddressedPluginMessage;
import cloud.timo.TimoCloud.common.protocol.Message;
import cloud.timo.TimoCloud.common.protocol.MessageType;
import cloud.timo.TimoCloud.common.utils.PluginMessageSerializer;
import cloud.timo.TimoCloud.core.TimoCloudCore;
import cloud.timo.TimoCloud.core.sockets.Communicatable;
import io.netty.channel.Channel;

import java.net.InetAddress;
import java.util.Map;

public class SendPluginMessageHandler extends CoreMessageHandler {
    public SendPluginMessageHandler() {
        super(MessageType.SEND_PLUGIN_MESSAGE);
    }

    @Override
    public void execute(Message message, Communicatable target, InetAddress address, Channel channel) {
        AddressedPluginMessage addressedPluginMessage = PluginMessageSerializer.deserialize((Map) message.getData());
        TimoCloudCore.getInstance().getPluginMessageManager().onMessage(addressedPluginMessage);
    }
}
