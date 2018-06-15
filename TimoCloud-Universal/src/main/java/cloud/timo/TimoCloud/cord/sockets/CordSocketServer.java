package cloud.timo.TimoCloud.cord.sockets;

import cloud.timo.TimoCloud.cord.TimoCloudCord;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class CordSocketServer {

    public void init(String address, int port) throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = TimoCloudCord.getInstance().getWorkerGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b
                    .childHandler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel channel) {
                            channel.pipeline().addLast("minecraftdecoder", TimoCloudCord.getInstance().getMinecraftDecoder());
                        }
                    })
                    .group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class);
            // Start the server.
            ChannelFuture f = b.bind(address, port).sync();
            TimoCloudCord.getInstance().setChannel(f.channel());
            TimoCloudCord.getInstance().info("Successfully started socket server on " + address + ":" + port + "!");
            // Wait until the server socket is closed.
            try {
                f.channel().closeFuture().sync();
            } catch (Exception e) {
                TimoCloudCord.getInstance().info("Socketserver closed.");
            }
        } finally {
            // Shut down all event loops to terminate all threads.
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}