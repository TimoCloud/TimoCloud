package cloud.timo.TimoCloud.velocity.sockets;

import cloud.timo.TimoCloud.velocity.TimoCloudVelocity;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class VelocitySocketClientHandler extends ChannelInboundHandlerAdapter {

    private Channel channel;
    private String queue;

    public VelocitySocketClientHandler() {
        resetQueue();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        TimoCloudVelocity.getInstance().info("&6Successfully connected to velocity socket!");
        this.channel = ctx.channel();
        TimoCloudVelocity.getInstance().onSocketConnect(ctx.channel());
        flush();
    }

    public void resetQueue() {
        queue = "";
    }

    public void flush() {
        if (channel == null) {
            return;
        }
        channel.writeAndFlush(queue);
        resetQueue();
    }

    public void sendMessage(String message) {
        if (channel == null) {
            queue += message;
        } else {
            channel.writeAndFlush(message);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        //cause.printStackTrace();
        ctx.close();
        TimoCloudVelocity.getInstance().onSocketDisconnect();
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }
}
