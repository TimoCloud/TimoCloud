package cloud.timo.TimoCloud.cord.sockets;

import cloud.timo.TimoCloud.common.protocol.Message;
import cloud.timo.TimoCloud.common.sockets.BasicStringHandler;
import cloud.timo.TimoCloud.cord.TimoCloudCord;
import cloud.timo.TimoCloud.cord.sockets.handler.APIDataHandler;
import cloud.timo.TimoCloud.cord.sockets.handler.EventFiredHandler;
import cloud.timo.TimoCloud.cord.sockets.handler.HandShakeSuccessHandler;
import cloud.timo.TimoCloud.cord.sockets.handler.PluginMessageHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;

@ChannelHandler.Sharable
public class CordStringHandler extends BasicStringHandler {

    @Override
    public void registerHandlers() {
        addHandler(new APIDataHandler());
        addHandler(new EventFiredHandler());
        addHandler(new HandShakeSuccessHandler());
        addHandler(new PluginMessageHandler());
    }

    @Override
    public void handleMessage(Message message, String originalMessage, Channel channel) {
        if (!handleMessageInternal(message, originalMessage, channel)) {
            TimoCloudCord.getInstance().severe("Could not categorize json message: " + originalMessage);
        }
    }
}
