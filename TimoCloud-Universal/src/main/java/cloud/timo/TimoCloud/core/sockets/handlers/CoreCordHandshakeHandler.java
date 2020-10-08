package cloud.timo.TimoCloud.core.sockets.handlers;

import cloud.timo.TimoCloud.common.protocol.Message;
import cloud.timo.TimoCloud.common.protocol.MessageType;
import cloud.timo.TimoCloud.common.sockets.MessageHandler;
import cloud.timo.TimoCloud.core.TimoCloudCore;
import cloud.timo.TimoCloud.core.objects.Cord;
import cloud.timo.TimoCloud.core.sockets.CoreRSAHandshakeHandler;
import io.netty.channel.Channel;

import java.net.InetAddress;
import java.net.InetSocketAddress;

public class CoreCordHandshakeHandler extends MessageHandler {
    public CoreCordHandshakeHandler() {
        super(MessageType.CORD_HANDSHAKE, true);
    }

    @Override
    public void execute(Message message, Channel channel) {
        String cordName = (String) message.get("cord");
        InetAddress address = channel == null ? null : ((InetSocketAddress) channel.remoteAddress()).getAddress();

        if (TimoCloudCore.getInstance().getInstanceManager().isCordConnected(cordName)) {
            TimoCloudCore.getInstance().severe("Error while cord handshake: A cord with the name '" + cordName + "' is already conencted.");
            return;
        }
        Cord cord = TimoCloudCore.getInstance().getInstanceManager().getOrCreateCord(cordName, address, channel);
        TimoCloudCore.getInstance().getSocketServerHandler().setCommunicatable(channel, cord);
        cord.onConnect(channel);
        cord.onHandshakeSuccess();
        channel.attr(CoreRSAHandshakeHandler.HANDSHAKE_PERFORMED_ATTRIBUTE_KEY).set(true);
    }
}
