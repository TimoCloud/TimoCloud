package cloud.timo.TimoCloud.bungeecord.api;

import cloud.timo.TimoCloud.api.implementations.objects.ProxyGroupObjectBasicImplementation;
import cloud.timo.TimoCloud.api.objects.BaseObject;
import cloud.timo.TimoCloud.api.objects.ProxyGroupObject;
import cloud.timo.TimoCloud.api.objects.ProxyObject;
import cloud.timo.TimoCloud.api.objects.ServerGroupObject;
import lombok.NoArgsConstructor;

import java.util.Collection;

@NoArgsConstructor
public class ProxyGroupObjectBungeeImplementation extends ProxyGroupObjectBasicImplementation implements ProxyGroupObject {

    public ProxyGroupObjectBungeeImplementation(String id, String name, Collection<ProxyObject> proxies, int onlinePlayerCount, int maxPlayerCount, int maxPlayerCountPerProxy, int keepFreeSlots, int minAmount, int maxAmount, int ram, String motd, boolean isStatic, int priority, Collection<ServerGroupObject> serverGroups, BaseObject base, String proxyChooseStrategy, Collection<String> hostNames) {
        super(id, name, proxies, onlinePlayerCount, maxPlayerCount, maxPlayerCountPerProxy, keepFreeSlots, minAmount, maxAmount, ram, motd, isStatic, priority, serverGroups, base, proxyChooseStrategy, hostNames);
    }
}
