package cloud.timo.TimoCloud.bukkit.listeners;

import cloud.timo.TimoCloud.bukkit.TimoCloudBukkit;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuit implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        TimoCloudBukkit instance = TimoCloudBukkit.getInstance();
        instance.getServer().getScheduler().runTaskAsynchronously(instance, instance::sendPlayers);
        Bukkit.getScheduler().scheduleSyncDelayedTask(instance, () ->
                instance.getStateByEventManager().onPlayerQuit(), 1L);
    }
}
