package cloud.timo.TimoCloud.core.sockets.handlers;

import cloud.timo.TimoCloud.common.protocol.Message;
import cloud.timo.TimoCloud.common.protocol.MessageType;
import cloud.timo.TimoCloud.common.sockets.MessageHandler;
import cloud.timo.TimoCloud.core.TimoCloudCore;
import cloud.timo.TimoCloud.core.objects.Server;
import cloud.timo.TimoCloud.core.sockets.CoreRSAHandshakeHandler;
import io.netty.channel.Channel;

import java.net.InetAddress;
import java.net.InetSocketAddress;

public class CoreServerHandshakeHandler extends MessageHandler {
    public CoreServerHandshakeHandler() {
        super(MessageType.SERVER_HANDSHAKE);
    }

    @Override
    public void execute(Message message, Channel channel) {
        String targetId = message.getTarget();
        Server server = TimoCloudCore.getInstance().getInstanceManager().getServerByIdentifier(targetId);
        InetAddress address = channel == null ? null : ((InetSocketAddress) channel.remoteAddress()).getAddress();

        if (server == null) {
            channel.close();
            return;
        }
        if (! (address.equals(server.getBase().getAddress()) || address.equals(server.getBase().getPublicAddress()))) {
            TimoCloudCore.getInstance().severe("Server connected with different InetAddress than its base. Refusing connection.");
            return;
        }
        if (! channel.attr(CoreRSAHandshakeHandler.RSA_KEY_ATTRIBUTE_KEY).get().equals(server.getPublicKey())) {
            TimoCloudCore.getInstance().severe(String.format("Server %s connected with wrong public key. Please report this.", server.getName()));
            return;
        }
        TimoCloudCore.getInstance().getSocketServerHandler().setCommunicatable(channel, server);
        server.onConnect(channel);
        server.onHandshakeSuccess();
        channel.attr(CoreRSAHandshakeHandler.HANDSHAKE_PERFORMED_ATTRIBUTE_KEY).set(true);
    }
}
