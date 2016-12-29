package at.TimoCraft.TimoCloud.bungeecord.sockets;

/**
 * Created by Timo on 28.12.16.
 */

import at.TimoCraft.TimoCloud.bungeecord.TimoCloud;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class BungeeSocketServer {

    public void init(String address, int port) throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b
                    .childHandler(new Pipeline())
                    .group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 100)
                    .option(ChannelOption.MAX_MESSAGES_PER_READ, 1)
                    .option(ChannelOption.SO_RCVBUF, 4096)
                    .option(ChannelOption.RCVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(4096));

            // Start the server.
            ChannelFuture f = b.bind(address, port).sync();
            TimoCloud.info("Successfully started socket server on " + address + ":" + port + "!");
            // Wait until the server socket is closed.
            try {
                f.channel().closeFuture().sync();
            } catch (Exception e) {
                TimoCloud.info("Socketserver closed.");
            }
        } finally {
            // Shut down all event loops to terminate all threads.
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}