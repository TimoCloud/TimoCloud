package at.TimoCraft.TimoCloud.base.sockets;

import at.TimoCraft.TimoCloud.base.Base;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class BaseSocketClient {
    public void init(String host, int port) throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new BasePipeline());

            // Start the client.
            ChannelFuture f = null;
            try {
                f = b.connect(host, port).sync();
            } catch (Exception e) {
                Base.getInstance().onSocketDisconnect();
            }
            // Wait until the connection is closed.
            try {
                f.channel().closeFuture().sync();
            } catch (Exception e) {
                f.channel().close();
                Base.getInstance().onSocketDisconnect();
            }
        } finally {
            // Shut down the event loop to terminate all threads.
            group.shutdownGracefully();
            Base.getInstance().onSocketDisconnect();
        }
    }
}
