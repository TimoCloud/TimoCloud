package cloud.timo.TimoCloud.bungeecord.sockets.handler;

import cloud.timo.TimoCloud.bungeecord.TimoCloudBungee;
import cloud.timo.TimoCloud.common.protocol.Message;
import cloud.timo.TimoCloud.common.protocol.MessageType;
import cloud.timo.TimoCloud.common.sockets.MessageHandler;
import io.netty.channel.Channel;

import java.net.InetSocketAddress;

public class ProxyAddServerHandler extends MessageHandler {
    public ProxyAddServerHandler() {
        super(MessageType.PROXY_ADD_SERVER);
    }

    @Override
    public void execute(Message message, Channel channel) {
        TimoCloudBungee.getInstance().getProxy().getServers().put((String) message.get("name"), TimoCloudBungee.getInstance().getProxy().constructServerInfo((String) message.get("name"), new InetSocketAddress((String) message.get("address"), ((Number) message.get("port")).intValue()), "", false));
    }
}
