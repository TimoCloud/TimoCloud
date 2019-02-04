package cloud.timo.TimoCloud.cord.api;

import cloud.timo.TimoCloud.api.implementations.objects.ServerObjectBasicImplementation;
import cloud.timo.TimoCloud.api.objects.BaseObject;
import cloud.timo.TimoCloud.api.objects.PlayerObject;
import cloud.timo.TimoCloud.api.objects.ServerGroupObject;
import cloud.timo.TimoCloud.api.objects.ServerObject;
import lombok.NoArgsConstructor;

import java.net.InetSocketAddress;
import java.util.List;

@NoArgsConstructor
public class ServerObjectCordImplementation extends ServerObjectBasicImplementation implements ServerObject {

    public ServerObjectCordImplementation(String name, String id, ServerGroupObject group, String state, String extra, String map, String motd, List<PlayerObject> onlinePlayers, int onlinePlayerCount, int maxPlayerCount, BaseObject base, InetSocketAddress socketAddress) {
        super(name, id, group, state, extra, map, motd, onlinePlayers, onlinePlayerCount, maxPlayerCount, base, socketAddress);
    }
}
