package cloud.timo.TimoCloud.cord.api;

import cloud.timo.TimoCloud.api.implementations.PlayerObjectBasicImplementation;
import lombok.NoArgsConstructor;

import java.net.InetAddress;
import java.util.UUID;

@NoArgsConstructor
public class PlayerObjectCordImplementation extends PlayerObjectBasicImplementation {

    public PlayerObjectCordImplementation(String name, UUID uuid, String server, String proxy, InetAddress ipAddress, boolean online, long lastOnline) {
        super(name, uuid, server, proxy, ipAddress, online, lastOnline);
    }
}
