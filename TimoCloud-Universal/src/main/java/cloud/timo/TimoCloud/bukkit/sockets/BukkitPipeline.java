package cloud.timo.TimoCloud.bukkit.sockets;

import cloud.timo.TimoCloud.bukkit.TimoCloudBukkit;
import cloud.timo.TimoCloud.common.sockets.PacketLengthPrepender;
import cloud.timo.TimoCloud.common.sockets.PacketLengthSplitter;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;

public class BukkitPipeline extends ChannelInitializer<Channel> {

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ch.pipeline().addLast("splitter", new PacketLengthSplitter());
        ch.pipeline().addLast(TimoCloudBukkit.getInstance().getSocketClientHandler());
        ch.pipeline().addLast("prepender", new PacketLengthPrepender());
    }

}