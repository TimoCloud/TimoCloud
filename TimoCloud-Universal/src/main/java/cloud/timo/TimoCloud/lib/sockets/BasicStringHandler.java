package cloud.timo.TimoCloud.lib.sockets;

import cloud.timo.TimoCloud.lib.global.logging.TimoCloudLogger;
import cloud.timo.TimoCloud.lib.protocol.Message;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public abstract class BasicStringHandler extends SimpleChannelInboundHandler<String> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String message) throws Exception {
        Channel channel = ctx.channel();
        try {
            handleMessage(Message.createFromJsonString(message), message, channel);
        } catch (Throwable e) {
            TimoCloudLogger.getLogger().severe("Error while parsing JSON message: " + message);
            TimoCloudLogger.getLogger().severe(e);
        }
    }

    public abstract void handleMessage(Message message, String originalMessage, Channel channel);

    public void closeChannel(Channel channel) {
        channel.close();
    }

}
