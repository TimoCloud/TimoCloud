package cloud.timo.TimoCloud.bukkit.listeners;

import cloud.timo.TimoCloud.bukkit.TimoCloudBukkit;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuit implements Listener {
    @EventHandler (priority = EventPriority.LOWEST)
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        event.getPlayer().kickPlayer("");
        Bukkit.getScheduler().scheduleSyncDelayedTask(TimoCloudBukkit.getInstance(), () -> {
            TimoCloudBukkit.getInstance().sendPlayers();
            TimoCloudBukkit.getInstance().getStateByEventManager().onPlayerQuit();
        }, 1L);
    }
}
