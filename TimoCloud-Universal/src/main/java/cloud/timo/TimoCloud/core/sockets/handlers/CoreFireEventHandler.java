package cloud.timo.TimoCloud.core.sockets.handlers;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.events.EventType;
import cloud.timo.TimoCloud.api.implementations.TimoCloudUniversalAPIBasicImplementation;
import cloud.timo.TimoCloud.api.utils.EventUtil;
import cloud.timo.TimoCloud.common.protocol.Message;
import cloud.timo.TimoCloud.common.protocol.MessageType;
import cloud.timo.TimoCloud.common.sockets.MessageHandler;
import cloud.timo.TimoCloud.common.utils.EnumUtil;
import cloud.timo.TimoCloud.core.TimoCloudCore;
import io.netty.channel.Channel;

public class CoreFireEventHandler extends MessageHandler {
    public CoreFireEventHandler() {
        super(MessageType.FIRE_EVENT);
    }

    @Override
    public void execute(Message message, Channel channel) {
        try {
            TimoCloudCore.getInstance().getEventManager().fireEvent(
                    ((TimoCloudUniversalAPIBasicImplementation) TimoCloudAPI.getUniversalAPI()).getObjectMapper().readValue(
                            (String) message.getData(), EventUtil.getClassByEventType(
                                    EnumUtil.valueOf(EventType.class, (String) message.get("eT")))));
        } catch (Exception e) {
            TimoCloudCore.getInstance().severe("Error while firing event: ");
            e.printStackTrace();
        }
    }
}
