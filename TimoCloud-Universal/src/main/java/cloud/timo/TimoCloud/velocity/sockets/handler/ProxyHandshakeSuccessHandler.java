package cloud.timo.TimoCloud.velocity.sockets.handler;

import cloud.timo.TimoCloud.common.protocol.Message;
import cloud.timo.TimoCloud.common.protocol.MessageType;
import cloud.timo.TimoCloud.common.sockets.handler.MessageHandler;
import cloud.timo.TimoCloud.velocity.TimoCloudVelocity;
import io.netty.channel.Channel;

public class ProxyHandshakeSuccessHandler extends MessageHandler {
    public ProxyHandshakeSuccessHandler() {
        super(MessageType.PROXY_HANDSHAKE_SUCCESS);
    }

    @Override
    public void execute(Message message, Channel channel) {
        TimoCloudVelocity.getInstance().onHandshakeSuccess();
    }
}
