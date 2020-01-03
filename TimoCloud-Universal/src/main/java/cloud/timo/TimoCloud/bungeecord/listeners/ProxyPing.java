package cloud.timo.TimoCloud.bungeecord.listeners;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.objects.ProxyObject;
import cloud.timo.TimoCloud.bungeecord.TimoCloudBungee;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class ProxyPing implements Listener {

    @EventHandler (priority = EventPriority.LOWEST)
    public void onProxyPingEvent(ProxyPingEvent event) {
        ProxyObject proxyObject = TimoCloudAPI.getBungeeAPI().getThisProxy();

        ServerPing serverPing = event.getResponse();
        serverPing.setPlayers(new ServerPing.Players(
                proxyObject.getGroup().getMaxPlayerCount(),
                proxyObject.getGroup().getOnlinePlayerCount(),
                serverPing.getPlayers().getSample()
        ));
        if (TimoCloudBungee.getInstance().getFileManager().getConfig().getBoolean("useGlobalMotd"))
            serverPing.setDescriptionComponent(new TextComponent(ChatColor.translateAlternateColorCodes('&', proxyObject.getGroup().getMotd())));
    }
}
