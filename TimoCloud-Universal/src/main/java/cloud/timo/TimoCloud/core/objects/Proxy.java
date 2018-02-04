package cloud.timo.TimoCloud.core.objects;

import cloud.timo.TimoCloud.core.TimoCloudCore;
import cloud.timo.TimoCloud.core.sockets.Communicatable;
import cloud.timo.TimoCloud.utils.HashUtil;
import io.netty.channel.Channel;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

public class Proxy implements Communicatable {

    private String name;
    private ProxyGroup group;
    private int port;
    private InetSocketAddress address;
    private Base base;
    private String token;
    private int onlinePlayerCount;
    private Channel channel;
    private boolean starting;
    private boolean registered;

    public Proxy(String name, ProxyGroup group, Base base, String token) {
        this.name = name;
        this.group = group;
        this.base = base;
        this.token = token;
        this.address = new InetSocketAddress(base.getAddress(), 0);
    }

    public void register() {
        if (isRegistered()) return;
        getGroup().onProxyConnect(this);
        this.starting = false;
        this.registered = true;
        TimoCloudCore.getInstance().info("Proxy " + getName() + " registered.");
    }

    public void unregister() {
        this.registered = false;
        getGroup().removeProxy(this);
        getBase().getProxies().remove(this);

        TimoCloudCore.getInstance().getSocketServerHandler().sendMessage(getBase().getChannel(), getName(), "PROXY_STOPPED", getToken());
    }

    public void start() {
        starting = true;
        JSONObject json = new JSONObject();
        json.put("type", "START_PROXY");
        json.put("name", getName());
        json.put("group", getGroup().getName());
        json.put("ram", getGroup().getRam());
        json.put("static", getGroup().isStatic());
        json.put("token", getToken());
        json.put("motd", getGroup().getMotd());
        json.put("maxplayers", getGroup().getMaxPlayers());
        json.put("maxplayersperproxy", getGroup().getPlayersPerProxy());
        if (! getGroup().isStatic()) {
            File templateDirectory = new File(TimoCloudCore.getInstance().getFileManager().getProxyTemplatesDirectory(), getGroup().getName());
            try {
                templateDirectory.mkdirs();
                json.put("templateHash", HashUtil.getHashes(templateDirectory));
                json.put("globalHash", HashUtil.getHashes(TimoCloudCore.getInstance().getFileManager().getProxyGlobalDirectory()));
            } catch (Exception e) {
                TimoCloudCore.getInstance().severe("Error while hashing files while starting proxy " + getName() + ": ");
                e.printStackTrace();
                return;
            }
        }
        try {
            getBase().sendMessage(json);
            getBase().setReady(false);
            getBase().setAvailableRam(getBase().getAvailableRam()-getGroup().getRam());
            TimoCloudCore.getInstance().info("Told base " + getBase().getName() + " to start proxy " + getName() + ".");
        } catch (Exception e) {
            TimoCloudCore.getInstance().severe("Error while starting proxy " + getName() + ": TimoCloudBase " + getBase().getName() + " not connected.");
            return;
        }
        getBase().getProxies().add(this);
        getGroup().addStartingProxy(this);
    }

    public void stop() {
        channel.close();
    }

    public void registerServer(Server server) {
        Map<String, Object> map = new HashMap<>();
        map.put("type", "ADD_SERVER");
        map.put("target", server.getName());
        map.put("address", server.getAddress().getAddress().getHostAddress());
        map.put("port", server.getPort());
        sendMessage(new JSONObject(map));
    }

    public void unregisterServer(Server server) {
        sendMessage(TimoCloudCore.getInstance().getSocketMessageManager().getMessage("REMOVE_SERVER", server.getName()));
    }

    @Override
    public void onMessage(JSONObject message) {
        String type = (String) message.get("type");
        Object data = message.get("data");
        switch (type) {
            case "STOP_PROXY":
                stop();
                break;
            case "PROXY_STARTED":
                setPort(((Long) message.get("port")).intValue());
                break;
            case "PROXY_NOT_STARTED":
                unregister();
                break;
            case "EXECUTE_COMMAND":
                executeCommand((String) data);
                break;
            case "REGISTER":
                register();
                break;
            case "SET_PLAYER_COUNT":
                this.onlinePlayerCount = ((Long) data).intValue();
                break;
            default:
                TimoCloudCore.getInstance().severe("Unknown proxy message type: '" + type + "'. Please report this.");
        }
    }

    @Override
    public void sendMessage(JSONObject message) {
        getChannel().writeAndFlush(message.toJSONString());
    }

    @Override
    public void onConnect(Channel channel) {
        setChannel(channel);
        TimoCloudCore.getInstance().info("Proxy " + getName() + " connected.");
    }

    @Override
    public void onDisconnect() {
        setChannel(null);
        TimoCloudCore.getInstance().info("Proxy " + getName() + " disconnected.");
        unregister();
    }

    public void executeCommand(String command) {
        sendMessage(TimoCloudCore.getInstance().getSocketMessageManager().getMessage("EXECUTE_COMMAND", command));
    }

    public String getName() {
        return name;
    }

    public ProxyGroup getGroup() {
        return group;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
        this.address = new InetSocketAddress(getAddress().getAddress(), port);
    }

    public InetSocketAddress getAddress() {
        return address;
    }

    public Base getBase() {
        return base;
    }

    public String getToken() {
        return token;
    }

    public int getOnlinePlayerCount() {
        return onlinePlayerCount;
    }

    @Override
    public Channel getChannel() {
        return this.channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public boolean isStarting() {
        return starting;
    }

    public boolean isRegistered() {
        return registered;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Proxy proxy = (Proxy) o;
        if (onlinePlayerCount != proxy.onlinePlayerCount) return false;
        if (name != null ? !name.equals(proxy.name) : proxy.name != null) return false;
        if (group != null ? !group.equals(proxy.group) : proxy.group != null) return false;
        if (base != null ? !base.equals(proxy.base) : proxy.base != null) return false;
        if (token != null ? !token.equals(proxy.token) : proxy.token != null) return false;
        return channel != null ? channel.equals(proxy.channel) : proxy.channel == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (group != null ? group.hashCode() : 0);
        result = 31 * result + (base != null ? base.hashCode() : 0);
        result = 31 * result + (token != null ? token.hashCode() : 0);
        result = 31 * result + onlinePlayerCount;
        result = 31 * result + (channel != null ? channel.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return getName();
    }
}
