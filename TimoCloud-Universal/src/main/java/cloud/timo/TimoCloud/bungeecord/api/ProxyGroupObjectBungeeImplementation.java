package cloud.timo.TimoCloud.bungeecord.api;

import cloud.timo.TimoCloud.api.implementations.objects.ProxyGroupObjectBasicImplementation;
import cloud.timo.TimoCloud.api.objects.ProxyGroupObject;
import cloud.timo.TimoCloud.api.objects.ProxyObject;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
public class ProxyGroupObjectBungeeImplementation extends ProxyGroupObjectBasicImplementation implements ProxyGroupObject {

    public ProxyGroupObjectBungeeImplementation(String name, List<ProxyObject> proxies, int onlinePlayerCount, int maxPlayerCount, int maxPlayerCountPerProxy, int keepFreeSlots, int minAmount, int maxAmount, int ram, String motd, boolean isStatic, int priority, List<String> serverGroups, String base, String proxyChooseStrategy, List<String> hostNames) {
        super(name, proxies, onlinePlayerCount, maxPlayerCount, maxPlayerCountPerProxy, keepFreeSlots, minAmount, maxAmount, ram, motd, isStatic, priority, serverGroups, base, proxyChooseStrategy, hostNames);
    }

}
