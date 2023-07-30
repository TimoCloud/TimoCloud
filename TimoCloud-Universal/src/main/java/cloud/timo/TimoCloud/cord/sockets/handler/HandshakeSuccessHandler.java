package cloud.timo.TimoCloud.cord.sockets.handler;

import cloud.timo.TimoCloud.common.protocol.Message;
import cloud.timo.TimoCloud.common.protocol.MessageType;
import cloud.timo.TimoCloud.common.sockets.handler.MessageHandler;
import cloud.timo.TimoCloud.cord.TimoCloudCord;
import io.netty.channel.Channel;

public class HandshakeSuccessHandler extends MessageHandler {
    public HandshakeSuccessHandler() {
        super(MessageType.CORD_HANDSHAKE_SUCCESS);
    }

    @Override
    public void execute(Message message, Channel channel) {
        TimoCloudCord.getInstance().onHandshakeSuccess();
    }
}
