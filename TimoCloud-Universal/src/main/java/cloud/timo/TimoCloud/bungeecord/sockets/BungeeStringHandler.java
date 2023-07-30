package cloud.timo.TimoCloud.bungeecord.sockets;

import cloud.timo.TimoCloud.bungeecord.TimoCloudBungee;
import cloud.timo.TimoCloud.bungeecord.sockets.handler.*;
import cloud.timo.TimoCloud.common.protocol.Message;
import cloud.timo.TimoCloud.common.protocol.MessageType;
import cloud.timo.TimoCloud.common.sockets.BasicStringHandler;
import io.netty.channel.Channel;

public class BungeeStringHandler extends BasicStringHandler {

    @Override
    public void registerHandlers() {
        addHandler(new APIDataHandler());
        addHandler(new CordSetIPHandler());
        addHandler(new CoreSendMessageToCommandSenderHandler());
        addHandler(new EventFiredHandler());
        addHandler(new OnPluginMessageHandler());
        addHandler(new ProxyAddServerHandler());
        addHandler(new ProxyExecuteCommandHandler());
        addHandler(new ProxyHandshakeSuccessHandler());
        addHandler(new ProxyRemoveServerHandler());
        addHandler(new ProxySendMessageHandler());
        addHandler(new ProxySendPlayerHandler());
        addHandler(new ProxyStopHandler());
    }

    @Override
    public void handleMessage(Message message, String originalMessage, Channel channel) {
        if (message == null) {
            TimoCloudBungee.getInstance().severe("Error while parsing json (json is null): " + originalMessage);
            return;
        }
        if (!handleMessageInternal(message, originalMessage, channel)) {
            TimoCloudBungee.getInstance().severe("Could not categorize json message: " + message);
        }
    }


}
