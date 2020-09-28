package cloud.timo.TimoCloud.core.sockets.handlers;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.bukkit.api.TimoCloudUniversalAPIBukkitImplementation;
import cloud.timo.TimoCloud.common.protocol.Message;
import cloud.timo.TimoCloud.common.protocol.MessageType;
import cloud.timo.TimoCloud.common.sockets.MessageHandler;
import cloud.timo.TimoCloud.core.TimoCloudCore;
import cloud.timo.TimoCloud.core.objects.Proxy;
import cloud.timo.TimoCloud.core.objects.Server;
import cloud.timo.TimoCloud.core.sockets.CoreRSAHandshakeHandler;
import io.netty.channel.Channel;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Map;

public class CoreProxyHandshakeHandler extends MessageHandler {
    public CoreProxyHandshakeHandler() {
        super(MessageType.PROXY_HANDSHAKE);
    }

    @Override
    public void execute(Message message, Channel channel) {
        String targetId = message.getTarget();
        Proxy proxy = TimoCloudCore.getInstance().getInstanceManager().getProxyByIdentifier(targetId);
        InetAddress address = channel == null ? null : ((InetSocketAddress) channel.remoteAddress()).getAddress();

        if (proxy == null) {
            channel.close();
            return;
        }
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
    }
}
