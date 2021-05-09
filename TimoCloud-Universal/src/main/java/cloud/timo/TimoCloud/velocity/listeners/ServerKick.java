package cloud.timo.TimoCloud.velocity.listeners;

import cloud.timo.TimoCloud.velocity.TimoCloudVelocity;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.KickedFromServerEvent;
import com.velocitypowered.api.proxy.server.RegisteredServer;

public class ServerKick {

    @Subscribe(order = PostOrder.LAST)
    public void onServerKickEvent(KickedFromServerEvent event) {
        if (!TimoCloudVelocity.getInstance().getFileManager().getConfig().getBoolean("useFallback")) return;
        if (!event.getPlayer().getCurrentServer().isPresent()) return;
        RegisteredServer server = TimoCloudVelocity.getInstance().getLobbyManager().getFreeLobby(event.getPlayer().getUniqueId(), true);
        if (server == null) {
            TimoCloudVelocity.getInstance().info("No fallback server found");
            return;
        }
        TimoCloudVelocity.getInstance().info("Connecting to fallback server: " + server.getServerInfo().getName());
        event.setResult(() -> false);
        event.getPlayer().createConnectionRequest(server).fireAndForget();
    }

}
