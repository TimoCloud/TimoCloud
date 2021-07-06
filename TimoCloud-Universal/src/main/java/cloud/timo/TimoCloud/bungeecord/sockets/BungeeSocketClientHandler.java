package cloud.timo.TimoCloud.bungeecord.sockets;

import cloud.timo.TimoCloud.bungeecord.TimoCloudBungee;
import cloud.timo.TimoCloud.common.sockets.BasicSocketClientHandler;
import io.netty.channel.ChannelHandlerContext;

public class BungeeSocketClientHandler extends BasicSocketClientHandler {

    public BungeeSocketClientHandler() {
        super();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        TimoCloudBungee.getInstance().info("&6Successfully connected to bungee socket!");
        setChannel(ctx.channel());
        TimoCloudBungee.getInstance().onSocketConnect(ctx.channel());
        flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        //cause.printStackTrace();
        ctx.close();
        TimoCloudBungee.getInstance().onSocketDisconnect();
    }

}
