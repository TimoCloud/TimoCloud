package cloud.timo.TimoCloud.bungeecord.managers;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.objects.Event;
import cloud.timo.TimoCloud.bungeecord.TimoCloudBungee;
import cloud.timo.TimoCloud.lib.implementations.TimoCloudUniversalAPIBasicImplementation;
import cloud.timo.TimoCloud.lib.objects.JSONBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BungeeEventManager {

    public BungeeEventManager() {
    }

    public void sendEvent(Event event) {
        try {
            TimoCloudBungee.getInstance().getSocketClientHandler().sendMessage(JSONBuilder.create()
                    .setType("FIRE_EVENT")
                    .set("eventType", event.getType().name())
                    .setData(getObjectMapper().writeValueAsString(event))
                    .toString());
        } catch (Exception e) {
            TimoCloudBungee.getInstance().severe("Error while sending event: ");
            e.printStackTrace();
        }
    }

    private ObjectMapper getObjectMapper() {
        return ((TimoCloudUniversalAPIBasicImplementation) TimoCloudAPI.getUniversalInstance()).getObjectMapper();
    }
}
