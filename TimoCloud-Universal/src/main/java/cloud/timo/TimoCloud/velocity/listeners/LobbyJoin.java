package cloud.timo.TimoCloud.velocity.listeners;

import cloud.timo.TimoCloud.common.utils.ChatColorUtil;
import cloud.timo.TimoCloud.velocity.TimoCloudVelocity;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.event.player.KickedFromServerEvent;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.text.TextComponent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class LobbyJoin {
    private final Set<UUID> pending;

    public LobbyJoin() {
        pending = new HashSet<>();
    }

    private boolean isPending(UUID uuid) {
        return pending.contains(uuid);
    }

    @Subscribe
    public void onPlayerConnect(PostLoginEvent event) {
        if (!useFallback()) return;
        pending.add(event.getPlayer().getUniqueId());
    }

    @Subscribe
    public void onServerConnect(ServerPreConnectEvent event) {
        if (!useFallback()) return;
        if (!isPending(event.getPlayer().getUniqueId())) return;

        Player player = event.getPlayer();
        RegisteredServer info = TimoCloudVelocity.getInstance().getServer().getServer(TimoCloudVelocity.getInstance().getLobbyManager().getFreeLobby(player.getUniqueId()).getName()).get();
        if (info == null) {
            TimoCloudVelocity.getInstance().severe("No lobby server found.");
            pending.remove(player.getUniqueId());
            kickPlayer(player);
            return;
        }
        event.setResult(ServerPreConnectEvent.ServerResult.allowed(info));
        pending.remove(player.getUniqueId());
    }

    @Subscribe
    public void onServerKick(KickedFromServerEvent event) {
        if (!useFallback()) return;
        RegisteredServer freeLobby = TimoCloudVelocity.getInstance().getServer().getServer(TimoCloudVelocity.getInstance().getLobbyManager().getFreeLobby(event.getPlayer().getUniqueId()).getName()).get();
        if (freeLobby == null) return;
        event.setResult(KickedFromServerEvent.RedirectPlayer.create(freeLobby));
    }

    private void kickPlayer(Player player) {
        String fallBackMessage = TimoCloudVelocity.getInstance().getFileManager().getMessages().getString("NoFallBackGroupFound");
        player.disconnect(TextComponent.of(ChatColorUtil.translateAlternateColorCodes('&', fallBackMessage)));
    }

    private boolean useFallback() {
        return TimoCloudVelocity.getInstance().getFileManager().getConfig().getBoolean("useFallback");
    }

}
