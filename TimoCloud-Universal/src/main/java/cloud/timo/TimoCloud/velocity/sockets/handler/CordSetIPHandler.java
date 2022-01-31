package cloud.timo.TimoCloud.velocity.sockets.handler;

import cloud.timo.TimoCloud.bungeecord.TimoCloudBungee;
import cloud.timo.TimoCloud.common.protocol.Message;
import cloud.timo.TimoCloud.common.protocol.MessageType;
import cloud.timo.TimoCloud.common.sockets.handler.MessageHandler;
import cloud.timo.TimoCloud.common.utils.network.InetAddressUtil;
import cloud.timo.TimoCloud.velocity.TimoCloudVelocity;
import io.netty.channel.Channel;

public class CordSetIPHandler extends MessageHandler {
    public CordSetIPHandler() {
        super(MessageType.CORD_SET_IP);
    }

    @Override
    public void execute(Message message, Channel channel) {
        try {
            TimoCloudVelocity.getInstance().getIpManager().setAddresses(
                    InetAddressUtil.getSocketAddressByName((String) message.get("CHANNEL_ADDRESS")),
                    InetAddressUtil.getSocketAddressByName((String) message.get("CLIENT_ADDRESS")));
        } catch (Exception e) {
            TimoCloudVelocity.getInstance().severe("Error while parsing IP addresses (" + message.get("CHANNEL_ADDRESS") + ", " + message.get("CLIENT_ADDRESS") + "): ");
            TimoCloudVelocity.getInstance().severe(e);
        }
    }
}
