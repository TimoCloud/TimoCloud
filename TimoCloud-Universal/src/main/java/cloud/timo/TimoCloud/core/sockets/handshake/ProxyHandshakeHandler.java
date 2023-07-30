package cloud.timo.TimoCloud.core.sockets.handshake;

import cloud.timo.TimoCloud.common.protocol.Message;
import cloud.timo.TimoCloud.common.protocol.MessageType;
import cloud.timo.TimoCloud.core.TimoCloudCore;
import cloud.timo.TimoCloud.core.objects.Proxy;
import cloud.timo.TimoCloud.core.sockets.Communicatable;
import cloud.timo.TimoCloud.core.sockets.CoreRSAHandshakeHandler;
import cloud.timo.TimoCloud.core.sockets.handler.CoreMessageHandler;
import io.netty.channel.Channel;

import java.net.InetAddress;

public class ProxyHandshakeHandler extends CoreMessageHandler {
    public ProxyHandshakeHandler() {
        super(MessageType.PROXY_HANDSHAKE);
    }

    @Override
    public void execute(Message message, Communicatable target, InetAddress address, Channel channel) {
        if (!(target instanceof Proxy)) {
            channel.close();
            return;
        }

        Proxy proxy = (Proxy) target;
        if (! (address.equals(proxy.getBase().getAddress()) || address.equals(proxy.getBase().getPublicAddress()))) {
            TimoCloudCore.getInstance().severe("Proxy connected with different InetAddress than its base. Refusing connection.");
            return;
        }
        if (! channel.attr(CoreRSAHandshakeHandler.RSA_KEY_ATTRIBUTE_KEY).get().equals(proxy.getPublicKey())) {
            TimoCloudCore.getInstance().severe(String.format("Proxy %s connected with wrong public key. Please report this.", proxy.getName()));
            return;
        }
        TimoCloudCore.getInstance().getSocketServerHandler().setCommunicatable(channel, proxy);
        proxy.onConnect(channel);
        proxy.onHandshakeSuccess();
        channel.attr(CoreRSAHandshakeHandler.HANDSHAKE_PERFORMED_ATTRIBUTE_KEY).set(true);
        return;
    }
}