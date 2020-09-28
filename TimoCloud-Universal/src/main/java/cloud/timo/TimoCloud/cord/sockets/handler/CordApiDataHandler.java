package cloud.timo.TimoCloud.cord.sockets.handler;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.common.protocol.Message;
import cloud.timo.TimoCloud.common.protocol.MessageType;
import cloud.timo.TimoCloud.common.sockets.MessageHandler;
import cloud.timo.TimoCloud.cord.TimoCloudCord;
import cloud.timo.TimoCloud.cord.api.TimoCloudUniversalAPICordImplementation;
import io.netty.channel.Channel;

import java.util.Map;

public class CordApiDataHandler extends MessageHandler {
    public CordApiDataHandler() {
        super(MessageType.API_DATA);
    }

    @Override
    public void execute(Message message, Channel channel) {
        ((TimoCloudUniversalAPICordImplementation) TimoCloudAPI.getUniversalAPI()).setData((Map<String, Object>) message.getData());
    }
}
