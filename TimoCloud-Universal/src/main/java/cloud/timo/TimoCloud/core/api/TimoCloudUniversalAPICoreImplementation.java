package cloud.timo.TimoCloud.core.api;

import cloud.timo.TimoCloud.api.TimoCloudUniversalAPI;
import cloud.timo.TimoCloud.api.implementations.BaseObjectOfflineImplementation;
import cloud.timo.TimoCloud.api.objects.*;
import cloud.timo.TimoCloud.core.TimoCloudCore;
import cloud.timo.TimoCloud.core.objects.*;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class TimoCloudUniversalAPICoreImplementation implements TimoCloudUniversalAPI {

    @Override
    public Set<ServerGroupObject> getServerGroups() {
        return TimoCloudCore.getInstance().getInstanceManager().getServerGroups().stream().map(ServerGroup::toGroupObject).collect(Collectors.toSet());
    }

    @Override
    public ServerGroupObject getServerGroup(String groupName) {
        ServerGroup serverGroup = TimoCloudCore.getInstance().getInstanceManager().getServerGroupByName(groupName);
        return serverGroup == null ? null : serverGroup.toGroupObject();
    }

    @Override
    public ServerObject getServer(String serverName) {
        Server server = TimoCloudCore.getInstance().getInstanceManager().getServerByName(serverName);
        return server == null ? null : server.toServerObject();
    }

    @Override
    public Set<ProxyGroupObject> getProxyGroups() {
        return TimoCloudCore.getInstance().getInstanceManager().getProxyGroups().stream().map(ProxyGroup::toGroupObject).collect(Collectors.toSet());
    }

    @Override
    public ProxyGroupObject getProxyGroup(String groupName) {
        ProxyGroup proxyGroup = TimoCloudCore.getInstance().getInstanceManager().getProxyGroupByName(groupName);
        return proxyGroup == null ? null : proxyGroup.toGroupObject();
    }

    @Override
    public ProxyObject getProxy(String proxyName) {
        Proxy proxy = TimoCloudCore.getInstance().getInstanceManager().getProxyByName(proxyName);
        return proxy == null ? null : proxy.toProxyObject();
    }

    @Override
    public Collection<BaseObject> getBases() {
        return null;
    }

    @Override
    public BaseObject getBase(String name) {
        Base base = TimoCloudCore.getInstance().getInstanceManager().getBase(name);
        if (base == null) {
            return new BaseObjectOfflineImplementation(name);
        }
        return base.toBaseObject();
    }

    @Override
    public Collection<CordObject> getCords() {
        return null;
    }

    @Override
    public CordObject getCord(String name) {
        return null;
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
}
