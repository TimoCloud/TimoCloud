package cloud.timo.TimoCloud.core.api;

import cloud.timo.TimoCloud.api.implementations.PlayerObjectBasicImplementation;

import java.net.InetAddress;
import java.util.UUID;

public class PlayerObjectCoreImplementation extends PlayerObjectBasicImplementation {

    public PlayerObjectCoreImplementation() {
    }

    public PlayerObjectCoreImplementation(String name, UUID uuid, String server, String proxy, InetAddress ipAddress, boolean online, long lastOnline) {
        super(name, uuid, server, proxy, ipAddress, online, lastOnline);
    }
}
