package cloud.timo.TimoCloud.velocity.utils;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.implementations.objects.ProxyObjectBasicImplementation;
import cloud.timo.TimoCloud.api.implementations.objects.ServerObjectBasicImplementation;
import cloud.timo.TimoCloud.api.internal.links.ProxyObjectLink;
import cloud.timo.TimoCloud.api.internal.links.ServerObjectLink;
import cloud.timo.TimoCloud.api.objects.PlayerObject;
import cloud.timo.TimoCloud.velocity.api.PlayerObjectVelocityImplementation;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;


public class PlayerUtil {

    public static PlayerObject playerToObject(Player player) {
        if (player.getCurrentServer().isPresent())
            return playerToObject(player, player.getCurrentServer().get().getServer());
        return null;
    }

    public static PlayerObject playerToObject(Player player, RegisteredServer registeredServer) {
        return playerToObject(player, registeredServer, true);
    }

    public static PlayerObject playerToObject(Player player, RegisteredServer registeredServer, boolean online) {
        ServerObjectLink serverObjectLink = null;
        ProxyObjectLink proxyObject = null;

        ServerObjectBasicImplementation server = (ServerObjectBasicImplementation) TimoCloudAPI.getUniversalAPI().getServer(registeredServer.getServerInfo().getName());
        if (server != null) serverObjectLink = server.toLink();

        proxyObject = ((ProxyObjectBasicImplementation) TimoCloudAPI.getBungeeAPI().getThisProxy()).toLink();

        return new PlayerObjectVelocityImplementation(
                player.getUsername(),
                player.getUniqueId(),
                serverObjectLink,
                proxyObject,
                player.getRemoteAddress().getAddress(),
                online);
    }

}
