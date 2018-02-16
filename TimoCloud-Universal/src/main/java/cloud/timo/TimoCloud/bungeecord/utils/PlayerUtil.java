package cloud.timo.TimoCloud.bungeecord.utils;

import cloud.timo.TimoCloud.api.implementations.PlayerObjectBasicImplementation;
import cloud.timo.TimoCloud.api.objects.PlayerObject;
import cloud.timo.TimoCloud.bungeecord.TimoCloudBungee;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class PlayerUtil {

    public static PlayerObject playerToObject(ProxiedPlayer player) {
        return playerToObject(player, true);
    }

    public static PlayerObject playerToObject(ProxiedPlayer player, boolean online) {
        return new PlayerObjectBasicImplementation(
                player.getName(),
                player.getUniqueId(),
                player.getServer().getInfo().getName(),
                TimoCloudBungee.getInstance().getProxyName(),
                player.getAddress().getAddress(),
                online,
                online ? 0 : -1);
    }

}
