package cloud.timo.TimoCloud.bukkit.listeners;

import cloud.timo.TimoCloud.api.events.EventHandler;
import cloud.timo.TimoCloud.api.events.Listener;
import cloud.timo.TimoCloud.api.events.server.ServerRegisterEvent;
import cloud.timo.TimoCloud.api.events.server.ServerUnregisterEvent;
import cloud.timo.TimoCloud.bukkit.TimoCloudBukkit;

public class ServerRegister implements Listener {

    @EventHandler
    public void onServerRegister(ServerRegisterEvent event) {
        TimoCloudBukkit instance = TimoCloudBukkit.getInstance();

        if (!event.getServer().getId().equals(instance.getServerId())) return;
        instance.setServerRegistered(true);
    }

    @EventHandler
    public void onServerUnregister(ServerUnregisterEvent event) {
        TimoCloudBukkit instance = TimoCloudBukkit.getInstance();

        if (!event.getServer().getId().equals(instance.getServerId())) return;
        instance.setServerRegistered(false);
    }
}