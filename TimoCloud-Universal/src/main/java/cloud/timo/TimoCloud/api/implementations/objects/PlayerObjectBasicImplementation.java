package cloud.timo.TimoCloud.api.implementations.objects;

import cloud.timo.TimoCloud.api.async.APIRequestFuture;
import cloud.timo.TimoCloud.api.async.APIRequestType;
import cloud.timo.TimoCloud.api.implementations.async.APIRequestImplementation;
import cloud.timo.TimoCloud.api.internal.links.LinkableObject;
import cloud.timo.TimoCloud.api.internal.links.PlayerObjectLink;
import cloud.timo.TimoCloud.api.internal.links.ProxyObjectLink;
import cloud.timo.TimoCloud.api.internal.links.ServerObjectLink;
import cloud.timo.TimoCloud.api.objects.PlayerObject;
import cloud.timo.TimoCloud.api.objects.ProxyObject;
import cloud.timo.TimoCloud.api.objects.ServerObject;
import cloud.timo.TimoCloud.common.datatypes.TypeMap;
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

    public PlayerObjectBasicImplementation(String name, UUID uuid, ServerObjectLink server, ProxyObjectLink proxy, InetAddress ipAddress, boolean online) {
        this.name = name;
        this.uuid = uuid;
        this.server = server;
        this.proxy = proxy;
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
        try {
            return server.resolve();
        } catch (Exception e) {
            return null;
        }
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
        return new APIRequestImplementation<Void>(APIRequestType.P_SEND_PLAYER, getProxy().getId(),  new TypeMap()
                .put("playerUUID", getId())
                .put("targetServer", serverObject.getId())).submit();
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
