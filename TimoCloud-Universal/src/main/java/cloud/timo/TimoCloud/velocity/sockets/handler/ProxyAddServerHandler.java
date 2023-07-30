package cloud.timo.TimoCloud.velocity.sockets.handler;

import cloud.timo.TimoCloud.common.protocol.Message;
import cloud.timo.TimoCloud.common.protocol.MessageType;
import cloud.timo.TimoCloud.common.sockets.handler.MessageHandler;
import cloud.timo.TimoCloud.velocity.TimoCloudVelocity;
import com.velocitypowered.api.proxy.server.ServerInfo;
import io.netty.channel.Channel;

import java.net.InetSocketAddress;

public class ProxyAddServerHandler extends MessageHandler {

    public ProxyAddServerHandler() {
        super(MessageType.PROXY_ADD_SERVER);
    }

    @Override
    public void execute(Message message, Channel channel) {
        TimoCloudVelocity.getInstance().getServer().registerServer(new ServerInfo((String) message.get("name"), new InetSocketAddress((String) message.get("address"), ((Number) message.get("port")).intValue())));
    }
}
