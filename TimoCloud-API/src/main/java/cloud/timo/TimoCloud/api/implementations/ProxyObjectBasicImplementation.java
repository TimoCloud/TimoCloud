package cloud.timo.TimoCloud.api.implementations;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.objects.PlayerObject;
import cloud.timo.TimoCloud.api.objects.ProxyGroupObject;
import cloud.timo.TimoCloud.api.objects.ProxyObject;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.List;

public class ProxyObjectBasicImplementation implements ProxyObject {

    private String name;
    private String group;
    private String token;
    private List<PlayerObject> onlinePlayers;
    private int onlinePlayerCount;
    private String base;
    private InetSocketAddress inetSocketAddress;

    public ProxyObjectBasicImplementation() {}

    public ProxyObjectBasicImplementation(String name, String group, String token, List<PlayerObject> onlinePlayers, int onlinePlayerCount, String base, InetSocketAddress inetSocketAddress) {
        this.name = name;
        this.group = group;
        this.token = token;
        this.onlinePlayers = onlinePlayers;
        this.onlinePlayerCount = onlinePlayerCount;
        this.base = base;
        this.inetSocketAddress = inetSocketAddress;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ProxyGroupObject getGroup() {
        return TimoCloudAPI.getUniversalInstance().getProxyGroup(group);
    }

    public String getGroupName() {
        return group;
    }

    public String getToken() {
        return token;
    }

    @Override
    public List<PlayerObject> getOnlinePlayers() {
        return onlinePlayers;
    }

    @Override
    public int getOnlinePlayerCount() {
        return onlinePlayerCount;
    }

    @Override
    public String getBase() {
        return base;
    }

    @Override
    public InetSocketAddress getSocketAddress() {
        return inetSocketAddress;
    }

    @Override
    public InetAddress getIpAddress() {
        return inetSocketAddress.getAddress();
    }

    @Override
    public int getPort() {
        return inetSocketAddress.getPort();
    }

    @Override
    public void executeCommand(String command) {}

    @Override
    public void stop() {
        executeCommand("end");
    }
}
