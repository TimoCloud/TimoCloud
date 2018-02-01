package cloud.timo.TimoCloud.core.objects;

import cloud.timo.TimoCloud.core.TimoCloudCore;
import cloud.timo.TimoCloud.core.sockets.Communicatable;
import io.netty.channel.Channel;
import org.json.simple.JSONObject;

public class Proxy implements Communicatable {

    private String name;
    private ProxyGroup group;
    private Base base;
    private String token;
    private int onlinePlayerCount;
    private Channel channel;

    public Proxy(String name, ProxyGroup group, Base base, String token) {
        this.name = name;
        this.group = group;
        this.base = base;
        this.token = token;
    }

    public String getName() {
        return name;
    }

    public ProxyGroup getGroup() {
        return group;
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

    @Override
    public void onMessage(JSONObject message) {
        String type = (String) message.get("type");
        String data = (String) message.get("data");
        switch (type) {
            case "STOP_PROXY":
                stop();
                break;
            case "PROXY_STARTED":
                setPort((int) message.get("port"));
                break;
            case "PROXY_NOT_STARTED":
                unregister();
                break;
            default:
                TimoCloudCore.getInstance().severe("Unknown proxy message type: '" + type + "'. Please report this.");
        }
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
