package at.TimoCraft.TimoCloud.bungeecord.listeners;

import at.TimoCraft.TimoCloud.bungeecord.TimoCloud;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

/**
 * Created by Timo on 01.01.17.
 */
public class ServerKick implements Listener {
    @EventHandler
    public void onServerKickEvent(ServerKickEvent event) {
        event.setCancelled(true);
        event.setCancelServer(TimoCloud.getInstance().getServerManager().getRandomLobbyServer(event.getKickedFrom()));
    }
}
