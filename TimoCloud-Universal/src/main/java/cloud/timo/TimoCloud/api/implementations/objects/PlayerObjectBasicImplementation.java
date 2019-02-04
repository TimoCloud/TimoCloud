package cloud.timo.TimoCloud.api.implementations.objects;

import cloud.timo.TimoCloud.api.async.APIRequestFuture;
import cloud.timo.TimoCloud.api.internal.links.LinkableObject;
import cloud.timo.TimoCloud.api.internal.links.PlayerObjectLink;
import cloud.timo.TimoCloud.api.internal.links.ProxyObjectLink;
import cloud.timo.TimoCloud.api.internal.links.ServerObjectLink;
import cloud.timo.TimoCloud.api.objects.PlayerObject;
import cloud.timo.TimoCloud.api.objects.ProxyObject;
import cloud.timo.TimoCloud.api.objects.ServerObject;
import lombok.NoArgsConstructor;

import java.net.InetAddress;
import java.util.UUID;

@NoArgsConstructor
public class PlayerObjectBasicImplementation implements PlayerObject, LinkableObject<PlayerObject> {

    private String name;
    private UUID uuid;
    private ServerObjectLink server;
    private ProxyObjectLink proxy;
    private InetAddress ipAddress;
    private boolean online;

    public PlayerObjectBasicImplementation(String name, UUID uuid, ServerObject server, ProxyObject proxy, InetAddress ipAddress, boolean online) {
        this.name = name;
        this.uuid = uuid;
        this.server = ((ServerObjectBasicImplementation) server).toLink();
        this.proxy = ((ProxyObjectBasicImplementation) proxy).toLink();
        this.ipAddress = ipAddress;
        this.online = online;
    }

    @Override
    public String getId() {
        return getUuid().toString();
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
        return server.resolve();
    }

    public void setServer(ServerObject server) {
        this.server = ((ServerObjectBasicImplementation) server).toLink();
    }

    public ProxyObject getProxy() {
        return proxy.resolve();
    }

    public void setProxy(ProxyObject proxy) {
        this.proxy = ((ProxyObjectBasicImplementation) proxy).toLink();
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
    public APIRequestFuture<Void> sendToServer(ServerObject serverObject) {
        return getProxy().executeCommand(String.format("send %s %s", getName(), getProxy().getName()));
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    @Override
    public PlayerObjectLink toLink() {
        return new PlayerObjectLink(this);
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
