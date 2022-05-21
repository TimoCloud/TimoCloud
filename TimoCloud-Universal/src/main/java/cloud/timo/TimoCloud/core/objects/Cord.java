package cloud.timo.TimoCloud.core.objects;

import cloud.timo.TimoCloud.api.events.cord.CordConnectEventBasicImplementation;
import cloud.timo.TimoCloud.api.events.cord.CordDisconnectEventBasicImplementation;
import cloud.timo.TimoCloud.api.internal.links.CordObjectLink;
import cloud.timo.TimoCloud.api.objects.CordObject;
import cloud.timo.TimoCloud.common.protocol.Message;
import cloud.timo.TimoCloud.common.protocol.MessageType;
import cloud.timo.TimoCloud.core.TimoCloudCore;
import cloud.timo.TimoCloud.core.api.CordObjectCoreImplementation;
import cloud.timo.TimoCloud.core.sockets.Communicatable;
import io.netty.channel.Channel;
import lombok.Getter;
import lombok.Setter;

import java.net.InetAddress;
import java.net.InetSocketAddress;

public class Cord implements Communicatable, Identifiable {

    @Getter
    private final String id;
    @Getter
    private final String name;
    @Getter
    @Setter
    private InetAddress address;
    @Getter
    @Setter
    private int port;
    @Getter
    @Setter
    private Channel channel;
    @Getter
    private boolean connected;

    public Cord(String name, InetAddress address, Channel channel) {
        this.id = name; // TODO Generate IDs
        this.name = name;
        this.address = address;
        this.channel = channel;
    }

    @Override
    public void onConnect(Channel channel) {
        this.channel = channel;
        this.connected = true;
        TimoCloudCore.getInstance().info("TimoCloudCord " + getName() + " connected.");
        TimoCloudCore.getInstance().getEventManager().fireEvent(new CordConnectEventBasicImplementation(toCordObject()));
    }

    @Override
    public void onDisconnect() {
        this.channel = null;
        this.connected = false;
        TimoCloudCore.getInstance().info("TimoCloudCord " + getName() + " disconnected.");
        TimoCloudCore.getInstance().getEventManager().fireEvent(new CordDisconnectEventBasicImplementation(toCordObject()));
    }

    @Override
    public void onMessage(Message message, Communicatable sender) {
        MessageType type = message.getType();
        Object data = message.getData();
        sendMessage(message);
    }

    @Override
    public void sendMessage(Message message) {
        if (getChannel() != null) getChannel().writeAndFlush(message.toJson());
    }

    @Override
    public void onHandshakeSuccess() {
        sendMessage(Message.create().setType(MessageType.CORD_HANDSHAKE_SUCCESS));
    }

    public InetSocketAddress getSocketAddress() {
        return new InetSocketAddress(getAddress(), getPort());
    }

    public CordObject toCordObject() {
        return new CordObjectCoreImplementation(getId(), getName(), getSocketAddress(), isConnected());
    }

    public CordObjectLink toLink() {
        return new CordObjectLink(getId(), getName());
    }
}
