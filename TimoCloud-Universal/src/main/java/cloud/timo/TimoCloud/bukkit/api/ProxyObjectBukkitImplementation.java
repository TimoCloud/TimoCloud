package cloud.timo.TimoCloud.bukkit.api;

import cloud.timo.TimoCloud.api.implementations.objects.ProxyObjectBasicImplementation;
import cloud.timo.TimoCloud.api.objects.PlayerObject;
import cloud.timo.TimoCloud.api.objects.ProxyObject;
import lombok.NoArgsConstructor;

import java.net.InetSocketAddress;
import java.util.List;

@NoArgsConstructor
public class ProxyObjectBukkitImplementation extends ProxyObjectBasicImplementation implements ProxyObject {

    public ProxyObjectBukkitImplementation(String name, String id, String group, List<PlayerObject> onlinePlayers, int onlinePlayerCount, String base, InetSocketAddress inetSocketAddress) {
        super(name, id, group, onlinePlayers, onlinePlayerCount, base, inetSocketAddress);
    }
}
