package at.TimoCraft.TimoCloud.bungeecord.listeners;

import at.TimoCraft.TimoCloud.bungeecord.TimoCloud;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ServerKick implements Listener {
    @EventHandler
    public void onServerKickEvent(ServerKickEvent event) {
        if (! TimoCloud.getInstance().getFileManager().getConfig().getBoolean("useFallback")) return;
        event.setCancelled(true);
        event.setCancelServer(TimoCloud.getInstance().getLobbyManager().getFreeLobby(event.getPlayer().getUniqueId(), true));
    }
}
