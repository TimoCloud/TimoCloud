package at.TimoCraft.TimoCloud.bukkit.listeners;

import at.TimoCraft.TimoCloud.bukkit.TimoCloudBukkit;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Created by Timo on 11.02.17.
 */
public class PlayerQuit implements Listener {
    @EventHandler (priority = EventPriority.LOWEST)
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(TimoCloudBukkit.getInstance(), () -> TimoCloudBukkit.getInstance().sendPlayers(), 1L);
    }
}
