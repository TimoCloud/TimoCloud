package cloud.timo.TimoCloud.cord.api;

import cloud.timo.TimoCloud.api.implementations.objects.BaseObjectBasicImplementation;
import cloud.timo.TimoCloud.api.objects.BaseObject;
import cloud.timo.TimoCloud.api.objects.ProxyObject;
import cloud.timo.TimoCloud.api.objects.ServerObject;
import lombok.NoArgsConstructor;

import java.net.InetAddress;
import java.util.Collection;

@NoArgsConstructor
public class BaseObjectCordImplementation extends BaseObjectBasicImplementation implements BaseObject {

    public BaseObjectCordImplementation(String id, String name, InetAddress ipAddress, Double cpuLoad, int availableRam, int maxRam, Boolean connected, Boolean ready, Collection<ServerObject> servers, Collection<ProxyObject> proxies) {
        super(id, name, ipAddress, cpuLoad, availableRam, maxRam, connected, ready, servers, proxies);
    }
}
