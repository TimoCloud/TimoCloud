package cloud.timo.TimoCloud.core.api;

import cloud.timo.TimoCloud.api.implementations.PlayerObjectBasicImplementation;
import cloud.timo.TimoCloud.api.objects.PlayerObject;
import lombok.NoArgsConstructor;

import java.net.InetAddress;
import java.util.UUID;

@NoArgsConstructor
public class PlayerObjectCoreImplementation extends PlayerObjectBasicImplementation implements PlayerObject {

    public PlayerObjectCoreImplementation(String name, UUID uuid, String server, String proxy, InetAddress ipAddress, boolean online) {
        super(name, uuid, server, proxy, ipAddress, online);
    }

}
