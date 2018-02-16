package cloud.timo.TimoCloud.bukkit.api;

import cloud.timo.TimoCloud.api.implementations.PlayerObjectBasicImplementation;

import java.net.InetAddress;
import java.util.UUID;

public class PlayerObjectBukkitImplementation extends PlayerObjectBasicImplementation {
    public PlayerObjectBukkitImplementation() {
    }

    public PlayerObjectBukkitImplementation(String name, UUID uuid, String server, String proxy, InetAddress ipAddress, boolean online, long lastOnline) {
        super(name, uuid, server, proxy, ipAddress, online, lastOnline);
    }
}
