package cloud.timo.TimoCloud.bungeecord.api;

import cloud.timo.TimoCloud.api.implementations.BaseObjectBasicImplementation;
import cloud.timo.TimoCloud.api.objects.BaseObject;
import lombok.NoArgsConstructor;

import java.net.InetAddress;
import java.util.Set;

@NoArgsConstructor
public class BaseObjectBungeeImplementation extends BaseObjectBasicImplementation implements BaseObject {

    public BaseObjectBungeeImplementation(String name, InetAddress ipAddress, Double cpuLoad, int freeRam, int maxRam, Boolean connected, Boolean ready, Set<String> servers, Set<String> proxies) {
        super(name, ipAddress, cpuLoad, freeRam, maxRam, connected, ready, servers, proxies);
    }
}
