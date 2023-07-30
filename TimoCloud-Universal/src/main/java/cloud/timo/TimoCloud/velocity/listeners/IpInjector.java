package cloud.timo.TimoCloud.velocity.listeners;

import cloud.timo.TimoCloud.velocity.TimoCloudVelocity;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.ConnectionHandshakeEvent;
import com.velocitypowered.api.event.connection.PreLoginEvent;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.proxy.InboundConnection;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.stream.Collectors;

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
            Object wrapper = null;
            try {
                Field wrapperField = connection.getClass().getDeclaredField("connection");
                wrapperField.setAccessible(true);
                     wrapper = wrapperField.get(connection);
            } catch (NoSuchFieldException e) {
                try {
                    Field loginInboundClass = connection.getClass().getDeclaredField("delegate");
                    loginInboundClass.setAccessible(true);
                    Object delegate = loginInboundClass.get(connection);

                    Field connectionField = delegate.getClass().getDeclaredField("connection");
                    connectionField.setAccessible(true);
                    wrapper = connectionField.get(delegate);



                } catch (NoSuchFieldException e2) {
                    TimoCloudVelocity.getInstance().warning("!!IP Injection Error!!");
                    TimoCloudVelocity.getInstance().warning("!!Please Report this!!");
                    TimoCloudVelocity.getInstance().warning(connection.getClass().getName());
                    TimoCloudVelocity.getInstance().warning(Arrays.stream(connection.getClass().getDeclaredFields()).map(Field::getName).collect(Collectors.joining(", ")));
                }
            }
            if(wrapper == null) return;
            Field addressField = wrapper.getClass().getDeclaredField("remoteAddress");
            addressField.setAccessible(true);
            addressField.set(wrapper, TimoCloudVelocity.getInstance().getIpManager().getAddressByChannel(connection.getRemoteAddress()));
        } catch (Exception e) {
            TimoCloudVelocity.getInstance().severe("Error while injecting ip address: ");
            TimoCloudVelocity.getInstance().severe(e);
        }
    }

}
