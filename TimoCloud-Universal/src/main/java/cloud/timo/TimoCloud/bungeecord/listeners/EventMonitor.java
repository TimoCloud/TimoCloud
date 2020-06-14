package cloud.timo.TimoCloud.bungeecord.listeners;

import cloud.timo.TimoCloud.api.events.player.PlayerConnectEventBasicImplementation;
import cloud.timo.TimoCloud.api.events.player.PlayerDisconnectEventBasicImplementation;
import cloud.timo.TimoCloud.api.events.player.PlayerServerChangeEventBasicImplementation;
import cloud.timo.TimoCloud.api.objects.PlayerObject;
import cloud.timo.TimoCloud.bungeecord.TimoCloudBungee;
import cloud.timo.TimoCloud.bungeecord.utils.PlayerUtil;
import cloud.timo.TimoCloud.common.events.EventTransmitter;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.*;

public class EventMonitor implements Listener {

    private Set<UUID> pending;
    private Map<UUID, String> previousServer;

    public EventMonitor() {
        pending = new HashSet<>();
        previousServer = new HashMap<>();
    }

    private boolean isPending(UUID uuid) {
        return pending.contains(uuid);
    }

    @EventHandler
    public void onPlayerConnect(PostLoginEvent event) {
        TimoCloudBungee.getInstance().sendPlayerCount();
        pending.add(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onServerSwitchEvent(ServerSwitchEvent event) {
        PlayerObject playerObject = getPlayer(event.getPlayer());
        if (isPending(event.getPlayer().getUniqueId())) { // Join
            EventTransmitter.sendEvent(new PlayerConnectEventBasicImplementation(playerObject));
            pending.remove(event.getPlayer().getUniqueId());
        } else { // Server change
            EventTransmitter.sendEvent(new PlayerServerChangeEventBasicImplementation(
                    playerObject,
                    previousServer.get(playerObject.getUuid()),
                    event.getPlayer().getServer().getInfo().getName()));
        }

        previousServer.put(event.getPlayer().getUniqueId(), event.getPlayer().getServer().getInfo().getName());
    }

    @EventHandler
    public void onPlayerQuitEvent(net.md_5.bungee.api.event.PlayerDisconnectEvent event) {
        TimoCloudBungee.getInstance().sendPlayerCount();

        //PlayerUtil.playerToObject is using getThisProxy method. this is returning null when server stops
        if (!TimoCloudBungee.getInstance().isShuttingDown()) return;
        EventTransmitter.sendEvent(new PlayerDisconnectEventBasicImplementation(getPlayer(event.getPlayer())));
    }

    private PlayerObject getPlayer(ProxiedPlayer proxiedPlayer) {
        return PlayerUtil.playerToObject(proxiedPlayer);
    }

}
