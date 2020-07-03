package cloud.timo.TimoCloud.bukkit.listeners;

import cloud.timo.TimoCloud.bukkit.TimoCloudBukkit;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockEvents implements Listener {

    @EventHandler
    public void onBlockPlaceEvent(BlockPlaceEvent event) {
        if (event.getBlockPlaced().getState() instanceof Sign) {
            if (TimoCloudBukkit.getInstance().getSignManager().signExists(event.getBlockPlaced().getLocation())) {
                TimoCloudBukkit.getInstance().getSignManager().lockSign(event.getBlockPlaced().getLocation());
            }
        }
    }
    @EventHandler
    public void onBlockBreakEvent(BlockBreakEvent event) {
        if (event.getBlock().getState() instanceof Sign) {
            if (TimoCloudBukkit.getInstance().getSignManager().signExists(event.getBlock().getLocation())) {
                TimoCloudBukkit.getInstance().getSignManager().removeSign(TimoCloudBukkit.getInstance().getSignManager().getSignInstanceByLocation(event.getBlock().getLocation()));
            }

        }
    }
}
