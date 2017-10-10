package at.TimoCraft.TimoCloud.api.objects;

import at.TimoCraft.TimoCloud.api.TimoCloudAPI;

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
    private int currentPlayers;
    private int maxPlayers;
    private InetSocketAddress socketAddress;

    public ServerObjectBasicImplementation() {}

    public ServerObjectBasicImplementation(String name, String group, String token, String state, String extra, String map, String motd, int currentPlayers, int maxPlayers, InetSocketAddress socketAddress) {
        this.name = name;
        this.group = group;
        this.token = token;
        this.state = state;
        this.extra = extra;
        this.motd = motd;
        this.map = map;
        this.currentPlayers = currentPlayers;
        this.maxPlayers = maxPlayers;
        this.socketAddress = socketAddress;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public GroupObject getGroup() {
        return TimoCloudAPI.getUniversalInstance().getGroup(group);
    }

    @Override
    public String getGroupName() {
        return group;
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
    public int getCurrentPlayers() {
        return currentPlayers;
    }

    @Override
    public int getMaxPlayers() {
        return maxPlayers;
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
