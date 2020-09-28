package cloud.timo.TimoCloud.bungeecord.sockets.handler;

import cloud.timo.TimoCloud.bungeecord.TimoCloudBungee;
import cloud.timo.TimoCloud.common.protocol.Message;
import cloud.timo.TimoCloud.common.protocol.MessageType;
import cloud.timo.TimoCloud.common.sockets.MessageHandler;
import io.netty.channel.Channel;

public class ProxyExecuteCommandHandler extends MessageHandler {
    public ProxyExecuteCommandHandler() {
        super(MessageType.PROXY_EXECUTE_COMMAND);
    }

    @Override
    public void execute(Message message, Channel channel) {
        TimoCloudBungee.getInstance().getProxy().getPluginManager().dispatchCommand(TimoCloudBungee.getInstance().getProxy().getConsole(), (String) message.getData());
    }
}
