package cloud.timo.TimoCloud.bungeecord.sockets.handler;

import cloud.timo.TimoCloud.bungeecord.TimoCloudBungee;
import cloud.timo.TimoCloud.common.protocol.Message;
import cloud.timo.TimoCloud.common.protocol.MessageType;
import cloud.timo.TimoCloud.common.sockets.MessageHandler;
import io.netty.channel.Channel;

public class ProxyRemoveServerHandler extends MessageHandler {
    public ProxyRemoveServerHandler() {
        super(MessageType.PROXY_REMOVE_SERVER);
    }

    @Override
    public void execute(Message message, Channel channel) {
        TimoCloudBungee.getInstance().getProxy().getServers().remove((String) message.get("name"));
    }
}
