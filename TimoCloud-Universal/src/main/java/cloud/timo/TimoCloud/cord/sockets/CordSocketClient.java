package cloud.timo.TimoCloud.cord.sockets;

import cloud.timo.TimoCloud.common.utils.network.NettyUtil;
import cloud.timo.TimoCloud.cord.TimoCloudCord;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;

public class CordSocketClient {
    public void init(String host, int port) throws Exception {
        EventLoopGroup group = NettyUtil.getEventLoopGroup();
        Bootstrap b = new Bootstrap();
        b.group(group)
                .channel(NettyUtil.getSocketChannelClass())
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new CordPipeline());

        ChannelFuture f;
        try {
            f = b.connect(host, port).sync();
            f.channel().closeFuture().sync();
        } catch (Exception ignored) {

        } finally {
            group.shutdownGracefully();
            TimoCloudCord.getInstance().onSocketDisconnect();
        }
    }
}
