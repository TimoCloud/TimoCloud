package cloud.timo.TimoCloud.common.events;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.events.Event;
import cloud.timo.TimoCloud.api.implementations.TimoCloudUniversalAPIBasicImplementation;
import cloud.timo.TimoCloud.api.internal.TimoCloudInternalAPI;
import cloud.timo.TimoCloud.common.global.logging.TimoCloudLogger;
import cloud.timo.TimoCloud.common.protocol.Message;
import cloud.timo.TimoCloud.common.protocol.MessageType;
import com.fasterxml.jackson.databind.ObjectMapper;

public class EventTransmitter {

    public static void sendEvent(Event event) {
        try {
            TimoCloudInternalAPI.getInternalMessageAPI().sendMessageToCore(Message.create()
                    .setType(MessageType.FIRE_EVENT)
                    .set("eventType", event.getType().name())
                    .setData(getObjectMapper().writeValueAsString(event))
                    .toString());
        } catch (Exception e) {
            TimoCloudLogger.getLogger().severe("Error while sending event: ");
            TimoCloudLogger.getLogger().severe(e);
        }
    }

    private static ObjectMapper getObjectMapper() {
        return ((TimoCloudUniversalAPIBasicImplementation) TimoCloudAPI.getUniversalAPI()).getObjectMapper();
    }
}
