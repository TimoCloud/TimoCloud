package cloud.timo.TimoCloud.core.sockets.handlers;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.core.commands.CommandSender;
import cloud.timo.TimoCloud.bukkit.api.TimoCloudUniversalAPIBukkitImplementation;
import cloud.timo.TimoCloud.common.protocol.Message;
import cloud.timo.TimoCloud.common.protocol.MessageType;
import cloud.timo.TimoCloud.common.sockets.MessageHandler;
import cloud.timo.TimoCloud.core.TimoCloudCore;
import io.netty.channel.Channel;

import java.util.Map;

public class CoreParseCommandHandler extends MessageHandler {
    public CoreParseCommandHandler() {
        super(MessageType.API_DATA);
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
