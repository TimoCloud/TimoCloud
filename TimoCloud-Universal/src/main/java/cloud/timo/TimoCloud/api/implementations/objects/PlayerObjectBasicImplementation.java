package cloud.timo.TimoCloud.api.implementations.objects;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.async.APIRequestFuture;
import cloud.timo.TimoCloud.api.objects.PlayerObject;
import cloud.timo.TimoCloud.api.objects.ProxyObject;
import cloud.timo.TimoCloud.api.objects.ServerObject;

import java.net.InetAddress;
import java.util.UUID;

public class PlayerObjectBasicImplementation implements PlayerObject {

    private String name;
    private UUID uuid;
    private String server;
    private String proxy;
    private InetAddress ipAddress;
    private boolean online;

    public PlayerObjectBasicImplementation() {}

    public PlayerObjectBasicImplementation(String name, UUID uuid, String server, String proxy, InetAddress ipAddress, boolean online) {
        this.name = name;
        this.uuid = uuid;
        this.server = server;
        this.proxy = proxy;
        this.ipAddress = ipAddress;
        this.online = online;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public ServerObject getServer() {
        return TimoCloudAPI.getUniversalAPI().getServer(server);
    }

    public void setServer(String server) {
        this.server = server;
    }

    public ProxyObject getProxy() {
        return TimoCloudAPI.getUniversalAPI().getProxy(proxy);
    }

    public void setProxy(String proxy) {
        this.proxy = proxy;
    }

    public InetAddress getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(InetAddress ipAddress) {
        this.ipAddress = ipAddress;
    }

    public boolean isOnline() {
        return online;
    }

    @Override
    public APIRequestFuture sendToServer(ServerObject serverObject) {
        return getProxy().executeCommand(String.format("send %s %s", getName(), getProxy().getName()));
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PlayerObjectBasicImplementation that = (PlayerObjectBasicImplementation) o;

        return uuid.equals(that.uuid);
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }
}
