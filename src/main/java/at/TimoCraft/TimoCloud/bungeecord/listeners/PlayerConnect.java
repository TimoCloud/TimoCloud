package at.TimoCraft.TimoCloud.bungeecord.listeners;

import at.TimoCraft.TimoCloud.bungeecord.TimoCloud;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.concurrent.TimeUnit;

/**
 * Created by Timo on 28.12.16.
 */
public class PlayerConnect implements Listener {
    @EventHandler
    public void onPlayerConnect(PostLoginEvent event) {
        ServerInfo info = TimoCloud.getInstance().getServerManager().getRandomLobbyServer(null);
        if (info == null) {
            TimoCloud.severe("No lobby server found.");
            return;
        }
        event.getPlayer().connect(info);
    }
}
