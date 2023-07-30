package cloud.timo.TimoCloud.velocity.sockets.handler;

import cloud.timo.TimoCloud.common.protocol.Message;
import cloud.timo.TimoCloud.common.protocol.MessageType;
import cloud.timo.TimoCloud.common.sockets.handler.MessageHandler;
import cloud.timo.TimoCloud.velocity.TimoCloudVelocity;
import io.netty.channel.Channel;

public class ProxyExecuteCommandHandler extends MessageHandler {
    public ProxyExecuteCommandHandler() {
        super(MessageType.PROXY_EXECUTE_COMMAND);
    }

    @Override
    public void execute(Message message, Channel channel) {
        TimoCloudVelocity.getInstance().getServer().getCommandManager().executeImmediatelyAsync(TimoCloudVelocity.getInstance().getServer().getConsoleCommandSource(), (String) message.getData());

    }
}
