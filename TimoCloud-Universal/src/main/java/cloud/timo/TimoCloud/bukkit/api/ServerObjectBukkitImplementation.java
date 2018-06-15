package cloud.timo.TimoCloud.bukkit.api;

import cloud.timo.TimoCloud.api.implementations.ServerObjectBasicImplementation;
import cloud.timo.TimoCloud.api.objects.PlayerObject;
import cloud.timo.TimoCloud.api.objects.ServerObject;
import cloud.timo.TimoCloud.bukkit.TimoCloudBukkit;
import cloud.timo.TimoCloud.lib.messages.Message;
import lombok.NoArgsConstructor;

import java.net.InetSocketAddress;
import java.util.List;

@NoArgsConstructor
public class ServerObjectBukkitImplementation extends ServerObjectBasicImplementation implements ServerObject {

    public ServerObjectBukkitImplementation(String name, String id, String group, String state, String extra, String map, String motd, List<PlayerObject> onlinePlayers, int onlinePlayerCount, int maxPlayerCount, String base, InetSocketAddress socketAddress) {
        super(name, id, group, state, extra, map, motd, onlinePlayers, onlinePlayerCount, maxPlayerCount, base, socketAddress);
    }

    @Override
    public void setState(String state) {
        this.state = state;
        TimoCloudBukkit.getInstance().getSocketMessageManager().sendMessage(Message.create().setType("SET_STATE").setTarget(getId()).setData(state));
    }

    @Override
    public void setExtra(String extra) {
        this.extra = extra;
        TimoCloudBukkit.getInstance().getSocketMessageManager().sendMessage(Message.create().setType("SET_EXTRA").setTarget(getId()).setData(extra));
    }
}
