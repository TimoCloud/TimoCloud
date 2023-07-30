package cloud.timo.TimoCloud.cord.sockets;

import cloud.timo.TimoCloud.cord.TimoCloudCord;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

@ChannelHandler.Sharable
public class CordSocketClientHandler extends ChannelInboundHandlerAdapter {

    private Channel channel;

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        setChannel(ctx.channel());
        TimoCloudCord.getInstance().onSocketConnect(ctx.channel());

    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
        TimoCloudCord.getInstance().onSocketDisconnect();
    }

    public void sendMessage(String message) {
        if (channel != null && channel.isActive()) channel.writeAndFlush(message);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        //cause.printStackTrace();
        ctx.close();
        TimoCloudCord.getInstance().onSocketDisconnect();
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }
}
