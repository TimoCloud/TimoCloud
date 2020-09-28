package cloud.timo.TimoCloud.bungeecord.sockets.handler;

import cloud.timo.TimoCloud.bungeecord.TimoCloudBungee;
import cloud.timo.TimoCloud.common.protocol.Message;
import cloud.timo.TimoCloud.common.protocol.MessageType;
import cloud.timo.TimoCloud.common.sockets.MessageHandler;
import io.netty.channel.Channel;

public class ProxyHandshakeSuccessHandler extends MessageHandler {
    public ProxyHandshakeSuccessHandler() {
        super(MessageType.PROXY_HANDSHAKE_SUCCESS);
    }

    @Override
    public void execute(Message message, Channel channel) {
        TimoCloudBungee.getInstance().onHandshakeSuccess();
    }
}
