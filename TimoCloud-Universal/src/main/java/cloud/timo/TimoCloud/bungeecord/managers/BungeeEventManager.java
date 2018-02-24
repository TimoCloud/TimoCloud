package cloud.timo.TimoCloud.bungeecord.managers;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.objects.Event;
import cloud.timo.TimoCloud.bungeecord.TimoCloudBungee;
import cloud.timo.TimoCloud.lib.implementations.TimoCloudUniversalAPIBasicImplementation;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class BungeeEventManager {

    public BungeeEventManager() {
    }

    public void sendEvent(Event event) {
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("type", "FIRE_EVENT");
            map.put("eventType", event.getType().name());
            map.put("data", getObjectMapper().writeValueAsString(event));
            TimoCloudBungee.getInstance().getSocketClientHandler().sendMessage(new JSONObject(map).toString());
        } catch (Exception e) {
            TimoCloudBungee.getInstance().severe("Error while sending event: ");
            e.printStackTrace();
        }
    }

    private ObjectMapper getObjectMapper() {
        return ((TimoCloudUniversalAPIBasicImplementation) TimoCloudAPI.getUniversalInstance()).getObjectMapper();
    }
}
