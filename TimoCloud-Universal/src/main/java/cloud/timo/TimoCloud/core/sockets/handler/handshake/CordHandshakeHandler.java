package cloud.timo.TimoCloud.core.sockets.handler.handshake;

import cloud.timo.TimoCloud.common.protocol.Message;
import cloud.timo.TimoCloud.common.protocol.MessageType;
import cloud.timo.TimoCloud.core.TimoCloudCore;
import cloud.timo.TimoCloud.core.objects.Cord;
import cloud.timo.TimoCloud.core.sockets.Communicatable;
import cloud.timo.TimoCloud.core.sockets.CoreRSAHandshakeHandler;
import cloud.timo.TimoCloud.core.sockets.handler.CoreMessageHandler;
import io.netty.channel.Channel;

import java.net.InetAddress;

public class CordHandshakeHandler extends CoreMessageHandler {
    public CordHandshakeHandler() {
        super(MessageType.CORD_HANDSHAKE);
    }

    @Override
    public void execute(Message message, Communicatable target, InetAddress address, Channel channel) {
        String cordName = (String) message.get("cord");

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
