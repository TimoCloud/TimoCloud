package cloud.timo.TimoCloud.bungeecord.listeners;

import cloud.timo.TimoCloud.bungeecord.TimoCloudBungee;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ServerKick implements Listener {
    @EventHandler
    public void onServerKickEvent(ServerKickEvent event) {
        if (! TimoCloudBungee.getInstance().getFileManager().getConfig().getBoolean("useFallback")) return;
        event.setCancelled(true);
        event.setCancelServer(TimoCloudBungee.getInstance().getLobbyManager().getFreeLobby(event.getPlayer().getUniqueId(), true));
    }
}
