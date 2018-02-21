package cloud.timo.TimoCloud.cord.sockets;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.events.EventType;
import cloud.timo.TimoCloud.api.implementations.EventManager;
import cloud.timo.TimoCloud.api.utils.EventUtil;
import cloud.timo.TimoCloud.cord.TimoCloudCord;
import cloud.timo.TimoCloud.cord.api.TimoCloudUniversalAPICordImplementation;
import cloud.timo.TimoCloud.lib.implementations.TimoCloudUniversalAPIBasicImplementation;
import cloud.timo.TimoCloud.lib.sockets.BasicStringHandler;
import cloud.timo.TimoCloud.lib.utils.EnumUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import org.json.simple.JSONObject;

@ChannelHandler.Sharable
public class CordStringHandler extends BasicStringHandler {

    @Override
    public void handleJSON(JSONObject json, String message, Channel channel) {
        String type = (String) json.get("type");
        Object data = json.get("data");
        switch (type) {
            case "HANDSHAKE_SUCCESS":
                TimoCloudCord.getInstance().onHandshakeSuccess();
                break;
            case "API_DATA": {
                ((TimoCloudUniversalAPICordImplementation) TimoCloudAPI.getUniversalInstance()).setData((JSONObject) data);
                break;
            }
            case "EVENT_FIRED":
                try {
                    EventType eventType = EnumUtil.valueOf(EventType.class, (String) json.get("eventType"));
                    ((EventManager) TimoCloudAPI.getEventImplementation()).callEvent(((TimoCloudUniversalAPIBasicImplementation) TimoCloudAPI.getUniversalInstance()).getObjectMapper().readValue((String) data, EventUtil.getClassByEventType(eventType)));
                } catch (Exception e) {
                    System.err.println("Error while parsing event from json: ");
                    e.printStackTrace();
                }
                break;
            default:
                TimoCloudCord.getInstance().severe("Could not categorize json message: " + message);
        }
    }
}
