package at.TimoCraft.TimoCloud.bungeecord.listeners;

import at.TimoCraft.TimoCloud.bungeecord.TimoCloud;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

/**
 * Created by Timo on 28.12.16.
 */
public class PlayerConnect implements Listener {
    @EventHandler
    public void onPlayerConnect(PostLoginEvent event) {
        event.getPlayer().connect(TimoCloud.getInstance().getServerManager().getRandomLobbyServer());
    }
}
