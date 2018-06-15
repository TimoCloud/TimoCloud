package cloud.timo.TimoCloud.core.api;

import cloud.timo.TimoCloud.api.TimoCloudUniversalAPI;
import cloud.timo.TimoCloud.api.implementations.TimoCloudUniversalAPIBasicImplementation;
import cloud.timo.TimoCloud.api.objects.*;
import cloud.timo.TimoCloud.core.TimoCloudCore;
import cloud.timo.TimoCloud.core.objects.Proxy;
import cloud.timo.TimoCloud.core.objects.ProxyGroup;
import cloud.timo.TimoCloud.core.objects.Server;
import cloud.timo.TimoCloud.core.objects.ServerGroup;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class TimoCloudUniversalAPICoreImplementation extends TimoCloudUniversalAPIBasicImplementation implements TimoCloudUniversalAPI {

    public TimoCloudUniversalAPICoreImplementation() {
        super(ServerObjectCoreImplementation.class, ProxyObjectCoreImplementation.class, ServerGroupObjectCoreImplementation.class, ProxyGroupObjectCoreImplementation.class, PlayerObjectCoreImplementation.class, CordObjectCoreImplementation.class);
    }

    @Override
    public List<ServerGroupObject> getServerGroups() {
        return TimoCloudCore.getInstance().getInstanceManager().getServerGroups().stream().map(ServerGroup::toGroupObject).collect(Collectors.toList());
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
    public List<ProxyGroupObject> getProxyGroups() {
        return TimoCloudCore.getInstance().getInstanceManager().getProxyGroups().stream().map(ProxyGroup::toGroupObject).collect(Collectors.toList());
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
