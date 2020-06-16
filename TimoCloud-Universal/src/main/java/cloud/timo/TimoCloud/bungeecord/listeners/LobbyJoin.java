package cloud.timo.TimoCloud.bungeecord.listeners;

import cloud.timo.TimoCloud.bungeecord.TimoCloudBungee;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class LobbyJoin implements Listener {
    private Set<UUID> pending;

    public LobbyJoin() {
        pending = new HashSet<>();
    }

    private boolean isPending(UUID uuid) {
        return pending.contains(uuid);
    }

    @EventHandler
    public void onPlayerConnect(PostLoginEvent event) {
        if (!useFallback()) return;
        pending.add(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onServerConnect(ServerConnectEvent event) {
        if (!useFallback()) return;
        if (!isPending(event.getPlayer().getUniqueId())) return;

        ProxiedPlayer player = event.getPlayer();
        ServerInfo info = TimoCloudBungee.getInstance().getLobbyManager().getFreeLobby(player.getUniqueId());
        if (info == null) {
            TimoCloudBungee.getInstance().severe("No lobby server found.");
            pending.remove(player.getUniqueId());

            kickPlayer(player);
            event.setCancelled(true);
            return;
        }
        event.setTarget(info);
        pending.remove(player.getUniqueId());
    }

    @EventHandler
    public void onServerKick(ServerKickEvent event) {
        if (!useFallback()) return;
        event.setCancelled(true);
        event.setCancelServer(TimoCloudBungee.getInstance().getLobbyManager().getFreeLobby(event.getPlayer().getUniqueId()));
    }

    private void kickPlayer(ProxiedPlayer proxiedPlayer) {
        String fallBackMessage = TimoCloudBungee.getInstance().getFileManager().getMessages().getString("NoFallBackGroupFound");
        fallBackMessage = ChatColor.translateAlternateColorCodes('&', fallBackMessage);
        proxiedPlayer.disconnect(TextComponent.fromLegacyText(fallBackMessage));
    }

    private boolean useFallback() {
        return TimoCloudBungee.getInstance().getFileManager().getConfig().getBoolean("useFallback");
    }

}
