package cloud.timo.TimoCloud.core.sockets.handler.handshake;

import cloud.timo.TimoCloud.common.protocol.Message;
import cloud.timo.TimoCloud.common.protocol.MessageType;
import cloud.timo.TimoCloud.core.TimoCloudCore;
import cloud.timo.TimoCloud.core.objects.Server;
import cloud.timo.TimoCloud.core.sockets.Communicatable;
import cloud.timo.TimoCloud.core.sockets.CoreRSAHandshakeHandler;
import cloud.timo.TimoCloud.core.sockets.handler.CoreMessageHandler;
import io.netty.channel.Channel;

import java.net.InetAddress;

public class ServerHandshakeHandler extends CoreMessageHandler {
    public ServerHandshakeHandler() {
        super(MessageType.SERVER_HANDSHAKE);
    }

    @Override
    public void execute(Message message, Communicatable target, InetAddress address, Channel channel) {
        if (!(target instanceof Server)) {
            channel.close();
            return;
        }

        Server server = (Server) target;
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
