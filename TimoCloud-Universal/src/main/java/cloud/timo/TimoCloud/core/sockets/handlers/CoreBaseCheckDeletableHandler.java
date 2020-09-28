package cloud.timo.TimoCloud.core.sockets.handlers;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.bukkit.api.TimoCloudUniversalAPIBukkitImplementation;
import cloud.timo.TimoCloud.common.protocol.Message;
import cloud.timo.TimoCloud.common.protocol.MessageType;
import cloud.timo.TimoCloud.common.sockets.MessageHandler;
import cloud.timo.TimoCloud.core.TimoCloudCore;
import cloud.timo.TimoCloud.core.objects.Base;
import cloud.timo.TimoCloud.core.sockets.Communicatable;
import io.netty.channel.Channel;

import java.util.Map;

public class CoreBaseCheckDeletableHandler extends MessageHandler {
    public CoreBaseCheckDeletableHandler() {
        super(MessageType.BASE_CHECK_IF_DELETABLE);
    }

    @Override
    public void execute(Message message, Channel channel) {
        String baseName = (String) message.get("base");
        Communicatable target = null;
        if (baseName != null) target = TimoCloudCore.getInstance().getInstanceManager().getBaseByIdentifier(baseName);

        if (target == null || target instanceof Base) {
            TimoCloudCore.getInstance().getSocketServerHandler().sendMessage(channel, Message.create().setType(MessageType.BASE_DELETE_DIRECTORY).setData(message.getData()));
        }
    }
}
