package cloud.timo.TimoCloud.bungeecord.sockets.handler;

import cloud.timo.TimoCloud.bungeecord.TimoCloudBungee;
import cloud.timo.TimoCloud.common.protocol.Message;
import cloud.timo.TimoCloud.common.protocol.MessageType;
import cloud.timo.TimoCloud.common.sockets.handler.MessageHandler;
import cloud.timo.TimoCloud.common.utils.network.InetAddressUtil;
import io.netty.channel.Channel;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetSocketAddress;
import java.util.Base64;

public class CordSetIPHandler extends MessageHandler {
    public CordSetIPHandler() {
        super(MessageType.CORD_SET_IP);
    }

    @Override
    public void execute(Message message, Channel channel) {
        try {
            InetSocketAddress clientAddress = (InetSocketAddress) fromString((String) message.get("CLIENT_ADDRESS"));
            TimoCloudBungee.getInstance().getIpManager().setAddresses(
                    InetAddressUtil.getSocketAddressByName((String) message.get("CHANNEL_ADDRESS")),
                    clientAddress);

        } catch (Exception e) {
            TimoCloudBungee.getInstance().severe("Error while parsing IP addresses (" + message.get("CHANNEL_ADDRESS") + ", " + message.get("CLIENT_ADDRESS") + "): ");
            TimoCloudBungee.getInstance().severe(e);
        }
    }

    /** Read the object from Base64 string. */
    private static Object fromString( String s )  {
        byte [] data = Base64.getDecoder().decode( s );
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(
                    new ByteArrayInputStream(  data ) );
            Object o  = ois.readObject();
            ois.close();
            return o;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
