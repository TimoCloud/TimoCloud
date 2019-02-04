package cloud.timo.TimoCloud.cord.api;

import cloud.timo.TimoCloud.api.implementations.objects.PlayerObjectBasicImplementation;
import cloud.timo.TimoCloud.api.objects.PlayerObject;
import cloud.timo.TimoCloud.api.objects.ProxyObject;
import cloud.timo.TimoCloud.api.objects.ServerObject;
import lombok.NoArgsConstructor;

import java.net.InetAddress;
import java.util.UUID;

@NoArgsConstructor
public class PlayerObjectCordImplementation extends PlayerObjectBasicImplementation implements PlayerObject {

    public PlayerObjectCordImplementation(String name, UUID uuid, ServerObject server, ProxyObject proxy, InetAddress ipAddress, boolean online) {
        super(name, uuid, server, proxy, ipAddress, online);
    }
}
