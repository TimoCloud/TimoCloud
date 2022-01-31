package cloud.timo.TimoCloud.core.sockets.handler;

import cloud.timo.TimoCloud.common.protocol.Message;
import cloud.timo.TimoCloud.common.protocol.MessageType;
import cloud.timo.TimoCloud.core.TimoCloudCore;
import cloud.timo.TimoCloud.core.objects.Proxy;
import cloud.timo.TimoCloud.core.sockets.Communicatable;
import io.netty.channel.Channel;

import java.net.InetAddress;

public class ProxyLogEntryHandler extends CoreMessageHandler{
    public ProxyLogEntryHandler() {
        super(MessageType.PROXY_LOG_ENTRY);
    }

    @Override
    public void execute(Message message, Communicatable target, InetAddress address, Channel channel) {
        if (target instanceof Proxy) {
            target.onMessage(message, TimoCloudCore.getInstance().getSocketServerHandler().getCommunicatable(channel));
        }
    }
}
