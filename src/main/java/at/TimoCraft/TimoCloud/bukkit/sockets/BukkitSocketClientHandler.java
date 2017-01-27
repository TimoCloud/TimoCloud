package at.TimoCraft.TimoCloud.bukkit.sockets;

import at.TimoCraft.TimoCloud.bukkit.Main;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Timo on 28.12.16.
 */
public class BukkitSocketClientHandler extends ChannelInboundHandlerAdapter {

    private Channel channel;
    private List<String> queue;

    public BukkitSocketClientHandler() {
        resetQueue();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        Main.log("Successfully connected to bungee socket!");
        this.channel = ctx.channel();
        Main.getInstance().getBukkitSocketMessageManager().sendMessage("HANDSHAKE", "I_JUST_CAME_ONLINE");
        Main.getInstance().getBukkitSocketMessageManager().sendMessage("SETSTATE", "ONLINE");
    }

    public void resetQueue() {
        queue = new ArrayList<>();
    }

    public void sendMessage(String message) {
        queue.add(message);
    }

    public void flush() {
        if (channel == null) {
            return;
        }
        ArrayList<String> q = (ArrayList<String>) ((ArrayList<String>)queue).clone();
        for (String message : q) {
            channel.writeAndFlush(message); //Unpooled.copiedBuffer(message, CharsetUtil.UTF_8)
        }
        queue.removeAll(q);
        if (queue.size() > 0) {
            flush();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
        Main.getInstance().onSocketDisconnect();
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }
}
