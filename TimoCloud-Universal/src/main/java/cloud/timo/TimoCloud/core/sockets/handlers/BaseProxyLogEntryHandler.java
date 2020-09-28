package cloud.timo.TimoCloud.core.sockets.handlers;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.bukkit.api.TimoCloudUniversalAPIBukkitImplementation;
import cloud.timo.TimoCloud.common.protocol.Message;
import cloud.timo.TimoCloud.common.protocol.MessageType;
import cloud.timo.TimoCloud.common.sockets.MessageHandler;
import cloud.timo.TimoCloud.core.TimoCloudCore;
import cloud.timo.TimoCloud.core.objects.Proxy;
import cloud.timo.TimoCloud.core.objects.Server;
import cloud.timo.TimoCloud.core.sockets.Communicatable;
import io.netty.channel.Channel;

import java.util.Map;

public class BaseProxyLogEntryHandler extends MessageHandler {
    public BaseProxyLogEntryHandler() {
        super(MessageType.PROXY_LOG_ENTRY);
    }

    @Override
    public void execute(Message message, Channel channel) {
        Communicatable sender = TimoCloudCore.getInstance().getSocketServerHandler().getCommunicatable(channel);
        String targetId = message.getTarget();
        Server proxy = TimoCloudCore.getInstance().getInstanceManager().getServerByIdentifier(targetId);
        Communicatable target = null;
        if (proxy != null) target = proxy;

        if (target instanceof Proxy) {
            target.onMessage(message, sender);
        }    }
}
