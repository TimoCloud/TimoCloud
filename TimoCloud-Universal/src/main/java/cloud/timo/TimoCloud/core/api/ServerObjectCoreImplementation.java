package cloud.timo.TimoCloud.core.api;

import cloud.timo.TimoCloud.api.implementations.objects.ServerObjectBasicImplementation;
import cloud.timo.TimoCloud.api.internal.links.BaseObjectLink;
import cloud.timo.TimoCloud.api.internal.links.PlayerObjectLink;
import cloud.timo.TimoCloud.api.internal.links.ServerGroupObjectLink;
import cloud.timo.TimoCloud.api.objects.ServerObject;
import lombok.NoArgsConstructor;

import java.net.InetSocketAddress;
import java.util.Set;

@NoArgsConstructor
public class ServerObjectCoreImplementation extends ServerObjectBasicImplementation implements ServerObject {

    public ServerObjectCoreImplementation(String name, String id, ServerGroupObjectLink group, String state, String extra, String map, String motd, Set<PlayerObjectLink> onlinePlayers, int onlinePlayerCount, int maxPlayerCount, BaseObjectLink base, InetSocketAddress socketAddress) {
        super(name, id, group, state, extra, map, motd, onlinePlayers, onlinePlayerCount, maxPlayerCount, base, socketAddress);
    }
}
