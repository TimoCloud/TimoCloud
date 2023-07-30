package cloud.timo.TimoCloud.core.sockets.handler;


import cloud.timo.TimoCloud.common.protocol.Message;
import cloud.timo.TimoCloud.common.protocol.MessageType;
import cloud.timo.TimoCloud.core.TimoCloudCore;
import cloud.timo.TimoCloud.core.objects.Base;
import cloud.timo.TimoCloud.core.sockets.Communicatable;
import io.netty.channel.Channel;

import java.net.InetAddress;


public class BaseCheckIfDeletableHandler extends CoreMessageHandler {
    public BaseCheckIfDeletableHandler() {
        super(MessageType.BASE_CHECK_IF_DELETABLE);
    }

    @Override
    public void execute(Message message, Communicatable target, InetAddress address, Channel channel) {
        if (target == null || target instanceof Base) {
            TimoCloudCore.getInstance().getSocketServerHandler().sendMessage(channel, Message.create().setType(MessageType.BASE_DELETE_DIRECTORY).setData(message.getData()));
        }
    }
}