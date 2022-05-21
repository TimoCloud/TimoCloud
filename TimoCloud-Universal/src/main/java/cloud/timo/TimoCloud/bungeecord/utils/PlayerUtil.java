package cloud.timo.TimoCloud.bungeecord.utils;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.implementations.objects.ProxyObjectBasicImplementation;
import cloud.timo.TimoCloud.api.implementations.objects.ServerObjectBasicImplementation;
import cloud.timo.TimoCloud.api.internal.links.ProxyObjectLink;
import cloud.timo.TimoCloud.api.internal.links.ServerObjectLink;
import cloud.timo.TimoCloud.api.objects.PlayerObject;
import cloud.timo.TimoCloud.bungeecord.api.PlayerObjectBungeeImplementation;
import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.connection.ProxiedPlayer;

@UtilityClass
public class PlayerUtil {

    public PlayerObject playerToObject(ProxiedPlayer player) {
        return playerToObject(player, true);
    }

    public PlayerObject playerToObject(ProxiedPlayer player, boolean online) {
        ServerObjectLink serverObjectLink = null;
        ProxyObjectLink proxyObject;

        if (player.getServer() != null) {
            ServerObjectBasicImplementation server = (ServerObjectBasicImplementation) TimoCloudAPI.getUniversalAPI().getServer(player.getServer().getInfo().getName());
            if (server != null) serverObjectLink = server.toLink();
        }

        proxyObject = ((ProxyObjectBasicImplementation) TimoCloudAPI.getProxyAPI().getThisProxy()).toLink();

        return new PlayerObjectBungeeImplementation(
                player.getName(),
                player.getUniqueId(),
                serverObjectLink,
                proxyObject,
                player.getAddress().getAddress(),
                online
        );
    }

}
