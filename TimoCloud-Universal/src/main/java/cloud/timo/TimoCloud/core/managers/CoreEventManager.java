package cloud.timo.TimoCloud.core.managers;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.events.*;
import cloud.timo.TimoCloud.api.implementations.EventManager;
import cloud.timo.TimoCloud.api.implementations.PlayerObjectBasicImplementation;
import cloud.timo.TimoCloud.api.objects.Event;
import cloud.timo.TimoCloud.api.objects.ProxyObject;
import cloud.timo.TimoCloud.api.objects.ServerObject;
import cloud.timo.TimoCloud.core.TimoCloudCore;
import cloud.timo.TimoCloud.core.objects.Proxy;
import cloud.timo.TimoCloud.core.objects.Server;
import cloud.timo.TimoCloud.core.sockets.Communicatable;
import cloud.timo.TimoCloud.lib.implementations.TimoCloudUniversalAPIBasicImplementation;
import cloud.timo.TimoCloud.lib.objects.JSONBuilder;
import org.json.simple.JSONObject;

public class CoreEventManager implements Listener {

    public void fireEvent(Event event) {
        JSONObject json = JSONBuilder.create()
                .setType("EVENT_FIRED")
                .set("eventType", event.getType().name())
                .setData(eventToJSON(event))
                .toJson();
        for (Communicatable communicatable : TimoCloudCore.getInstance().getServerManager().getAllCommunicatableInstances()) {
            communicatable.sendMessage(json);
        }
        ((EventManager) TimoCloudAPI.getEventImplementation()).callEvent(event);
    }

    private static String eventToJSON(Event event) {
        try {
            return ((TimoCloudUniversalAPIBasicImplementation) TimoCloudAPI.getUniversalInstance()).getObjectMapper().writeValueAsString(event);
        } catch (Exception e) {
            TimoCloudCore.getInstance().severe("Error while converting Event to JSON: ");
            e.printStackTrace();
            return null;
        }
    }

    @EventHandler
    public void onPlayerConnectEvent(PlayerConnectEvent event) {
        ProxyObject proxyObject = event.getPlayer().getProxy();
        if (proxyObject != null) {
            Proxy proxy = TimoCloudCore.getInstance().getServerManager().getProxyByName(proxyObject.getName());
            if (proxy != null) proxy.onPlayerConnect(event.getPlayer());
        }
        ServerObject serverObject = event.getPlayer().getServer();
        if (serverObject != null) {
            Server server = TimoCloudCore.getInstance().getServerManager().getServerByName(serverObject.getName());
            if (server != null) server.onPlayerConnect(event.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerDisconnect(PlayerDisconnectEvent event) {
        ((PlayerObjectBasicImplementation) event.getPlayer()).setOnline(false);
        ProxyObject proxyObject = event.getPlayer().getProxy();
        if (proxyObject != null) {
            Proxy proxy = TimoCloudCore.getInstance().getServerManager().getProxyByName(proxyObject.getName());
            if (proxy != null) proxy.onPlayerDisconnect(event.getPlayer());
        }
        ServerObject serverObject = event.getPlayer().getServer();
        if (serverObject != null) {
            Server server = TimoCloudCore.getInstance().getServerManager().getServerByName(serverObject.getName());
            if (server != null) server.onPlayerDisconnect(event.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerServerChange(PlayerServerChangeEvent event) {
        Server serverFrom = TimoCloudCore.getInstance().getServerManager().getServerByName(event.getServerFrom().getName());
        if (serverFrom != null) serverFrom.onPlayerDisconnect(event.getPlayer());
        Server serverTo = TimoCloudCore.getInstance().getServerManager().getServerByName(event.getServerTo().getName());
        if (serverTo != null) serverTo.onPlayerConnect(event.getPlayer());
    }

}
