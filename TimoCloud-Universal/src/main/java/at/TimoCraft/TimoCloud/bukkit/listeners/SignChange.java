package at.TimoCraft.TimoCloud.bukkit.listeners;

import at.TimoCraft.TimoCloud.bukkit.TimoCloudBukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

public class SignChange implements Listener {

    private int parseIntOr0(String string) {
        try {
            return Integer.parseInt(string.trim());
        } catch (Exception e) {
            return 0;
        }
    }

    @EventHandler
    public void onSignChangeEvent(SignChangeEvent event) {
        if (! event.getPlayer().hasPermission("timocloud.signs.create")) return;
        if (TimoCloudBukkit.getInstance().getSignManager().signExists(event.getBlock().getLocation())) TimoCloudBukkit.getInstance().getSignManager().unlockSign(event.getBlock().getLocation());
        if (event.getLine(0).trim().equalsIgnoreCase("[TimoCloud]")) {
            String target = event.getLine(1).trim();
            String template = event.getLine(2).trim();
            TimoCloudBukkit.getInstance().getSignManager().addSign(event.getBlock().getLocation(), target, template, parseIntOr0(event.getLine(3)), event.getPlayer());
        }
    }
}
