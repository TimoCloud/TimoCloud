package cloud.timo.TimoCloud.cord.sockets;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.events.EventType;
import cloud.timo.TimoCloud.api.implementations.EventManager;
import cloud.timo.TimoCloud.api.implementations.TimoCloudMessageAPIBasicImplementation;
import cloud.timo.TimoCloud.api.implementations.TimoCloudUniversalAPIBasicImplementation;
import cloud.timo.TimoCloud.api.messages.objects.AddressedPluginMessage;
import cloud.timo.TimoCloud.api.utils.EventUtil;
import cloud.timo.TimoCloud.cord.TimoCloudCord;
import cloud.timo.TimoCloud.cord.api.TimoCloudUniversalAPICordImplementation;
import cloud.timo.TimoCloud.lib.messages.Message;
import cloud.timo.TimoCloud.lib.sockets.BasicStringHandler;
import cloud.timo.TimoCloud.lib.utils.EnumUtil;
import cloud.timo.TimoCloud.lib.utils.PluginMessageSerializer;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;

import java.util.Map;

@ChannelHandler.Sharable
public class CordStringHandler extends BasicStringHandler {

    @Override
    public void handleMessage(Message message, String originalMessage, Channel channel) {
        String type = (String) message.get("type");
        Object data = message.get("data");
        switch (type) {
            case "HANDSHAKE_SUCCESS":
                TimoCloudCord.getInstance().onHandshakeSuccess();
                break;
            case "API_DATA": {
                ((TimoCloudUniversalAPICordImplementation) TimoCloudAPI.getUniversalAPI()).setData((Map<String, Object>) data);
                break;
            }
            case "EVENT_FIRED":
                try {
                    EventType eventType = EnumUtil.valueOf(EventType.class, (String) message.get("eventType"));
                    ((EventManager) TimoCloudAPI.getEventAPI()).callEvent(((TimoCloudUniversalAPIBasicImplementation) TimoCloudAPI.getUniversalAPI()).getObjectMapper().readValue((String) data, EventUtil.getClassByEventType(eventType)));
                } catch (Exception e) {
                    System.err.println("Error while parsing event from json: ");
                    TimoCloudCord.getInstance().severe(e);
                }
                break;
            case "PLUGIN_MESSAGE": {
                AddressedPluginMessage addressedPluginMessage = PluginMessageSerializer.deserialize((Map) data);
                ((TimoCloudMessageAPIBasicImplementation) TimoCloudAPI.getMessageAPI()).onMessage(addressedPluginMessage);
                break;
            }
            default:
                TimoCloudCord.getInstance().severe("Could not categorize json message: " + originalMessage);
        }
    }
}
