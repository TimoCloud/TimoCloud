package cloud.timo.TimoCloud.core.api;

import cloud.timo.TimoCloud.api.implementations.objects.BaseObjectBasicImplementation;
import cloud.timo.TimoCloud.api.internal.links.ProxyObjectLink;
import cloud.timo.TimoCloud.api.internal.links.ServerObjectLink;
import cloud.timo.TimoCloud.api.objects.BaseObject;
import lombok.NoArgsConstructor;

import java.net.InetAddress;
import java.util.Set;

@NoArgsConstructor
public class BaseObjectCoreImplementation extends BaseObjectBasicImplementation implements BaseObject {

    public BaseObjectCoreImplementation(String id, String name, InetAddress ipAddress, Double cpuLoad, Double maxCpuLoad, int availableRam, int maxRam, Boolean connected, Boolean ready, Set<ServerObjectLink> servers, Set<ProxyObjectLink> proxies) {
        super(id, name, ipAddress, cpuLoad, maxCpuLoad, availableRam, maxRam, connected, ready, servers, proxies);
    }
}
