package cloud.timo.TimoCloud.velocity.sockets.handler;

import cloud.timo.TimoCloud.bungeecord.TimoCloudBungee;
import cloud.timo.TimoCloud.common.protocol.Message;
import cloud.timo.TimoCloud.common.protocol.MessageType;
import cloud.timo.TimoCloud.common.sockets.handler.MessageHandler;
import cloud.timo.TimoCloud.velocity.TimoCloudVelocity;
import io.netty.channel.Channel;

import java.net.InetSocketAddress;

public class ProxyAddServerHandler extends MessageHandler {
    public ProxyAddServerHandler() {
        super(MessageType.PROXY_ADD_SERVER);
    }

    @Override
    public void execute(Message message, Channel channel) {
        TimoCloudVelocity.getInstance().getServer().unregisterServer(TimoCloudVelocity.getInstance().getServer().getServer((String) message.get("name")).get().getServerInfo());
    }
}
