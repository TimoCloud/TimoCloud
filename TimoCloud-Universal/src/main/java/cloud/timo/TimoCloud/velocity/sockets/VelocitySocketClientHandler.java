package cloud.timo.TimoCloud.velocity.sockets;

import cloud.timo.TimoCloud.common.sockets.BasicSocketClientHandler;
import cloud.timo.TimoCloud.velocity.TimoCloudVelocity;
import io.netty.channel.ChannelHandlerContext;

public class VelocitySocketClientHandler extends BasicSocketClientHandler {

    public VelocitySocketClientHandler() {
        super();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        TimoCloudVelocity.getInstance().info("&6Successfully connected to velocity socket!");
        setChannel(ctx.channel());
        TimoCloudVelocity.getInstance().onSocketConnect(ctx.channel());
        flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        //cause.printStackTrace();
        ctx.close();
        TimoCloudVelocity.getInstance().onSocketDisconnect();
    }

}
