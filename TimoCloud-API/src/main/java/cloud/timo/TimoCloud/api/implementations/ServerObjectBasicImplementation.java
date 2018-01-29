package cloud.timo.TimoCloud.api.implementations;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.objects.ServerGroupObject;
import cloud.timo.TimoCloud.api.objects.ServerObject;

import java.net.InetAddress;
import java.net.InetSocketAddress;

public class ServerObjectBasicImplementation implements ServerObject, Comparable {

    private String name;
    private String group;
    private String token;
    protected String state;
    protected String extra;
    private String map;
    private String motd;
    private int onlinePlayerCount;
    private int maxPlayerCount;
    private String base;
    private InetSocketAddress socketAddress;

    public ServerObjectBasicImplementation() {}

    public ServerObjectBasicImplementation(String name, String group, String token, String state, String extra, String map, String motd, int onlinePlayerCount, int maxPlayerCount, String base, InetSocketAddress socketAddress) {
        this.name = name;
        this.group = group;
        this.token = token;
        this.state = state;
        this.extra = extra;
        this.map = map;
        this.motd = motd;
        this.onlinePlayerCount = onlinePlayerCount;
        this.maxPlayerCount = maxPlayerCount;
        this.base = base;
        this.socketAddress = socketAddress;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ServerGroupObject getGroup() {
        return TimoCloudAPI.getUniversalInstance().getGroup(group);
    }

    protected void setGroup(String group) {
        this.group = group;
    }

    public String getToken() {
        return token;
    }

    @Override
    public String getState() {
        return state;
    }

    @Override
    public void setState(String state) {
        this.state = state;
    }

    @Override
    public String getExtra() {
        return extra;
    }

    @Override
    public void setExtra(String extra) {
        this.extra = extra;
    }

    @Override
    public String getMap() {
        return map;
    }

    @Override
    public String getMotd() {
        return motd;
    }

    @Override
    public int getOnlinePlayerCount() {
        return onlinePlayerCount;
    }

    @Override
    public int getMaxPlayerCount() {
        return maxPlayerCount;
    }

    @Override
    public String getBase() {
        return base;
    }

    @Override
    public InetSocketAddress getSocketAddress() {
        return socketAddress;
    }

    @Override
    public InetAddress getIpAddress() {
        return getSocketAddress().getAddress();
    }

    @Override
    public int getPort() {
        return getSocketAddress().getPort();
    }

    @Override
    public boolean isSortedOut() {
        return getGroup().getSortOutStates().contains(getState());
    }

    @Override
    public void executeCommand(String command) {}

    @Override
    public void stop() {
        executeCommand("stop");
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public int compareTo(Object o) {
        if (! (o instanceof ServerObject)) return 1;
        ServerObject so = (ServerObject) o;
        try {
            return Integer.parseInt(getName().split("-")[getName().split("-").length-1])-Integer.parseInt(so.getName().split("-")[so.getName().split("-").length-1]);
        } catch (Exception e) {
            return getName().compareTo(so.getName());
        }
    }
}
