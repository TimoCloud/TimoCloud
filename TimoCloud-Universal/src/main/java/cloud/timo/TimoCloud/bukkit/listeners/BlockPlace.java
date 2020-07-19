package cloud.timo.TimoCloud.bukkit.listeners;

import cloud.timo.TimoCloud.bukkit.TimoCloudBukkit;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockPlace implements Listener {

    @EventHandler
    public void onBlockPlaceEvent(BlockPlaceEvent event) {
        if (event.getBlockPlaced().getState() instanceof Sign) {
            if (TimoCloudBukkit.getInstance().getSignManager().signExists(event.getBlockPlaced().getLocation())) {
                TimoCloudBukkit.getInstance().getSignManager().lockSign(event.getBlockPlaced().getLocation());
            }
        }
    }

}
