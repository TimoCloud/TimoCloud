package at.TimoCraft.TimoCloud.bukkit.listeners;

import at.TimoCraft.TimoCloud.bukkit.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Created by Timo on 28.12.16.
 */
public class SignChangeEvent implements Listener {
    @EventHandler
    public void onSignChangeEvent(org.bukkit.event.block.SignChangeEvent event) {
        if (event.getLine(0).trim().equalsIgnoreCase("[TimoCloud]")) {
            String server = event.getLine(1).trim();
            if (server == null || server == "") {
                event.getPlayer().sendMessage(Main.getInstance().getPrefix() + "§cPlease specify a server at line 2");
                event.setCancelled(true);
                return;
            }
            Main.getInstance().getSignManager().addSign(server, event.getBlock().getLocation());
        }
    }
}
