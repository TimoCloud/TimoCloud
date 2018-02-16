package cloud.timo.TimoCloud.bungeecord.listeners;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.events.PlayerConnectEvent;
import cloud.timo.TimoCloud.api.events.PlayerDisconnectEvent;
import cloud.timo.TimoCloud.api.events.PlayerServerChangeEvent;
import cloud.timo.TimoCloud.api.objects.PlayerObject;
import cloud.timo.TimoCloud.bungeecord.TimoCloudBungee;
import cloud.timo.TimoCloud.bungeecord.utils.PlayerUtil;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class EventMonitor implements Listener {

    @EventHandler
    public void onServerSwitchEvent(ServerSwitchEvent event) {
        PlayerObject playerObject = TimoCloudAPI.getUniversalInstance().getPlayer(event.getPlayer().getUniqueId());
        if (playerObject == null) {
            TimoCloudBungee.getInstance().getEventManager().sendEvent(new PlayerConnectEvent(PlayerUtil.playerToObject(event.getPlayer())));
        } else {
            TimoCloudBungee.getInstance().getEventManager().sendEvent(new PlayerServerChangeEvent(
                    playerObject,
                    playerObject.getServer().getName(),
                    event.getPlayer().getServer().getInfo().getName()
            ));
        }
    }

    @EventHandler
    public void onPlayerQuitEvent(net.md_5.bungee.api.event.PlayerDisconnectEvent event) {
        TimoCloudBungee.getInstance().getEventManager().sendEvent(new PlayerDisconnectEvent(TimoCloudAPI.getUniversalInstance().getPlayer(event.getPlayer().getUniqueId())));
    }

}
