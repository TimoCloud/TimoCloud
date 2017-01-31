package at.TimoCraft.TimoCloud.bukkit.listeners;

import at.TimoCraft.TimoCloud.bukkit.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

/**
 * Created by Timo on 28.12.16.
 */
public class SignChange implements Listener {

    @EventHandler
    public void onSignChangeEvent(SignChangeEvent event) {
        if (event.getLine(0).trim().equalsIgnoreCase("[TimoCloud]")) {
            String server = event.getLine(1).trim();
            if (server == null || server.equals("")) {
                event.getPlayer().sendMessage(Main.getInstance().getPrefix() + "§cPlease specify a server at line 2");
                event.setCancelled(true);
                return;
            }
            if (server.equalsIgnoreCase("Spectate")) {
                
                return;
            }
            Main.getInstance().getSignManager().addSign(server, event.getBlock().getLocation());
        }
    }
}
