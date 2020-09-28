package cloud.timo.TimoCloud.bungeecord.sockets.handler;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.bungeecord.api.TimoCloudUniversalAPIBungeeImplementation;
import cloud.timo.TimoCloud.common.protocol.Message;
import cloud.timo.TimoCloud.common.protocol.MessageType;
import cloud.timo.TimoCloud.common.sockets.MessageHandler;
import io.netty.channel.Channel;

import java.util.Map;

public class ProxyApiDataHandler extends MessageHandler {
    public ProxyApiDataHandler() {
        super(MessageType.API_DATA);
    }

    @Override
    public void execute(Message message, Channel channel) {
        ((TimoCloudUniversalAPIBungeeImplementation) TimoCloudAPI.getUniversalAPI()).setData((Map<String, Object>) message.getData());
    }
}
