package cloud.timo.TimoCloud.bungeecord.sockets;

import cloud.timo.TimoCloud.bungeecord.TimoCloudBungee;
import cloud.timo.TimoCloud.common.sockets.PacketLengthPrepender;
import cloud.timo.TimoCloud.common.sockets.PacketLengthSplitter;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;

public class BungeePipeline extends ChannelInitializer<Channel> {

    @Override
    protected void initChannel(Channel ch) {
        ch.pipeline().addLast("splitter", new PacketLengthSplitter())
                .addLast(TimoCloudBungee.getInstance().getSocketClientHandler())
                .addLast("prepender", new PacketLengthPrepender());
    }

}