package at.TimoCraft.TimoCloud.bungeecord.listeners;

import at.TimoCraft.TimoCloud.bungeecord.TimoCloud;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.*;

/**
 * Created by Timo on 28.12.16.
 */
public class LobbyJoin implements Listener {
    private Map<UUID, Boolean> pending;

    public LobbyJoin() {
        pending = new HashMap<>();
    }

    private boolean isPending(UUID uuid) {
        pending.putIfAbsent(uuid, false);
        return pending.get(uuid);
    }

    @EventHandler
    public void onPlayerConnect(PostLoginEvent event) {
        if (! TimoCloud.getInstance().getFileManager().getConfig().getBoolean("useFallback")) {
            return;
        }
        pending.put(event.getPlayer().getUniqueId(), true);
    }

    @EventHandler
    public void onServerChange(ServerConnectEvent event) {
        if (! isPending(event.getPlayer().getUniqueId())) {
            return;
        }
        ServerInfo info = TimoCloud.getInstance().getServerManager().getRandomLobbyServer(null);
        if (info == null) {
            TimoCloud.severe("No lobby server found.");
            return;
        }
        event.setTarget(info);
        pending.put(event.getPlayer().getUniqueId(), false);
    }

    @EventHandler
    public void onServerKick(ServerKickEvent event) {
        event.setCancelled(true);
        event.setCancelServer(TimoCloud.getInstance().getServerManager().getRandomLobbyServer(event.getKickedFrom()));
    }
}
