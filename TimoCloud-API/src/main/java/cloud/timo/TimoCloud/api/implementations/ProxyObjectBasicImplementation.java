package cloud.timo.TimoCloud.api.implementations;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.objects.ProxyGroupObject;
import cloud.timo.TimoCloud.api.objects.ProxyObject;

import java.net.InetAddress;
import java.net.InetSocketAddress;

public class ProxyObjectBasicImplementation implements ProxyObject {

    private String name;
    private String group;
    private String token;
    private int onlinePlayerCount;
    private InetSocketAddress inetSocketAddress;

    public ProxyObjectBasicImplementation() {}

    public ProxyObjectBasicImplementation(String name, String group, String token, int onlinePlayerCount, InetSocketAddress inetSocketAddress) {
        this.name = name;
        this.group = group;
        this.token = token;
        this.onlinePlayerCount = onlinePlayerCount;
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
    public int getOnlinePlayerCount() {
        return onlinePlayerCount;
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
