package cloud.timo.TimoCloud.bungeecord.api;

import cloud.timo.TimoCloud.api.implementations.objects.PlayerObjectBasicImplementation;
import cloud.timo.TimoCloud.api.objects.PlayerObject;
import lombok.NoArgsConstructor;

import java.net.InetAddress;
import java.util.UUID;

@NoArgsConstructor
public class PlayerObjectBungeeImplementation extends PlayerObjectBasicImplementation implements PlayerObject {

    public PlayerObjectBungeeImplementation(String name, UUID uuid, String server, String proxy, InetAddress ipAddress, boolean online) {
        super(name, uuid, server, proxy, ipAddress, online);
    }
}
