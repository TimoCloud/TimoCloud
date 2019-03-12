package cloud.timo.TimoCloud.core.api;

import cloud.timo.TimoCloud.api.implementations.objects.ProxyObjectBasicImplementation;
import cloud.timo.TimoCloud.api.internal.links.BaseObjectLink;
import cloud.timo.TimoCloud.api.internal.links.PlayerObjectLink;
import cloud.timo.TimoCloud.api.internal.links.ProxyGroupObjectLink;
import cloud.timo.TimoCloud.api.objects.ProxyObject;
import lombok.NoArgsConstructor;

import java.net.InetSocketAddress;
import java.util.Set;

@NoArgsConstructor
public class ProxyObjectCoreImplementation extends ProxyObjectBasicImplementation implements ProxyObject {

    public ProxyObjectCoreImplementation(String name, String id, ProxyGroupObjectLink group, Set<PlayerObjectLink> onlinePlayers, int onlinePlayerCount, BaseObjectLink base, InetSocketAddress inetSocketAddress) {
        super(name, id, group, onlinePlayers, onlinePlayerCount, base, inetSocketAddress);
    }

}
