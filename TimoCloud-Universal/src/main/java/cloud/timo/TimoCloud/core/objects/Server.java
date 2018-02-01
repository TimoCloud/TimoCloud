package cloud.timo.TimoCloud.core.objects;

import cloud.timo.TimoCloud.api.objects.ServerObject;
import cloud.timo.TimoCloud.core.TimoCloudCore;
import cloud.timo.TimoCloud.core.api.ServerObjectCoreImplementation;
import cloud.timo.TimoCloud.core.sockets.Communicatable;
import io.netty.channel.Channel;
import org.json.simple.JSONObject;

import java.net.InetSocketAddress;

public class Server implements Communicatable {

    private boolean registered = false;
    private String name;
    private Integer port;
    private ServerGroup group;
    private Channel channel;
    private Base base;
    private InetSocketAddress address;
    private String state = "STARTING";
    private String extra = "";
    private String motd = "";
    private int currentPlayers = 0;
    private int maxPlayers = 0;
    private String map = "";
    private String token;
    private boolean starting;

    public Server(String name, ServerGroup group, Base base, String map, String token) {
        this.name = name;
        this.group = group;
        this.base = base;
        this.address = new InetSocketAddress(base.getAddress(), getPort());
        this.map = map;
        this.token = token;
    }

    public boolean isStatic() {
        return getGroup().isStatic();
    }

    public void onStart() {
        this.starting = true;
        getBase().getServers().add(this);
    }

    public void stop() {
        TimoCloudCore.getInstance().info("Stopping server " + getName() + "...");
        if (channel == null) {
            TimoCloudCore.getInstance().info("Did not stop server " + getName() + " because it was not connected. Will stop itself.");
            return;
        }
        channel.close();
        TimoCloudCore.getInstance().info("Stopped " + getName() + ".");
    }

    @Override
    public void onConnect(Channel channel) {
        setChannel(channel);
        TimoCloudCore.getInstance().info("Server " + getName() + " connected.");
    }

    @Override
    public void onDisconnect() {
        setChannel(null);
        TimoCloudCore.getInstance().info("Server " + getName() + " disconnected.");
        unregister();
    }

    public void register() {
        if (isRegistered()) return;
        getGroup().onServerConnect(this);
        setState("ONLINE");
        this.starting = false;
        this.registered = true;
        TimoCloudCore.getInstance().info("Server " + getName() + " registered.");
    }

    public void unregister() {
        if (!isRegistered()) return;
        getGroup().removeServer(this);
        getBase().getServers().remove(this);
        setState("OFFLINE");
        this.registered = false;
        TimoCloudCore.getInstance().getSocketServerHandler().sendMessage(getBase().getChannel(), getName(), "SERVER_STOPPED", "");
    }

    @Override
    public void onMessage(JSONObject message) {
        String type = (String) message.get("type");
        String data = (String) message.get("data");
        switch (type) {
            case "SET_STATE":
                setState(data);
                break;
            case "SET_EXTRA":
                setExtra(data);
                break;
            case "SET_MOTD":
                setMotd(data);
                break;
            case "SET_MAP":
                setMap(data);
                break;
            case "SET_PLAYERS":
                setCurrentPlayers(Integer.parseInt(data.split("/")[0]));
                setMaxPlayers(Integer.parseInt(data.split("/")[1]));
                break;
            case "EXECUTE_COMMAND":
                executeCommand(data);
                break;
            case "STOP_SERVER":
                stop();
                break;
            case "SERVER_STARTED":
                setPort((int) message.get("port"));
                break;
            case "SERVER_NOT_STARTED":
                unregister();
                break;
            default:
                TimoCloudCore.getInstance().severe("Unknown server message type: '" + type + "'. Please report this.");
        }
    }

    public String getName() {
        return name;
    }

    public boolean isRegistered() {
        return registered;
    }


    public ServerGroup getGroup() {
        return group;
    }

    public Channel getChannel() {
        return channel;
    }

    public Base getBase() {
        return base;
    }

    public InetSocketAddress getAddress() {
        return address;
    }

    public void setAddress(InetSocketAddress address) {
        this.address = address;
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

    public void setPort(Integer port) {
        this.port = port;
        setAddress(new InetSocketAddress(getAddress().getAddress(), port));
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

    public boolean isStarting() {
        return starting;
    }

    public void executeCommand(String command) {
        TimoCloudCore.getInstance().getSocketServerHandler().sendMessage(channel, "EXECUTE_COMMAND", command);
    }

    public ServerObject toServerObject() {
        return new ServerObjectCoreImplementation(
                getName(),
                getGroup().getName(),
                getToken(),
                getState(),
                getExtra(),
                getMap(),
                getMotd(),
                getCurrentPlayers(),
                getMaxPlayers(),
                getAddress()
        );
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
}
