package at.TimoCraft.TimoCloud.bungeecord.objects;

import at.TimoCraft.TimoCloud.api.objects.ServerObject;
import at.TimoCraft.TimoCloud.api.objects.ServerObjectBasicImplementation;
import at.TimoCraft.TimoCloud.bungeecord.TimoCloud;
import at.TimoCraft.TimoCloud.bungeecord.api.ServerObjectBungeeImplementation;
import io.netty.channel.Channel;
import net.md_5.bungee.api.config.ServerInfo;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * Created by Timo on 27.12.16.
 */
public class Server {

    private ServerInfo serverInfo;
    private boolean registered = false;
    private String name;
    private int port;
    private Group group;
    private Channel channel;
    private String state = "STARTING";
    private String extra = "";
    private String motd = "";
    private int currentPlayers = 0;
    private int maxPlayers = 0;
    private String map = "";
    private String token;

    public Server(String name, Group group, int port, String token) {
        this.port = port;
        this.name = name;
        this.group = group;
        InetSocketAddress address = new InetSocketAddress(getGroup().getBase().getAddress(), getPort());
        serverInfo = TimoCloud.getInstance().getProxy().constructServerInfo(name, address, name, false);
        this.token = token;
    }

    public boolean isStatic() {
        return getGroup().isStatic();
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
        group.onServerConnect(this);
        TimoCloud.getInstance().getServerManager().addServer(getName());
        registered = true;
        TimoCloud.info("Server " + getName() + " connected.");
        setState("ONLINE");
    }

    public void unregister() {
        if (!isRegistered()) {
            return;
        }
        TimoCloud.getInstance().getServerManager().removeServer(getName());
        getGroup().removeServer(this);
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
        }, 60, 0, TimeUnit.SECONDS);
        TimoCloud.getInstance().getSocketServerHandler().sendMessage(getGroup().getBase().getChannel(), getName(), "SERVERSTOPPED", "");
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

        Server that = (Server) o;

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

    public Group getGroup() {
        return group;
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

    public int getCurrentPlayers() {
        return currentPlayers;
    }

    public void setCurrentPlayers(int currentPlayers) {
        this.currentPlayers = currentPlayers;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public String getMap() {
        return map;
    }

    public void setMap(String map) {
        this.map = map;
    }

    public String getToken() {
        return token;
    }

    public ServerObject toServerObject() {
        return (ServerObjectBasicImplementation) new ServerObjectBungeeImplementation(
                getName(),
                getGroup().getName(),
                getToken(),
                getState(),
                getExtra(),
                getMotd(),
                getMap(),
                getCurrentPlayers(),
                getMaxPlayers(),
                getServerInfo().getAddress()
        );
    }
}
