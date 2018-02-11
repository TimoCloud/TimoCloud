package cloud.timo.TimoCloud.bungeecord.api;

import cloud.timo.TimoCloud.api.implementations.ProxyGroupObjectBasicImplementation;
import cloud.timo.TimoCloud.api.objects.ProxyGroupObject;
import cloud.timo.TimoCloud.api.objects.ProxyObject;

import java.util.List;

public class ProxyGroupObjectBungeeImplementation extends ProxyGroupObjectBasicImplementation implements ProxyGroupObject {

    public ProxyGroupObjectBungeeImplementation() {}

    public ProxyGroupObjectBungeeImplementation(String name, List<ProxyObject> proxies, int onlinePlayerCount, int maxPlayerCount, int maxPlayerCountPerProxy, int keepFreeSlots, int ram, String motd, boolean isStatic, int priority, List<String> serverGroups, String base, String proxyChooseStrategy, List<String> hostNames) {
        super(name, proxies, onlinePlayerCount, maxPlayerCount, maxPlayerCountPerProxy, keepFreeSlots, ram, motd, isStatic, priority, serverGroups, base, proxyChooseStrategy, hostNames);
    }
}
