package cloud.timo.TimoCloud.bukkit.api;

import cloud.timo.TimoCloud.api.implementations.objects.PlayerObjectBasicImplementation;
import cloud.timo.TimoCloud.api.objects.PlayerObject;
import lombok.NoArgsConstructor;

import java.net.InetAddress;
import java.util.UUID;

@NoArgsConstructor
public class PlayerObjectBukkitImplementation extends PlayerObjectBasicImplementation implements PlayerObject {

    public PlayerObjectBukkitImplementation(String name, UUID uuid, String server, String proxy, InetAddress ipAddress, boolean online) {
        super(name, uuid, server, proxy, ipAddress, online);
    }

}
