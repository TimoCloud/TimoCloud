package cloud.timo.TimoCloud.base.sockets;

import cloud.timo.TimoCloud.base.TimoCloudBase;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class BaseSocketClient {
    public void init(String host, int port) throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();
        b.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new BasePipeline());
        ChannelFuture f = null;
        try {
            f = b.connect(host, port).sync();
        } catch (Exception e) {
            TimoCloudBase.getInstance().onSocketDisconnect();
            f.channel().close();
        }
        f.channel().closeFuture().addListener(future -> {
            group.shutdownGracefully();
            TimoCloudBase.getInstance().onSocketDisconnect();
        });
    }
}
