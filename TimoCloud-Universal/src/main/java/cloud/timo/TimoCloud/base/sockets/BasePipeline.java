package cloud.timo.TimoCloud.base.sockets;

import cloud.timo.TimoCloud.base.TimoCloudBase;
import cloud.timo.TimoCloud.common.sockets.PacketLengthPrepender;
import cloud.timo.TimoCloud.common.sockets.PacketLengthSplitter;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;

public class BasePipeline extends ChannelInitializer<Channel> {

    @Override
    protected void initChannel(Channel ch) {
        ch.pipeline().addLast("splitter", new PacketLengthSplitter())
                .addLast(TimoCloudBase.getInstance().getSocketClientHandler())
                .addLast("prepender", new PacketLengthPrepender());
    }

}