package at.TimoCraft.TimoCloud.bukkit.sockets;

import at.TimoCraft.TimoCloud.bukkit.TimoCloudBukkit;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Created by Timo on 28.12.16.
 */
public class BukkitSocketClientHandler extends ChannelInboundHandlerAdapter {

    private Channel channel;
    private String queue;

    public BukkitSocketClientHandler() {
        resetQueue();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        TimoCloudBukkit.log("Successfully connected to bungee socket!");
        this.channel = ctx.channel();
        TimoCloudBukkit.getInstance().onSocketConnect();
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
        cause.printStackTrace();
        ctx.close();
        TimoCloudBukkit.getInstance().onSocketDisconnect();
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }
}
