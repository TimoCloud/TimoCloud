package at.TimoCraft.TimoCloud.bungeecord.objects;

import at.TimoCraft.TimoCloud.bungeecord.TimoCloud;
import io.netty.channel.Channel;
import net.md_5.bungee.api.config.ServerInfo;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * Created by Timo on 27.12.16.
 */
public class TemporaryServer {
    private ServerInfo serverInfo;
    private boolean registered = false;
    private String name;
    private int port;
    private ServerGroup serverGroup;
    private Channel channel;
    private String state = "STARTING";
    private String extra = "";
    private String motd = "";
    private String players = "0/0";
    private String map = "";

    public TemporaryServer(String name, ServerGroup serverGroup, int port) {
        this.port = port;
        this.name = name;
        this.serverGroup = serverGroup;
        InetSocketAddress address = new InetSocketAddress(getServerGroup().getBase().getAddress(), getPort());
        serverInfo = TimoCloud.getInstance().getProxy().constructServerInfo(name, address, name, false);
    }

    public void stop() {
        TimoCloud.info("Stopping server " + getName() + "...");
        if (channel == null) {
            TimoCloud.info("Did not stop server " + getName() + " because it was not connected. Will stop itself.");
            return;
        }
        channel.close();
        TimoCloud.info("Stopped " + getName() + ".");
    }

    public void register() {
        TimoCloud.getInstance().getProxy().getServers().put(getServerInfo().getName(), getServerInfo());
        serverGroup.getStartingServers().remove(this);
        serverGroup.getTemporaryServers().add(this);
        TimoCloud.getInstance().getServerManager().addServer(getName());
        registered = true;
        TimoCloud.info("Server " + getName() + " connected.");
        setState("ONLINE");
    }

    public void unregister() {
        if (!isRegistered()) {
            TimoCloud.severe("Wanted to unregister not-registered server: " + serverInfo.getName());
            return;
        }
        TimoCloud.getInstance().getServerManager().removeServer(getName());
        getServerGroup().removeServer(this);
        setState("OFFLINE");
        registered = false;
        if (channel != null && channel.isOpen()) {
            channel.close();
        }
        TimoCloud.info("Server " + getName() + " disconnected.");
        if (TimoCloud.getInstance().isShuttingDown()) {
            return;
        }
        TimoCloud.getInstance().getProxy().getScheduler().schedule(TimoCloud.getInstance(), () -> {
            TimoCloud.getInstance().getServerManager().unregisterPort(getPort());
        }, 1, 0, TimeUnit.SECONDS);
        TimoCloud.getInstance().getSocketServerHandler().sendMessage(getServerGroup().getBase().getChannel(), getName(), "SERVERSTOPPED", "");
    }

    public ServerInfo getServerInfo() {
        return serverInfo;
    }

    public String getName() {
        return name;
    }

    public boolean isRegistered() {
        return registered;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TemporaryServer that = (TemporaryServer) o;

        if (port != that.port) return false;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + port;
        return result;
    }

    @Override
    public String toString() {
        return getName();
    }

    public ServerGroup getServerGroup() {
        return serverGroup;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public String getMotd() {
        return motd;
    }

    public void setMotd(String motd) {
        this.motd = motd;
    }

    public int getPort() {
        return port;
    }

    public String getPlayers() {
        return players;
    }

    public void setPlayers(String players) {
        this.players = players;
    }

    public String getMap() {
        return map;
    }

    public void setMap(String map) {
        this.map = map;
    }
}
