package cloud.timo.TimoCloud.velocity.sockets.handler;

import cloud.timo.TimoCloud.common.protocol.Message;
import cloud.timo.TimoCloud.common.protocol.MessageType;
import cloud.timo.TimoCloud.common.sockets.handler.MessageHandler;
import cloud.timo.TimoCloud.velocity.TimoCloudVelocity;
import io.netty.channel.Channel;

public class CoreSendMessageToCommandSenderHandler extends MessageHandler {

    public CoreSendMessageToCommandSenderHandler() {
        super(MessageType.CORE_SEND_MESSAGE_TO_COMMAND_SENDER);
    }

    @Override
    public void execute(Message message, Channel channel) {
        TimoCloudVelocity.getInstance().getTimoCloudCommand().sendMessage((String) message.get("sender"), (String) message.getData());

    }
}
