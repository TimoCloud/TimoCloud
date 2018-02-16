package cloud.timo.TimoCloud.bungeecord.api;

import cloud.timo.TimoCloud.api.implementations.PlayerObjectBasicImplementation;

import java.net.InetAddress;
import java.util.UUID;

public class PlayerObjectBungeeImplementation extends PlayerObjectBasicImplementation {
    public PlayerObjectBungeeImplementation() {
    }

    public PlayerObjectBungeeImplementation(String name, UUID uuid, String server, String proxy, InetAddress ipAddress, boolean online, long lastOnline) {
        super(name, uuid, server, proxy, ipAddress, online, lastOnline);
    }
}
