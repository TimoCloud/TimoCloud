package cloud.timo.TimoCloud.velocity.listeners;

import cloud.timo.TimoCloud.velocity.TimoCloudVelocity;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.ConnectionHandshakeEvent;
import com.velocitypowered.api.event.connection.PreLoginEvent;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.proxy.InboundConnection;

import java.lang.reflect.Field;

public class IpInjector {

    @Subscribe(order = PostOrder.EARLY)
    public void onPreLoginEvent(PreLoginEvent event) {
        injectConnection(event.getConnection());
    }

    @Subscribe(order = PostOrder.EARLY)
    public void onProxyPingEvent(ProxyPingEvent event) {
        injectConnection(event.getConnection());
    }

    @Subscribe(order = PostOrder.FIRST)
    public void onPlayerHandshakeEvent(ConnectionHandshakeEvent event) {
        injectConnection(event.getConnection());
    }

    private void injectConnection(InboundConnection connection) {
        if (TimoCloudVelocity.getInstance().getIpManager().getAddressByChannel(connection.getRemoteAddress()) == null)
            return;
        try {
            Field wrapperField = connection.getClass().getDeclaredField("ch");
            wrapperField.setAccessible(true);
            Object wrapper = wrapperField.get(connection);
            Field addressField = wrapper.getClass().getDeclaredField("remoteAddress");
            addressField.setAccessible(true);
            addressField.set(wrapper, TimoCloudVelocity.getInstance().getIpManager().getAddressByChannel(connection.getRemoteAddress()));
        } catch (Exception e) {
            TimoCloudVelocity.getInstance().severe("Error while injecting ip address: ");
            TimoCloudVelocity.getInstance().severe(e);
        }
    }

}
