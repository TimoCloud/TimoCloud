package cloud.timo.TimoCloud.core.api;

import cloud.timo.TimoCloud.api.implementations.objects.PlayerObjectBasicImplementation;
import cloud.timo.TimoCloud.api.internal.links.ProxyObjectLink;
import cloud.timo.TimoCloud.api.internal.links.ServerObjectLink;
import cloud.timo.TimoCloud.api.objects.PlayerObject;
import lombok.NoArgsConstructor;

import java.net.InetAddress;
import java.util.UUID;

@NoArgsConstructor
public class PlayerObjectCoreImplementation extends PlayerObjectBasicImplementation implements PlayerObject {

    public PlayerObjectCoreImplementation(String name, UUID uuid, ServerObjectLink server, ProxyObjectLink proxy, InetAddress ipAddress, boolean online) {
        super(name, uuid, server, proxy, ipAddress, online);
    }
}
