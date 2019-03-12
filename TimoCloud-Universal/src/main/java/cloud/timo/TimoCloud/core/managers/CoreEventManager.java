package cloud.timo.TimoCloud.core.managers;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.events.Event;
import cloud.timo.TimoCloud.api.events.EventHandler;
import cloud.timo.TimoCloud.api.events.Listener;
import cloud.timo.TimoCloud.api.events.player.PlayerConnectEvent;
import cloud.timo.TimoCloud.api.events.player.PlayerDisconnectEvent;
import cloud.timo.TimoCloud.api.events.player.PlayerServerChangeEvent;
import cloud.timo.TimoCloud.api.implementations.TimoCloudUniversalAPIBasicImplementation;
import cloud.timo.TimoCloud.api.implementations.managers.EventManager;
import cloud.timo.TimoCloud.api.implementations.objects.PlayerObjectBasicImplementation;
import cloud.timo.TimoCloud.api.objects.ProxyObject;
import cloud.timo.TimoCloud.api.objects.ServerObject;
import cloud.timo.TimoCloud.common.protocol.Message;
import cloud.timo.TimoCloud.common.protocol.MessageType;
import cloud.timo.TimoCloud.core.TimoCloudCore;
import cloud.timo.TimoCloud.core.objects.Base;
import cloud.timo.TimoCloud.core.objects.Proxy;
import cloud.timo.TimoCloud.core.objects.Server;
import cloud.timo.TimoCloud.core.sockets.Communicatable;

public class CoreEventManager implements Listener {

    public void fireEvent(Event event) {
        Message message = Message.create()
                .setType(MessageType.EVENT_FIRED)
                .set("eventType", event.getType().name())
                .setData(eventToJSON(event));
        for (Communicatable communicatable : TimoCloudCore.getInstance().getInstanceManager().getAllCommunicatableInstances()) {
            if (communicatable instanceof Base) continue; // Bases do not support events
            communicatable.sendMessage(message);
        }
        ((EventManager) TimoCloudAPI.getEventAPI()).callEvent(event);
    }

    private static String eventToJSON(Event event) {
        try {
            return ((TimoCloudUniversalAPIBasicImplementation) TimoCloudAPI.getUniversalAPI()).getObjectMapper().writeValueAsString(event);
        } catch (Exception e) {
            TimoCloudCore.getInstance().severe("Error while converting Event to JSON: ");
            TimoCloudCore.getInstance().severe(e);
            return null;
        }
    }

    @EventHandler
    public void onPlayerConnectEvent(PlayerConnectEvent event) {
        ProxyObject proxyObject = event.getPlayer().getProxy();
        if (proxyObject != null) {
            Proxy proxy = TimoCloudCore.getInstance().getInstanceManager().getProxyByProxyObject(proxyObject);
            if (proxy != null) proxy.onPlayerConnect(event.getPlayer());
        }
        ServerObject serverObject = event.getPlayer().getServer();
        if (serverObject != null) {
            Server server = TimoCloudCore.getInstance().getInstanceManager().getServerByServerObject(serverObject);
            if (server != null) server.onPlayerConnect(event.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerDisconnect(PlayerDisconnectEvent event) {
        ((PlayerObjectBasicImplementation) event.getPlayer()).setOnline(false);
        ProxyObject proxyObject = event.getPlayer().getProxy();
        if (proxyObject != null) {
            Proxy proxy = TimoCloudCore.getInstance().getInstanceManager().getProxyByProxyObject(proxyObject);
            if (proxy != null) proxy.onPlayerDisconnect(event.getPlayer());
        }
        ServerObject serverObject = event.getPlayer().getServer();
        if (serverObject != null) {
            Server server = TimoCloudCore.getInstance().getInstanceManager().getServerByServerObject(serverObject);
            if (server != null) server.onPlayerDisconnect(event.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerServerChange(PlayerServerChangeEvent event) {
        Server serverFrom = TimoCloudCore.getInstance().getInstanceManager().getServerByServerObject(event.getServerFrom());
        if (serverFrom != null) serverFrom.onPlayerDisconnect(event.getPlayer());
        Server serverTo = TimoCloudCore.getInstance().getInstanceManager().getServerByServerObject(event.getServerTo());
        if (serverTo != null) serverTo.onPlayerConnect(event.getPlayer());
        Proxy proxy = TimoCloudCore.getInstance().getInstanceManager().getProxyByProxyObject(event.getPlayer().getProxy());
        proxy.update(event.getPlayer());
    }

}
