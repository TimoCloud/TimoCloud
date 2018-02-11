package cloud.timo.TimoCloud.core.api;

import cloud.timo.TimoCloud.api.TimoCloudUniversalAPI;
import cloud.timo.TimoCloud.api.objects.ProxyGroupObject;
import cloud.timo.TimoCloud.api.objects.ProxyObject;
import cloud.timo.TimoCloud.api.objects.ServerGroupObject;
import cloud.timo.TimoCloud.api.objects.ServerObject;
import cloud.timo.TimoCloud.core.TimoCloudCore;
import cloud.timo.TimoCloud.core.objects.ProxyGroup;
import cloud.timo.TimoCloud.core.objects.ServerGroup;

import java.util.List;
import java.util.stream.Collectors;

public class TimoCloudUniversalAPICoreImplementation implements TimoCloudUniversalAPI {
    @Override
    public List<ServerGroupObject> getServerGroups() {
        return TimoCloudCore.getInstance().getServerManager().getServerGroups().stream().map(ServerGroup::toGroupObject).collect(Collectors.toList());
    }

    @Override
    public ServerGroupObject getServerGroup(String groupName) {
        return TimoCloudCore.getInstance().getServerManager().getServerGroupByName(groupName).toGroupObject();
    }

    @Override
    public ServerObject getServer(String serverName) {
        return TimoCloudCore.getInstance().getServerManager().getServerByName(serverName).toServerObject();
    }

    @Override
    public List<ProxyGroupObject> getProxyGroups() {
        return TimoCloudCore.getInstance().getServerManager().getProxyGroups().stream().map(ProxyGroup::toGroupObject).collect(Collectors.toList());
    }

    @Override
    public ProxyGroupObject getProxyGroup(String groupName) {
        return TimoCloudCore.getInstance().getServerManager().getProxyGroupByName(groupName).toGroupObject();
    }

    @Override
    public ProxyObject getProxy(String proxyName) {
        return TimoCloudCore.getInstance().getServerManager().getProxyByName(proxyName).toProxyObject();
    }
}
