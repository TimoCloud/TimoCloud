package cloud.timo.TimoCloud.core.objects;

import cloud.timo.TimoCloud.api.events.CordConnectEvent;
import cloud.timo.TimoCloud.api.events.CordDisconnectEvent;
import cloud.timo.TimoCloud.api.objects.CordObject;
import cloud.timo.TimoCloud.core.TimoCloudCore;
import cloud.timo.TimoCloud.core.api.CordObjectCoreImplementation;
import cloud.timo.TimoCloud.core.sockets.Communicatable;
import cloud.timo.TimoCloud.lib.protocol.Message;
import cloud.timo.TimoCloud.lib.protocol.MessageType;
import io.netty.channel.Channel;

import java.net.InetAddress;
import java.net.InetSocketAddress;

public class Cord implements Communicatable, Identifiable {

    private String id;
    private String name;
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
        TimoCloudCore.getInstance().getEventManager().fireEvent(new CordConnectEvent(toCordObject()));
    }

    @Override
    public void onDisconnect() {
        this.channel = null;
        this.connected = false;
        TimoCloudCore.getInstance().info("TimoCloudCord " + getName() + " disconnected.");
        TimoCloudCore.getInstance().getEventManager().fireEvent(new CordDisconnectEvent(toCordObject()));
    }

    @Override
    public Channel getChannel() {
        return channel;
    }

    @Override
    public void onMessage(Message message, Communicatable sender) {
        MessageType type = message.getType();
        Object data = message.getData();
        switch (type) {
            default:
                sendMessage(message);
                break;
        }
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
}
