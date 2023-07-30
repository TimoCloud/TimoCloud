package cloud.timo.TimoCloud.bukkit.listeners;

import cloud.timo.TimoCloud.bukkit.TimoCloudBukkit;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerInteract implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
        Block block = event.getClickedBlock();

        if (block.getType().name().contains("SIGN")) {
            if (TimoCloudBukkit.getInstance().getSignManager().onSignClick(event.getPlayer(), block.getLocation())) {
                event.setCancelled(true);
            }
        }
    }

}
