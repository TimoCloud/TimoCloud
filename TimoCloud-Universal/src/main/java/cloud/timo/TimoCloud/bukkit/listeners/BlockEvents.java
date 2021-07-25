package cloud.timo.TimoCloud.bukkit.listeners;

import cloud.timo.TimoCloud.bukkit.TimoCloudBukkit;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockEvents implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPlaceEvent(BlockPlaceEvent event) {
        if (event.getBlockPlaced().getState() instanceof Sign) {
            TimoCloudBukkit instance = TimoCloudBukkit.getInstance();

            if (instance.getSignManager().signExists(event.getBlockPlaced().getLocation())) {
                instance.getSignManager().lockSign(event.getBlockPlaced().getLocation());
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreakEvent(BlockBreakEvent event) {
        if (event.getBlock().getState() instanceof Sign) {
            TimoCloudBukkit instance = TimoCloudBukkit.getInstance();

            if (instance.getSignManager().signExists(event.getBlock().getLocation())) {
                instance.getSignManager().removeSign(TimoCloudBukkit.getInstance().getSignManager().getSignInstanceByLocation(event.getBlock().getLocation()));
            }

        }
    }
}
