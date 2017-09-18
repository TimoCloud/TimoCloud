package at.TimoCraft.TimoCloud.api.objects;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.InetSocketAddress;

/**
 * Created by Timo on 02.09.17.
 */
public class ServerObject implements Serializable {
    private String name;
    private GroupObject group;
    private String state;
    private String extra;
    private String motd;
    private String map = "";
    private int currentPlayers;
    private int maxPlayers;
    private InetSocketAddress socketAddress;

    public ServerObject(String name, GroupObject group, String state, String extra, String motd, String map, int currentPlayers, int maxPlayers, InetSocketAddress socketAddress) {
        this.name = name;
        this.group = group;
        this.state = state;
        this.extra = extra;
        this.motd = motd;
        this.map = map;
        this.currentPlayers = currentPlayers;
        this.maxPlayers = maxPlayers;
        this.socketAddress = socketAddress;
    }

    public String getName() {
        return name;
    }

    public GroupObject getGroup() {
        return group;
    }

    protected void setGroup(GroupObject group) {
        this.group = group;
    }

    public String getState() {
        return state;
    }

    public String getExtra() {
        return extra;
    }

    public String getMotd() {
        return motd;
    }

    public String getMap() {
        return map;
    }

    public int getCurrentPlayers() {
        return currentPlayers;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public InetSocketAddress getSocketAddress() {
        return socketAddress;
    }

    public InetAddress getIpAddress() {
        return getSocketAddress().getAddress();
    }

    public int getPort() {
        return getSocketAddress().getPort();
    }

    public boolean isSortedOut() {
        return getGroup().getSortOutStates().contains(getState());
    }
}
