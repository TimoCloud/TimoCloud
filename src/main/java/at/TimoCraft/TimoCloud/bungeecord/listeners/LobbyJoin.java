package at.TimoCraft.TimoCloud.bungeecord.listeners;

import at.TimoCraft.TimoCloud.bungeecord.TimoCloud;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.ArrayList;
import java.util.List;

import java.util.concurrent.TimeUnit;

/**
 * Created by Timo on 28.12.16.
 */
public class LobbyJoin implements Listener {
    private List<ProxiedPlayer> pending;

    public LobbyJoin() {
        pending = new ArrayList<>();
    }

    @EventHandler
    public void onPlayerConnect(PostLoginEvent event) {
        pending.add(event.getPlayer());
    }

    @EventHandler
    public void onServerChange(ServerConnectEvent event) {
        if (! pending.contains(event.getPlayer())) {
            return;
        }
        ServerInfo info = TimoCloud.getInstance().getServerManager().getRandomLobbyServer(null);
        if (info == null) {
            TimoCloud.severe("No lobby server found.");
            return;
        }
        event.setTarget(info);
        pending.remove(event.getPlayer());
    }

    @EventHandler
    public void onServerKick(ServerKickEvent event) {
        event.setCancelled(true);
        event.setCancelServer(TimoCloud.getInstance().getServerManager().getRandomLobbyServer(event.getKickedFrom()));
    }
}
