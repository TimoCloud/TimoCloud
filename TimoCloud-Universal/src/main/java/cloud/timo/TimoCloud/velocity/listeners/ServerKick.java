package cloud.timo.TimoCloud.velocity.listeners;

import cloud.timo.TimoCloud.api.objects.ServerObject;
import cloud.timo.TimoCloud.velocity.TimoCloudVelocity;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.KickedFromServerEvent;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import java.util.Optional;

public class ServerKick {

    @Subscribe(order = PostOrder.LAST)
    public void onServerKickEvent(KickedFromServerEvent event) {
        if (!TimoCloudVelocity.getInstance().getFileManager().getConfig().getBoolean("useFallback")) return;
        if (!event.getPlayer().getCurrentServer().isPresent()) return;
        final ServerObject freeLobby = TimoCloudVelocity.getInstance().getLobbyManager().getFreeLobby(event.getPlayer().getUniqueId(), true);
        if (freeLobby == null) {
            TimoCloudVelocity.getInstance().info("No fallback server found");
            return;
        }
        final Optional<RegisteredServer> server = TimoCloudVelocity.getInstance().getServer().getServer(
                freeLobby.getName());
        if (!server.isPresent()) {
            TimoCloudVelocity.getInstance().info("No fallback server found");
            return;
        }
        TimoCloudVelocity.getInstance().info("Connecting to fallback server: " + server.get().getServerInfo().getName());
        event.setResult(KickedFromServerEvent.RedirectPlayer.create(server.get()));
    }

}
