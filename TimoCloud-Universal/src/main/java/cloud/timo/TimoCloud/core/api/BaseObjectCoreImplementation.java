package cloud.timo.TimoCloud.core.api;

import cloud.timo.TimoCloud.api.implementations.BaseObjectBasicImplementation;
import cloud.timo.TimoCloud.api.objects.BaseObject;
import lombok.NoArgsConstructor;

import java.net.InetAddress;
import java.util.Set;

@NoArgsConstructor
public class BaseObjectCoreImplementation extends BaseObjectBasicImplementation implements BaseObject  {

    public BaseObjectCoreImplementation(String name, InetAddress ipAddress, Double cpuLoad, int freeRam, int maxRam, Boolean connected, Boolean ready, Set<String> servers, Set<String> proxies) {
        super(name, ipAddress, cpuLoad, freeRam, maxRam, connected, ready, servers, proxies);
    }
}
