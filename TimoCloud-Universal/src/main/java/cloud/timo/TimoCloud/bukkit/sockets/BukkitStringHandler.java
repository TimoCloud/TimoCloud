package cloud.timo.TimoCloud.bukkit.sockets;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.events.EventType;
import cloud.timo.TimoCloud.api.implementations.EventManager;
import cloud.timo.TimoCloud.api.utils.EventUtil;
import cloud.timo.TimoCloud.bukkit.TimoCloudBukkit;
import cloud.timo.TimoCloud.bukkit.api.TimoCloudUniversalAPIBukkitImplementation;
import cloud.timo.TimoCloud.lib.implementations.TimoCloudUniversalAPIBasicImplementation;
import cloud.timo.TimoCloud.lib.sockets.BasicStringHandler;
import cloud.timo.TimoCloud.lib.utils.EnumUtil;
import io.netty.channel.Channel;
import org.json.simple.JSONObject;

public class BukkitStringHandler extends BasicStringHandler {

    @Override
    public void handleJSON(JSONObject json, String message, Channel channel) {
        if (json == null) {
            TimoCloudBukkit.getInstance().severe("Error while parsing json (json is null): " + message);
            return;
        }
        String server = (String) json.get("target");
        String type = (String) json.get("type");
        Object data = json.get("data");
        switch (type) {
            case "HANDSHAKE_SUCCESS":
                TimoCloudBukkit.getInstance().onHandshakeSuccess();
                break;
            case "API_DATA":
                ((TimoCloudUniversalAPIBukkitImplementation) TimoCloudAPI.getUniversalInstance()).setData((JSONObject) data);
                break;
            case "EVENT_FIRED":
                try {
                    EventType eventType = EnumUtil.valueOf(EventType.class, (String) json.get("eventType"));
                    ((EventManager) TimoCloudAPI.getEventImplementation()).callEvent(((TimoCloudUniversalAPIBasicImplementation) TimoCloudAPI.getUniversalInstance()).getObjectMapper().readValue((String) data, EventUtil.getClassByEventType(eventType)));
                } catch (Exception e) {
                    System.err.println("Error while parsing event from json: ");
                    e.printStackTrace();
                }
                break;
            case "EXECUTE_COMMAND":
                TimoCloudBukkit.getInstance().getServer().dispatchCommand(TimoCloudBukkit.getInstance().getServer().getConsoleSender(), (String) data);
                break;
            default:
                TimoCloudBukkit.getInstance().severe("Error: Could not categorize json message: " + message);
        }
    }

}
