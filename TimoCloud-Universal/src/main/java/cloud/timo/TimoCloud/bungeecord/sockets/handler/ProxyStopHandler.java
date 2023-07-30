package cloud.timo.TimoCloud.bungeecord.sockets.handler;

import cloud.timo.TimoCloud.bungeecord.TimoCloudBungee;
import cloud.timo.TimoCloud.common.protocol.Message;
import cloud.timo.TimoCloud.common.protocol.MessageType;
import cloud.timo.TimoCloud.common.sockets.handler.MessageHandler;
import io.netty.channel.Channel;

public class ProxyStopHandler extends MessageHandler {
    public ProxyStopHandler() {
        super(MessageType.PROXY_STOP);
    }

    @Override
    public void execute(Message message, Channel channel) {
        TimoCloudBungee.getInstance().stop();
    }
}
