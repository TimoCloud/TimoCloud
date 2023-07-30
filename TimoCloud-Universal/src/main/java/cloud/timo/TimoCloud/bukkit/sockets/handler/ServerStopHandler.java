package cloud.timo.TimoCloud.bukkit.sockets.handler;

import cloud.timo.TimoCloud.bukkit.TimoCloudBukkit;
import cloud.timo.TimoCloud.common.protocol.Message;
import cloud.timo.TimoCloud.common.protocol.MessageType;
import cloud.timo.TimoCloud.common.sockets.handler.MessageHandler;
import io.netty.channel.Channel;

public class ServerStopHandler extends MessageHandler {
    public ServerStopHandler() {
        super(MessageType.SERVER_STOP);
    }

    @Override
    public void execute(Message message, Channel channel) {
        TimoCloudBukkit.getInstance().stop();
    }
}
