package at.TimoCraft.TimoCloud.bukkit.listeners;

import at.TimoCraft.TimoCloud.bukkit.Main;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Created by Timo on 30.12.16.
 */
public class PlayerInteract implements Listener {

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        Block block = event.getClickedBlock();
        if (block.getType() != Material.WALL_SIGN) {
            return;
        }
        if (! Main.getInstance().getSignManager().getSigns().containsKey(block.getLocation())) {
            return;
        }

        String server = Main.getInstance().getSignManager().getServerOnSign(block.getLocation());
        String group = Main.getInstance().getGroupByServer(server);
        String state = Main.getInstance().getOtherServerPingManager().getState(server);
        if (Main.getInstance().getSignManager().shouldBeSortedOut(state, group)) {
            return;
        }
        Main.getInstance().sendPlayerToServer(event.getPlayer(), Main.getInstance().getSignManager().getSigns().get(block.getLocation()));
    }
}
