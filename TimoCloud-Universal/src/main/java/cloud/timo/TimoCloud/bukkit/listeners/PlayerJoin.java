package cloud.timo.TimoCloud.bukkit.listeners;

import cloud.timo.TimoCloud.bukkit.TimoCloudBukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoin implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        TimoCloudBukkit instance = TimoCloudBukkit.getInstance();
        instance.getServer().getScheduler().runTaskAsynchronously(instance, instance::sendPlayers);
        instance.getStateByEventManager().onPlayerJoin();
    }

}
