package cloud.timo.TimoCloud.bungeecord.listeners;

import cloud.timo.TimoCloud.bungeecord.TimoCloudBungee;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.event.PlayerHandshakeEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.lang.reflect.Field;

public class IpInjector implements Listener {

    @EventHandler (priority = -128)
    public void onPreLoginEvent(PreLoginEvent event) {
        injectConnection(event.getConnection());
    }

    @EventHandler (priority = -128)
    public void onProxyPingEvent(ProxyPingEvent event) {
        injectConnection(event.getConnection());
    }

    @EventHandler (priority = -128)
    public void onPlayerHandshakeEvent(PlayerHandshakeEvent event) {
        injectConnection(event.getConnection());
    }

    private void injectConnection(Connection connection) {
        if (TimoCloudBungee.getInstance().getIpManager().getAddressByChannel(connection.getAddress()) == null) return;
        try {
            Field wrapperField = connection.getClass().getDeclaredField("ch");
            wrapperField.setAccessible(true);
            Object wrapper = wrapperField.get(connection);
            Field addressField = wrapper.getClass().getDeclaredField("remoteAddress");
            addressField.setAccessible(true);
            addressField.set(wrapper, TimoCloudBungee.getInstance().getIpManager().getAddressByChannel(connection.getAddress()));
        } catch (Exception e) {
            TimoCloudBungee.getInstance().severe("Error while injecting ip address: ");
            e.printStackTrace();
        }
    }

}
