package cloud.timo.TimoCloud.bukkit.sockets;

import cloud.timo.TimoCloud.bukkit.TimoCloudBukkit;
import cloud.timo.TimoCloud.common.sockets.PacketLengthPrepender;
import cloud.timo.TimoCloud.common.sockets.PacketLengthSplitter;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;

public class BukkitPipeline extends ChannelInitializer<Channel> {

    @Override
    protected void initChannel(Channel ch) {
        ch.pipeline().addLast("splitter", new PacketLengthSplitter())
                .addLast(TimoCloudBukkit.getInstance().getSocketClientHandler())
                .addLast("prepender", new PacketLengthPrepender());
    }

}