package cloud.timo.TimoCloud.core.sockets.handler;


import cloud.timo.TimoCloud.common.protocol.Message;
import cloud.timo.TimoCloud.common.protocol.MessageType;
import cloud.timo.TimoCloud.common.sockets.handler.MessageHandler;
import cloud.timo.TimoCloud.core.sockets.Communicatable;
import io.netty.channel.Channel;

import java.net.InetAddress;

public abstract class CoreMessageHandler extends MessageHandler {
    public CoreMessageHandler(MessageType messageType) {
        super(messageType);
    }

    public abstract void execute(Message message, Communicatable target, InetAddress address, Channel channel);

    @Override
    @Deprecated
    public void execute(Message message, Channel channel) {
        //Not Usable
    }
}