package cloud.timo.TimoCloud.bungeecord.sockets;

import cloud.timo.TimoCloud.bungeecord.TimoCloudBungee;
import cloud.timo.TimoCloud.common.sockets.PacketLengthPrepender;
import cloud.timo.TimoCloud.common.sockets.PacketLengthSplitter;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;

public class BungeePipeline extends ChannelInitializer<Channel> {

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ch.pipeline().addLast("splitter", new PacketLengthSplitter());
        ch.pipeline().addLast(TimoCloudBungee.getInstance().getSocketClientHandler());
        ch.pipeline().addLast("prepender", new PacketLengthPrepender());
    }

}