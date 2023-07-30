package cloud.timo.TimoCloud.base.sockets.handler;

import cloud.timo.TimoCloud.base.TimoCloudBase;
import cloud.timo.TimoCloud.common.protocol.Message;
import cloud.timo.TimoCloud.common.protocol.MessageType;
import cloud.timo.TimoCloud.common.sockets.handler.MessageHandler;
import io.netty.channel.Channel;

public class BaseProxyStoppedHandler extends MessageHandler {
    public BaseProxyStoppedHandler() {
        super(MessageType.BASE_PROXY_STOPPED);
    }

    @Override
    public void execute(Message message, Channel channel) {
        TimoCloudBase.getInstance().getInstanceManager().onProxyStopped((String) message.getData());
    }
}
