package cloud.timo.TimoCloud.core.sockets.handlers;

import cloud.timo.TimoCloud.common.protocol.Message;
import cloud.timo.TimoCloud.common.protocol.MessageType;
import cloud.timo.TimoCloud.common.sockets.MessageHandler;
import cloud.timo.TimoCloud.core.TimoCloudCore;
import cloud.timo.TimoCloud.core.objects.Server;
import cloud.timo.TimoCloud.core.sockets.Communicatable;
import io.netty.channel.Channel;

public class CoreServerLogEntryHandler extends MessageHandler {
    public CoreServerLogEntryHandler() {
        super(MessageType.SERVER_LOG_ENTRY);
    }

    @Override
    public void execute(Message message, Channel channel) {
        Communicatable sender = TimoCloudCore.getInstance().getSocketServerHandler().getCommunicatable(channel);
        String targetId = message.getTarget();
        Server server = TimoCloudCore.getInstance().getInstanceManager().getServerByIdentifier(targetId);
        Communicatable target = null;
        if (server != null) target = server;

        if (target instanceof Server) {
            target.onMessage(message, sender);
        }
    }
}
