package cloud.timo.TimoCloud.bukkit.api;

import cloud.timo.TimoCloud.api.implementations.ServerObjectBasicImplementation;
import cloud.timo.TimoCloud.api.objects.PlayerObject;
import cloud.timo.TimoCloud.api.objects.ServerObject;
import cloud.timo.TimoCloud.bukkit.TimoCloudBukkit;
import lombok.NoArgsConstructor;

import java.net.InetSocketAddress;
import java.util.List;

@NoArgsConstructor
public class ServerObjectBukkitImplementation extends ServerObjectBasicImplementation implements ServerObject {

    public ServerObjectBukkitImplementation(String name, String group, String token, String state, String extra, String map, String motd, List<PlayerObject> onlinePlayers, int onlinePlayerCount, int maxPlayerCount, String base, InetSocketAddress socketAddress) {
        super(name, group, token, state, extra, map, motd, onlinePlayers, onlinePlayerCount, maxPlayerCount, base, socketAddress);
    }

    @Override
    public void setState(String state) {
        this.state = state;
        TimoCloudBukkit.getInstance().getSocketMessageManager().sendMessage("SET_STATE", getToken(), state);
    }

    @Override
    public void setExtra(String extra) {
        this.extra = extra;
        TimoCloudBukkit.getInstance().getSocketMessageManager().sendMessage("SET_EXTRA", getToken(), extra);
    }
}
