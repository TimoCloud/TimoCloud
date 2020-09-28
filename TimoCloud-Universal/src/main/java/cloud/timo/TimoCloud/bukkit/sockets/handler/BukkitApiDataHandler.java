package cloud.timo.TimoCloud.bukkit.sockets.handler;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.bukkit.api.TimoCloudUniversalAPIBukkitImplementation;
import cloud.timo.TimoCloud.common.protocol.Message;
import cloud.timo.TimoCloud.common.protocol.MessageType;
import cloud.timo.TimoCloud.common.sockets.MessageHandler;
import io.netty.channel.Channel;

import java.util.Map;

public class BukkitApiDataHandler extends MessageHandler {
    public BukkitApiDataHandler() {
        super(MessageType.API_DATA);
    }

    @Override
    public void execute(Message message, Channel channel) {
        ((TimoCloudUniversalAPIBukkitImplementation) TimoCloudAPI.getUniversalAPI()).setData((Map<String, Object>) message.getData());
    }
}
