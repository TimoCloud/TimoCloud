package cloud.timo.TimoCloud.core.sockets.handshake;

import cloud.timo.TimoCloud.common.protocol.Message;
import cloud.timo.TimoCloud.common.protocol.MessageType;
import cloud.timo.TimoCloud.core.TimoCloudCore;
import cloud.timo.TimoCloud.core.objects.Base;
import cloud.timo.TimoCloud.core.sockets.Communicatable;
import cloud.timo.TimoCloud.core.sockets.CoreRSAHandshakeHandler;
import cloud.timo.TimoCloud.core.sockets.handler.CoreMessageHandler;
import io.netty.channel.Channel;

import java.net.InetAddress;
import java.security.PublicKey;

public class BaseHandshakeHandler extends CoreMessageHandler {
    public BaseHandshakeHandler() {
        super(MessageType.BASE_HANDSHAKE);
    }

    @Override
    public void execute(Message message, Communicatable target, InetAddress address, Channel channel) {
        String baseName = (String) message.get("base");

        if (TimoCloudCore.getInstance().getInstanceManager().isBaseConnected(baseName)) {
            TimoCloudCore.getInstance().severe("Error while base handshake: A base with the name '" + baseName + "' is already conencted.");
            return;
        }
        InetAddress publicAddress = address;
        try {
            publicAddress = InetAddress.getByName((String) message.get("publicAddress"));
        } catch (Exception e) {
            TimoCloudCore.getInstance().severe("Unable to resolve public ip address '" + message.get("publicAddress") + "' for base " + baseName + ". Please make sure the base's hostname is configured correctly in your operating system.");
        }
        PublicKey publicKey = channel.attr(CoreRSAHandshakeHandler.RSA_KEY_ATTRIBUTE_KEY).get();

        if (! TimoCloudCore.getInstance().getCorePublicKeyManager().redeemBaseKeyIfPermitted(publicKey)) {
            channel.close();
            return;
        }

        Base base = TimoCloudCore.getInstance().getInstanceManager().getBaseByPublicKey(publicKey);
        if (base == null) { // First connection
            base = TimoCloudCore.getInstance().getInstanceManager().createBase(publicKey);
        }
        String publicIpConfig = base.getPublicIpConfig();
        if(!publicIpConfig.equalsIgnoreCase("AUTO")) {
            try {
                publicAddress = InetAddress.getByName(publicIpConfig);
            } catch (Exception e) {
                TimoCloudCore.getInstance().severe("Unable to resolve public ip address from bases.yml '" + publicIpConfig + "' for base " + baseName + ". Please make sure the base's hostname is configured correctly in your bases.yml.");
            }
        }
        TimoCloudCore.getInstance().getSocketServerHandler().setCommunicatable(channel, base);
        base.onConnect(channel, address, publicAddress);
        base.onHandshakeSuccess();
        channel.attr(CoreRSAHandshakeHandler.HANDSHAKE_PERFORMED_ATTRIBUTE_KEY).set(true);
    }
}