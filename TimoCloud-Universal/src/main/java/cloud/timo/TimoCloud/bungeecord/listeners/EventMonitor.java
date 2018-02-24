package cloud.timo.TimoCloud.bungeecord.listeners;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.events.PlayerConnectEvent;
import cloud.timo.TimoCloud.api.events.PlayerDisconnectEvent;
import cloud.timo.TimoCloud.api.events.PlayerServerChangeEvent;
import cloud.timo.TimoCloud.api.objects.PlayerObject;
import cloud.timo.TimoCloud.bungeecord.TimoCloudBungee;
import cloud.timo.TimoCloud.bungeecord.utils.PlayerUtil;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EventMonitor implements Listener {

    private Map<UUID, Boolean> pending;
    private Map<UUID, String> previousServer;

    public EventMonitor() {
        pending = new HashMap<>();
        previousServer = new HashMap<>();
    }

    private boolean isPending(UUID uuid) {
        pending.putIfAbsent(uuid, false);
        return pending.get(uuid);
    }

    @EventHandler
    public void onPlayerConnect(PostLoginEvent event) {
        TimoCloudBungee.getInstance().sendPlayerCount();
        pending.put(event.getPlayer().getUniqueId(), true);
    }

    @EventHandler
    public void onServerSwitchEvent(ServerSwitchEvent event) {
        PlayerObject playerObject = getPlayer(event.getPlayer());
        if (isPending(event.getPlayer().getUniqueId())) { // Join
            TimoCloudBungee.getInstance().getEventManager().sendEvent(new PlayerConnectEvent(playerObject));
        } else { // Server change
            TimoCloudBungee.getInstance().getEventManager().sendEvent(new PlayerServerChangeEvent(
                    playerObject,
                    previousServer.get(playerObject),
                    event.getPlayer().getServer().getInfo().getName()
            ));
        }
        previousServer.put(event.getPlayer().getUniqueId(), event.getPlayer().getServer().getInfo().getName());
    }

    @EventHandler
    public void onPlayerQuitEvent(net.md_5.bungee.api.event.PlayerDisconnectEvent event) {
        TimoCloudBungee.getInstance().sendPlayerCount();
        TimoCloudBungee.getInstance().getEventManager().sendEvent(new PlayerDisconnectEvent(getPlayer(event.getPlayer())));
    }

    private PlayerObject getPlayer(ProxiedPlayer proxiedPlayer) {
        PlayerObject player = TimoCloudAPI.getUniversalInstance().getPlayer(proxiedPlayer.getUniqueId());
        if (player != null) return player;
        return PlayerUtil.playerToObject(proxiedPlayer);
    }

}
