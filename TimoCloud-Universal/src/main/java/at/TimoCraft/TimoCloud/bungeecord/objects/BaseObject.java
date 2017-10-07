package at.TimoCraft.TimoCloud.bungeecord.objects;

import io.netty.channel.Channel;

import java.net.InetAddress;

public class BaseObject {
    private String name;
    private InetAddress address;
    private Channel channel;
    private boolean connected;

    public BaseObject(String name, InetAddress address, Channel channel) {
        this.name = name;
        this.address = address;
        this.channel = channel;
    }

    public String getName() {
        return name;
    }

    public InetAddress getAddress() {
        return address;
    }

    public Channel getChannel() {
        return channel;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }
}
