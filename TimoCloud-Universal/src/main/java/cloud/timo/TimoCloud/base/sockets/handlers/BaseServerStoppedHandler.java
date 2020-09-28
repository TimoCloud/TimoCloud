package cloud.timo.TimoCloud.base.sockets.handlers;

import cloud.timo.TimoCloud.base.TimoCloudBase;
import cloud.timo.TimoCloud.common.protocol.Message;
import cloud.timo.TimoCloud.common.protocol.MessageType;
import cloud.timo.TimoCloud.common.sockets.MessageHandler;
import io.netty.channel.Channel;

public class BaseServerStoppedHandler extends MessageHandler {
    public BaseServerStoppedHandler() {
        super(MessageType.BASE_SERVER_STOPPED);
    }

    @Override
    public void execute(Message message, Channel channel) {
        TimoCloudBase.getInstance().getInstanceManager().onServerStopped((String) message.getData());
    }
}
