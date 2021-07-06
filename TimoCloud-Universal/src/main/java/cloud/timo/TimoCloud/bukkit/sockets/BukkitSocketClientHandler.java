package cloud.timo.TimoCloud.bukkit.sockets;

import cloud.timo.TimoCloud.bukkit.TimoCloudBukkit;
import cloud.timo.TimoCloud.common.sockets.BasicSocketClientHandler;
import io.netty.channel.ChannelHandlerContext;

public class BukkitSocketClientHandler extends BasicSocketClientHandler {

    public BukkitSocketClientHandler() {
        super();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        TimoCloudBukkit.getInstance().info("Successfully connected to Core socket!");
        setChannel(ctx.channel());
        TimoCloudBukkit.getInstance().onSocketConnect(ctx.channel());
        flush();
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        //causTimoCloudBukkit.getInstance().severe(e);
        ctx.close();
        TimoCloudBukkit.getInstance().onSocketDisconnect(false);
    }

}
