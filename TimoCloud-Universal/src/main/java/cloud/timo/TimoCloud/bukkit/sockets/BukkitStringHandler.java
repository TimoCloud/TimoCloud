package cloud.timo.TimoCloud.bukkit.sockets;

import cloud.timo.TimoCloud.bukkit.TimoCloudBukkit;
import cloud.timo.TimoCloud.bukkit.sockets.handler.*;
import cloud.timo.TimoCloud.common.protocol.Message;
import cloud.timo.TimoCloud.common.sockets.BasicStringHandler;
import io.netty.channel.Channel;

public class BukkitStringHandler extends BasicStringHandler {

    @Override
    public void registerHandlers() {
        addHandler(new APIDataHandler());
        addHandler(new EventFiredHandler());
        addHandler(new OnPluginMessageHandler());
        addHandler(new ServerExecuteCommandHandler());
        addHandler(new ServerHandShakeSuccessHandler());
        addHandler(new ServerStopHandler());
    }

    @Override
    public void handleMessage(Message message, String originalMessage, Channel channel) {
        if (message == null) {
            TimoCloudBukkit.getInstance().severe("Error while parsing json (json is null): " + originalMessage);
            return;
        }

        if(!handleMessageInternal(message, originalMessage, channel)) {
            TimoCloudBukkit.getInstance().severe("Error: Could not categorize json message: " + message);
        }
    }
}
