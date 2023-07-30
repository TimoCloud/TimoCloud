package cloud.timo.TimoCloud.bukkit.sockets.handler;

import cloud.timo.TimoCloud.bukkit.TimoCloudBukkit;
import cloud.timo.TimoCloud.common.protocol.Message;
import cloud.timo.TimoCloud.common.protocol.MessageType;
import cloud.timo.TimoCloud.common.sockets.handler.MessageHandler;
import io.netty.channel.Channel;

public class ServerHandShakeSuccessHandler extends MessageHandler {
    public ServerHandShakeSuccessHandler() {
        super(MessageType.SERVER_HANDSHAKE_SUCCESS);
    }

    @Override
    public void execute(Message message, Channel channel) {
        TimoCloudBukkit.getInstance().onHandshakeSuccess();
    }
}
