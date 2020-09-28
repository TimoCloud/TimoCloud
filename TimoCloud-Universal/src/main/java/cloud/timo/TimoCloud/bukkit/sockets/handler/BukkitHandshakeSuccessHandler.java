package cloud.timo.TimoCloud.bukkit.sockets.handler;

import cloud.timo.TimoCloud.bukkit.TimoCloudBukkit;
import cloud.timo.TimoCloud.common.protocol.Message;
import cloud.timo.TimoCloud.common.protocol.MessageType;
import cloud.timo.TimoCloud.common.sockets.MessageHandler;
import io.netty.channel.Channel;

public class BukkitHandshakeSuccessHandler extends MessageHandler {
    public BukkitHandshakeSuccessHandler() {
        super(MessageType.SERVER_HANDSHAKE_SUCCESS);
    }

    @Override
    public void execute(Message message, Channel channel) {
        TimoCloudBukkit.getInstance().onHandshakeSuccess();
    }
}
