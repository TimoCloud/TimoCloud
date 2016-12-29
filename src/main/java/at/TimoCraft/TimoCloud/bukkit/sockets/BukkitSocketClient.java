package at.TimoCraft.TimoCloud.bukkit.sockets;

/**
 * Created by Timo on 28.12.16.
 */

import at.TimoCraft.TimoCloud.bukkit.Main;
import at.TimoCraft.TimoCloud.bungeecord.TimoCloud;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class BukkitSocketClient {
    public void init(String host, int port) throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();
                            BukkitSocketClientHandler handler = new BukkitSocketClientHandler();
                            p.addLast(handler);
                            Main.getInstance().setSocketClientHandler(handler);
                        }
                    });

            // Start the client.
            ChannelFuture f = b.connect(host, port).sync();
            // Wait until the connection is closed.
            try {
                f.channel().closeFuture().sync();
            } catch (Exception e) {
                TimoCloud.info("Socketserver closed.");
            }
            } finally {
            // Shut down the event loop to terminate all threads.
            group.shutdownGracefully();
            Main.getInstance().onSocketDisconnect();
        }
    }
}
