package at.TimoCraft.TimoCloud.bukkit.listeners;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/**
 * Created by Timo on 27.12.16.
 */
public class ProxyPingEvent implements Listener {
    @EventHandler (priority = EventPriority.HIGHEST)
    public void onProxyPingEvent(net.md_5.bungee.api.event.ProxyPingEvent event) {
        event.getResponse().setDescriptionComponent(new TextComponent("ONLINE"));
    }
}
