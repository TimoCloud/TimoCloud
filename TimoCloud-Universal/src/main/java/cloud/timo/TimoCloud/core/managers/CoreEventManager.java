package cloud.timo.TimoCloud.core.managers;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.events.*;
import cloud.timo.TimoCloud.api.implementations.EventManager;
import cloud.timo.TimoCloud.api.implementations.PlayerObjectBasicImplementation;
import cloud.timo.TimoCloud.api.objects.Event;
import cloud.timo.TimoCloud.api.objects.ServerObject;
import cloud.timo.TimoCloud.core.TimoCloudCore;
import cloud.timo.TimoCloud.core.objects.Proxy;
import cloud.timo.TimoCloud.core.objects.Server;
import cloud.timo.TimoCloud.core.sockets.Communicatable;
import cloud.timo.TimoCloud.lib.implementations.TimoCloudUniversalAPIBasicImplementation;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CoreEventManager implements Listener {

    public void fireEvent(Event event) {
        Map<String, Object> map = new HashMap<>();
        map.put("type", "EVENT_FIRED");
        map.put("eventType", event.getType().name());
        map.put("data", eventToJSON(event));
        JSONObject json = new JSONObject(map);
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
        Proxy proxy = TimoCloudCore.getInstance().getServerManager().getProxyByName(event.getPlayer().getProxy().getName());
        if (proxy != null) proxy.onPlayerConnect(event.getPlayer());
        Server server = TimoCloudCore.getInstance().getServerManager().getServerByName(event.getPlayer().getServer().getName());
        if (server != null) server.onPlayerConnect(event.getPlayer());
    }

    @EventHandler
    public void onPlayerDisconnect(PlayerDisconnectEvent event) {
        ((PlayerObjectBasicImplementation) event.getPlayer()).setOnline(false);
        Proxy proxy = TimoCloudCore.getInstance().getServerManager().getProxyByName(event.getPlayer().getProxy().getName());
        if (proxy != null) proxy.onPlayerDisconnect(event.getPlayer());
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
