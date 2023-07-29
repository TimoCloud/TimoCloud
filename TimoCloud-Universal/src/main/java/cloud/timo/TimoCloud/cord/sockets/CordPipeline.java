package cloud.timo.TimoCloud.cord.sockets;

import cloud.timo.TimoCloud.common.sockets.PacketLengthPrepender;
import cloud.timo.TimoCloud.common.sockets.PacketLengthSplitter;
import cloud.timo.TimoCloud.cord.TimoCloudCord;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;

public class CordPipeline extends ChannelInitializer<Channel> {

    @Override
    protected void initChannel(Channel ch) {
        ch.pipeline().addLast("splitter", new PacketLengthSplitter());
        ch.pipeline().addLast(TimoCloudCord.getInstance().getSocketClientHandler());
        ch.pipeline().addLast("prepender", new PacketLengthPrepender());

    }

}