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

import java.net.InetAddress;
import java.net.InetSocketAddress;

public class Cord implements Communicatable, Identifiable {

    private final String id;
    private final String name;
    private InetAddress address;
    private int port;
    private Channel channel;
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
    public Channel getChannel() {
        return channel;
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

    @Override
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public InetAddress getAddress() {
        return address;
    }

    public void setAddress(InetAddress address) {
        this.address = address;
    }

    public InetSocketAddress getSocketAddress() {
        return new InetSocketAddress(getAddress(), getPort());
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    @Override
    public boolean isConnected() {
        return connected;
    }

    public CordObject toCordObject() {
        return new CordObjectCoreImplementation(getId(), getName(), getSocketAddress(), isConnected());
    }

    public CordObjectLink toLink() {
        return new CordObjectLink(getId(), getName());
    }
}
