package cloud.timo.TimoCloud.velocity.sockets;

import cloud.timo.TimoCloud.common.utils.network.NettyUtil;
import cloud.timo.TimoCloud.velocity.TimoCloudVelocity;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;

public class VelocitySocketClient {
    public void init(String host, int port) throws Exception {
        EventLoopGroup group = NettyUtil.getEventLoopGroup();
        Bootstrap b = new Bootstrap();
        b.group(group)
                .channel(NettyUtil.getSocketChannelClass())
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new VelocityPipeline());
        ChannelFuture f = null;
        try {
            f = b.connect(host, port).sync();
        } catch (Exception e) {
            TimoCloudVelocity.getInstance().onSocketDisconnect();
            group.shutdownGracefully();
            return;
        }
        f.channel().closeFuture().addListener(future -> {
            TimoCloudVelocity.getInstance().onSocketDisconnect();
            group.shutdownGracefully();
        });
    }
}
