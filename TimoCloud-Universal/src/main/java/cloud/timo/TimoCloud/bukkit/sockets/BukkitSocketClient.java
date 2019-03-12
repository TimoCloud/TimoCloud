package cloud.timo.TimoCloud.bukkit.sockets;

import cloud.timo.TimoCloud.bukkit.TimoCloudBukkit;
import cloud.timo.TimoCloud.common.utils.network.NettyUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;

public class BukkitSocketClient {
    public void init(String host, int port) throws Exception {
        EventLoopGroup group = NettyUtil.getEventLoopGroup();
        Bootstrap b = new Bootstrap();
        b.group(group)
                .channel(NettyUtil.getSocketChannelClass())
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new BukkitPipeline());
        ChannelFuture f = null;
        try {
            f = b.connect(host, port).sync();
        } catch (Exception e) {
            TimoCloudBukkit.getInstance().onSocketDisconnect(true);
            group.shutdownGracefully();
            return;
        }
        f.channel().closeFuture().addListener(future -> {
            TimoCloudBukkit.getInstance().onSocketDisconnect(false);
            group.shutdownGracefully();
        });
    }
}
