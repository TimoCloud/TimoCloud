package cloud.timo.TimoCloud.bungeecord.sockets.handler;

import cloud.timo.TimoCloud.bungeecord.TimoCloudBungee;
import cloud.timo.TimoCloud.common.protocol.Message;
import cloud.timo.TimoCloud.common.protocol.MessageType;
import cloud.timo.TimoCloud.common.sockets.MessageHandler;
import cloud.timo.TimoCloud.common.utils.network.InetAddressUtil;
import io.netty.channel.Channel;

public class ProxyCordSetIPHandler extends MessageHandler {
    public ProxyCordSetIPHandler() {
        super(MessageType.CORD_SET_IP);
    }

    @Override
    public void execute(Message message, Channel channel) {
        try {
            TimoCloudBungee.getInstance().getIpManager().setAddresses(
                    InetAddressUtil.getSocketAddressByName((String) message.get("CHANNEL_ADDRESS")),
                    InetAddressUtil.getSocketAddressByName((String) message.get("CLIENT_ADDRESS")));
        } catch (Exception e) {
            TimoCloudBungee.getInstance().severe("Error while parsing IP addresses (" + message.get("CHANNEL_ADDRESS") + ", " + message.get("CLIENT_ADDRESS") + "): ");
            TimoCloudBungee.getInstance().severe(e);
        }
    }
}
