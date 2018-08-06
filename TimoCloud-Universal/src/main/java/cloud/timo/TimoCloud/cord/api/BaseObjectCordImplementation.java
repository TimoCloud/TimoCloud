package cloud.timo.TimoCloud.cord.api;

import cloud.timo.TimoCloud.api.implementations.BaseObjectBasicImplementation;
import cloud.timo.TimoCloud.api.objects.BaseObject;

import java.net.InetAddress;
import java.util.Set;

public class BaseObjectCordImplementation extends BaseObjectBasicImplementation implements BaseObject {

    public BaseObjectCordImplementation(String name, InetAddress ipAddress, Double cpuLoad, int freeRam, int maxRam, Boolean connected, Boolean ready, Set<String> servers, Set<String> proxies) {
        super(name, ipAddress, cpuLoad, freeRam, maxRam, connected, ready, servers, proxies);
    }
}
