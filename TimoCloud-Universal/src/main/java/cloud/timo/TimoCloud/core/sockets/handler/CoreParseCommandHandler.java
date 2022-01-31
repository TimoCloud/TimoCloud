package cloud.timo.TimoCloud.core.sockets.handler;

import cloud.timo.TimoCloud.api.core.commands.CommandSender;
import cloud.timo.TimoCloud.common.protocol.Message;
import cloud.timo.TimoCloud.common.protocol.MessageType;
import cloud.timo.TimoCloud.common.sockets.handler.MessageHandler;
import cloud.timo.TimoCloud.core.TimoCloudCore;
import io.netty.channel.Channel;

public class CoreParseCommandHandler extends MessageHandler {
    public CoreParseCommandHandler() {
        super(MessageType.CORE_PARSE_COMMAND);
    }

    @Override
    public void execute(Message message, Channel channel) {
        TimoCloudCore.getInstance().getCommandManager().onCommand((String) message.getData(), new CommandSender() {
            @Override
            public void sendMessage(String msg) {
                TimoCloudCore.getInstance().getSocketServerHandler().sendMessage(channel, Message.create()
                        .setType(MessageType.CORE_SEND_MESSAGE_TO_COMMAND_SENDER)
                        .set("sender", message.get("sender"))
                        .setData(msg));
            }

            @Override
            public void sendError(String message) {
                sendMessage("&c" + message);
            }
        });
    }
}
