package cloud.timo.TimoCloud.core.objects;

import cloud.timo.TimoCloud.api.events.ServerRegisterEvent;
import cloud.timo.TimoCloud.api.events.ServerUnregisterEvent;
import cloud.timo.TimoCloud.api.objects.PlayerObject;
import cloud.timo.TimoCloud.api.objects.ServerObject;
import cloud.timo.TimoCloud.core.TimoCloudCore;
import cloud.timo.TimoCloud.core.api.ServerObjectCoreImplementation;
import cloud.timo.TimoCloud.core.sockets.Communicatable;
import cloud.timo.TimoCloud.lib.utils.HashUtil;
import io.netty.channel.Channel;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

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
    private List<PlayerObject> onlinePlayers;
    private int onlinePlayerCount = 0;
    private int maxPlayers = 0;
    private String map = "";
    private String token;
    private boolean starting;

    public Server(String name, ServerGroup group, Base base, String map, String token) {
        this.name = name;
        this.group = group;
        this.base = base;
        this.address = new InetSocketAddress(base.getAddress(), 0);
        this.onlinePlayers = new ArrayList<>();
        this.map = map;
        this.token = token;
    }

    public boolean isStatic() {
        return getGroup().isStatic();
    }

    public void start() {
        this.starting = true;
        JSONObject json = new JSONObject();
        json.put("type", "START_SERVER");
        json.put("name", getName());
        json.put("group", getGroup().getName());
        json.put("ram", getGroup().getRam());
        json.put("static", getGroup().isStatic());
        if (getMap() != null) json.put("map", getMap());
        json.put("token", getToken());
        if (! getGroup().isStatic()) {
            File templateDirectory = new File(TimoCloudCore.getInstance().getFileManager().getServerTemplatesDirectory(), getGroup().getName());
            File mapDirectory = new File(TimoCloudCore.getInstance().getFileManager().getServerTemplatesDirectory(), getGroup().getName() + "_" + getMap());
            try {
                templateDirectory.mkdirs();
                if (getMap() != null) mapDirectory.mkdirs();
                json.put("templateHash", HashUtil.getHashes(templateDirectory));
                if (getMap() != null) json.put("mapHash", HashUtil.getHashes(mapDirectory));
                json.put("globalHash", HashUtil.getHashes(TimoCloudCore.getInstance().getFileManager().getServerGlobalDirectory()));
            } catch (IOException e) {
                TimoCloudCore.getInstance().severe("Error while hashing files while starting server " + getName() + ": ");
                e.printStackTrace();
                return;
            }
        }
        try {
            getBase().sendMessage(json);
            getBase().setReady(false);
            getBase().setAvailableRam(getBase().getAvailableRam()-getGroup().getRam());
            TimoCloudCore.getInstance().info("Told base " + getBase().getName() + " to start server " + getName() + ".");
        } catch (Exception e) {
            TimoCloudCore.getInstance().severe("Error while starting server " + getName() + ": TimoCloudBase " + getBase().getName() + " not connected.");
            return;
        }
        getGroup().addStartingServer(this);
        getBase().getServers().add(this);
    }

    public void stop() {
        if (channel == null) unregister();
        else channel.close();
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
        for (ProxyGroup proxyGroup : TimoCloudCore.getInstance().getServerManager().getProxyGroups()) {
            if (! proxyGroup.getServerGroups().contains(getGroup())) continue;
            proxyGroup.registerServer(this);
        }
        this.starting = false;
        this.registered = true;
        TimoCloudCore.getInstance().info("Server " + getName() + " registered.");
        TimoCloudCore.getInstance().getEventManager().fireEvent(new ServerRegisterEvent(toServerObject()));
    }

    public void unregister() {
        if (!isRegistered()) return;
        getGroup().removeServer(this);
        getBase().getServers().remove(this);
        setState("OFFLINE");
        for (ProxyGroup proxyGroup : TimoCloudCore.getInstance().getServerManager().getProxyGroups()) {
            if (! proxyGroup.getServerGroups().contains(getGroup())) continue;
            proxyGroup.unregisterServer(this);
        }
        this.registered = false;
        TimoCloudCore.getInstance().getSocketServerHandler().sendMessage(getBase().getChannel(), getName(), "SERVER_STOPPED", getToken());
        TimoCloudCore.getInstance().getEventManager().fireEvent(new ServerUnregisterEvent(toServerObject()));
    }

    public void onPlayerConnect(PlayerObject playerObject) {
        if (! getOnlinePlayers().contains(playerObject)) getOnlinePlayers().add(playerObject);
    }

    public void onPlayerDisconnect(PlayerObject playerObject) {
        if (getOnlinePlayers().contains(playerObject)) getOnlinePlayers().remove(playerObject);
    }

    @Override
    public void onMessage(JSONObject message) {
        String type = (String) message.get("type");
        Object data = message.get("data");
        switch (type) {
            case "SET_STATE":
                setState((String) data);
                break;
            case "SET_EXTRA":
                setExtra((String) data);
                break;
            case "SET_MOTD":
                setMotd((String) data);
                break;
            case "SET_MAP":
                setMap((String) data);
                break;
            case "SET_PLAYERS":
                setOnlinePlayerCount(Integer.parseInt(((String) data).split("/")[0]));
                setMaxPlayers(Integer.parseInt(((String) data).split("/")[1]));
                break;
            case "EXECUTE_COMMAND":
                executeCommand((String) data);
                break;
            case "STOP_SERVER":
                stop();
                break;
            case "SERVER_STARTED":
                setPort(((Number) message.get("port")).intValue());
                break;
            case "SERVER_NOT_STARTED":
                unregister();
                break;
            case "REGISTER":
                register();
                break;
            default:
                sendMessage(message);
        }
    }

    @Override
    public void sendMessage(JSONObject message) {
        if (getChannel() != null) getChannel().writeAndFlush(message.toString());
    }

    @Override
    public void onHandshakeSuccess() {
        TimoCloudCore.getInstance().getSocketServerHandler().sendMessage(getChannel(), "HANDSHAKE_SUCCESS", null);
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

    public List<PlayerObject> getOnlinePlayers() {
        return onlinePlayers;
    }

    public int getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
        setAddress(new InetSocketAddress(getAddress().getAddress(), port));
    }

    public int getOnlinePlayerCount() {
        return onlinePlayerCount;
    }

    public void setOnlinePlayerCount(int onlinePlayerCount) {
        this.onlinePlayerCount = onlinePlayerCount;
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
                getOnlinePlayers(),
                getOnlinePlayerCount(),
                getMaxPlayers(),
                getBase() == null ? null : getBase().getName(),
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
