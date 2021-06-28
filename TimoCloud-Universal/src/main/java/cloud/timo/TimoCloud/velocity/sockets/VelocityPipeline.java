package cloud.timo.TimoCloud.velocity.sockets;

import cloud.timo.TimoCloud.common.sockets.PacketLengthPrepender;
import cloud.timo.TimoCloud.common.sockets.PacketLengthSplitter;
import cloud.timo.TimoCloud.velocity.TimoCloudVelocity;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;

public class VelocityPipeline extends ChannelInitializer<Channel> {

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ch.pipeline().addLast("splitter", new PacketLengthSplitter());
        ch.pipeline().addLast(TimoCloudVelocity.getInstance().getSocketClientHandler());
        ch.pipeline().addLast("prepender", new PacketLengthPrepender());
    }

}