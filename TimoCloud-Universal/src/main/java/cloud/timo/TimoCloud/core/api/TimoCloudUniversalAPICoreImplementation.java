package cloud.timo.TimoCloud.core.api;

import cloud.timo.TimoCloud.api.TimoCloudUniversalAPI;
import cloud.timo.TimoCloud.api.implementations.TimoCloudUniversalAPIBasicImplementation;
import cloud.timo.TimoCloud.api.objects.BaseObject;
import cloud.timo.TimoCloud.api.objects.CordObject;
import cloud.timo.TimoCloud.api.objects.PlayerObject;
import cloud.timo.TimoCloud.api.objects.ProxyGroupObject;
import cloud.timo.TimoCloud.api.objects.ProxyObject;
import cloud.timo.TimoCloud.api.objects.ServerGroupObject;
import cloud.timo.TimoCloud.api.objects.ServerObject;
import cloud.timo.TimoCloud.core.TimoCloudCore;
import cloud.timo.TimoCloud.core.objects.Base;
import cloud.timo.TimoCloud.core.objects.Cord;
import cloud.timo.TimoCloud.core.objects.Proxy;
import cloud.timo.TimoCloud.core.objects.ProxyGroup;
import cloud.timo.TimoCloud.core.objects.Server;
import cloud.timo.TimoCloud.core.objects.ServerGroup;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class TimoCloudUniversalAPICoreImplementation extends TimoCloudUniversalAPIBasicImplementation implements TimoCloudUniversalAPI {

    public TimoCloudUniversalAPICoreImplementation() {
        super(ServerObjectCoreImplementation.class, ProxyObjectCoreImplementation.class, ServerGroupObjectCoreImplementation.class, ProxyGroupObjectCoreImplementation.class, PlayerObjectCoreImplementation.class, BaseObjectCoreImplementation.class, CordObjectCoreImplementation.class);
    }

    @Override
    public Set<ServerGroupObject> getServerGroups() {
        return TimoCloudCore.getInstance().getInstanceManager().getServerGroups().stream().map(ServerGroup::toGroupObject).collect(Collectors.toSet());
    }

    @Override
    public ServerGroupObject getServerGroup(String identifier) {
        ServerGroup serverGroup = TimoCloudCore.getInstance().getInstanceManager().getServerGroupByIdentifier(identifier);
        return serverGroup == null ? null : serverGroup.toGroupObject();
    }

    @Override
    public ServerObject getServer(String identifier) {
        Server server = TimoCloudCore.getInstance().getInstanceManager().getServerByIdentifier(identifier);
        return server == null ? null : server.toServerObject();
    }

    @Override
    public Collection<ServerObject> getServers() {
        return TimoCloudCore.getInstance().getInstanceManager().getServers().stream().map(Server::toServerObject).collect(Collectors.toList());
    }

    @Override
    public Set<ProxyGroupObject> getProxyGroups() {
        return TimoCloudCore.getInstance().getInstanceManager().getProxyGroups().stream().map(ProxyGroup::toGroupObject).collect(Collectors.toSet());
    }

    @Override
    public ProxyGroupObject getProxyGroup(String identifier) {
        ProxyGroup proxyGroup = TimoCloudCore.getInstance().getInstanceManager().getProxyGroupByIdentifier(identifier);
        return proxyGroup == null ? null : proxyGroup.toGroupObject();
    }

    @Override
    public ProxyObject getProxy(String identifier) {
        Proxy proxy = TimoCloudCore.getInstance().getInstanceManager().getProxyByIdentifier(identifier);
        return proxy == null ? null : proxy.toProxyObject();
    }

    @Override
    public Collection<ProxyObject> getProxies() {
        return TimoCloudCore.getInstance().getInstanceManager().getProxies().stream().map(Proxy::toProxyObject).collect(Collectors.toList());
    }

    @Override
    public Collection<BaseObject> getBases() {
        return TimoCloudCore.getInstance().getInstanceManager().getBases().stream().map(Base::toBaseObject).collect(Collectors.toSet());
    }

    @Override
    public BaseObject getBase(String identifier) {
        Base base = TimoCloudCore.getInstance().getInstanceManager().getBaseByIdentifier(identifier);
        return base == null ? null : base.toBaseObject();
    }

    @Override
    public Collection<CordObject> getCords() {
        return TimoCloudCore.getInstance().getInstanceManager().getCords().stream().map(Cord::toCordObject).collect(Collectors.toSet());
    }

    @Override
    public CordObject getCord(String identifier) {
        Cord cord = TimoCloudCore.getInstance().getInstanceManager().getCord(identifier);
        return cord == null ? null : cord.toCordObject();
    }

    @Override
    public PlayerObject getPlayer(UUID uuid) {
        for (Proxy proxy : TimoCloudCore.getInstance().getInstanceManager().getProxyGroups().stream().map(ProxyGroup::getProxies).flatMap(Collection::stream).collect(Collectors.toList()))
            for (PlayerObject playerObject : proxy.getOnlinePlayers())
                if (playerObject.getUuid().equals(uuid)) return playerObject;
        return null;
    }

    @Override
    public PlayerObject getPlayer(String name) {
        for (Proxy proxy : TimoCloudCore.getInstance().getInstanceManager().getProxyGroups().stream().map(ProxyGroup::getProxies).flatMap(Collection::stream).collect(Collectors.toList()))
            for (PlayerObject playerObject : proxy.getOnlinePlayers())
                if (playerObject.getName().equals(name)) return playerObject;
        return null;
    }

    @Override
    public Collection<PlayerObject> getPlayers() {
        return TimoCloudCore.getInstance().getInstanceManager().getProxies().stream().flatMap(proxy -> proxy.getOnlinePlayers().stream()).collect(Collectors.toSet());
    }

}
