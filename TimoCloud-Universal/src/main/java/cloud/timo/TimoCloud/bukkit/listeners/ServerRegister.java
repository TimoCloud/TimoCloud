package cloud.timo.TimoCloud.bukkit.listeners;

import cloud.timo.TimoCloud.api.events.EventHandler;
import cloud.timo.TimoCloud.api.events.Listener;
import cloud.timo.TimoCloud.api.events.server.ServerRegisterEvent;
import cloud.timo.TimoCloud.api.events.server.ServerUnregisterEvent;
import cloud.timo.TimoCloud.bukkit.TimoCloudBukkit;

public class ServerRegister implements Listener {

    @EventHandler
    public void onServerRegister(ServerRegisterEvent event) {
        if (!event.getServer().getId().equals(TimoCloudBukkit.getInstance().getServerId())) return;
        TimoCloudBukkit.getInstance().setServerRegistered(true);
    }

    @EventHandler
    public void onServerUnregister(ServerUnregisterEvent event) {
        if (!event.getServer().getId().equals(TimoCloudBukkit.getInstance().getServerId())) return;
        TimoCloudBukkit.getInstance().setServerRegistered(false);
    }
}