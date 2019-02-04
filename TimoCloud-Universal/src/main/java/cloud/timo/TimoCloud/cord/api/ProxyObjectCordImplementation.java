package cloud.timo.TimoCloud.cord.api;

import cloud.timo.TimoCloud.api.implementations.objects.ProxyObjectBasicImplementation;
import cloud.timo.TimoCloud.api.objects.BaseObject;
import cloud.timo.TimoCloud.api.objects.PlayerObject;
import cloud.timo.TimoCloud.api.objects.ProxyGroupObject;
import cloud.timo.TimoCloud.api.objects.ProxyObject;
import lombok.NoArgsConstructor;

import java.net.InetSocketAddress;
import java.util.Collection;

@NoArgsConstructor
public class ProxyObjectCordImplementation extends ProxyObjectBasicImplementation implements ProxyObject {

    public ProxyObjectCordImplementation(String name, String id, ProxyGroupObject group, Collection<PlayerObject> onlinePlayers, int onlinePlayerCount, BaseObject base, InetSocketAddress inetSocketAddress) {
        super(name, id, group, onlinePlayers, onlinePlayerCount, base, inetSocketAddress);
    }
}
