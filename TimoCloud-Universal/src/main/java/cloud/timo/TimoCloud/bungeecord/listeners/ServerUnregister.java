package cloud.timo.TimoCloud.bungeecord.listeners;

import cloud.timo.TimoCloud.api.events.EventHandler;
import cloud.timo.TimoCloud.api.events.Listener;
import cloud.timo.TimoCloud.api.events.server.ServerUnregisterEvent;
import cloud.timo.TimoCloud.bungeecord.TimoCloudBungee;

public class ServerUnregister implements Listener {

    @EventHandler
    public void onServerUnregister(ServerUnregisterEvent event) {
        TimoCloudBungee.getInstance().getTimoCloudCommand().removeServerName(event.getServer().getName());
    }
}
