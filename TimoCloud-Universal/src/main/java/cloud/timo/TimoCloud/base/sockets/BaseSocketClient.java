package cloud.timo.TimoCloud.base.sockets;

import cloud.timo.TimoCloud.base.TimoCloudBase;
import cloud.timo.TimoCloud.lib.utils.network.NettyUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;

public class BaseSocketClient {
    public void init(String host, int port) throws Exception {
        EventLoopGroup group = NettyUtil.getEventLoopGroup();
        Bootstrap b = new Bootstrap();
        b.group(group)
                .channel(NettyUtil.getSocketChannelClass())
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new BasePipeline());
        ChannelFuture f = null;
        try {
            f = b.connect(host, port).sync();
            f.channel().closeFuture().sync();
        } catch (Exception e) {

        } finally {
            group.shutdownGracefully();
            TimoCloudBase.getInstance().onSocketDisconnect();
        }
    }
}
