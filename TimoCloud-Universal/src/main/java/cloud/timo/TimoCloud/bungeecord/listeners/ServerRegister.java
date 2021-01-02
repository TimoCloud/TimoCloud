package cloud.timo.TimoCloud.bungeecord.listeners;

import cloud.timo.TimoCloud.api.events.EventHandler;
import cloud.timo.TimoCloud.api.events.Listener;
import cloud.timo.TimoCloud.api.events.server.ServerRegisterEvent;
import cloud.timo.TimoCloud.bungeecord.TimoCloudBungee;

public class ServerRegister implements Listener {

    @EventHandler
    public void onServerRegister(ServerRegisterEvent event) {
        TimoCloudBungee.getInstance().getTimoCloudCommand().addServerName(event.getServer().getName());
    }
}
