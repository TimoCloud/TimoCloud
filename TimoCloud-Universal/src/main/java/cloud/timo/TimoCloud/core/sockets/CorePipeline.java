package cloud.timo.TimoCloud.core.sockets;

import cloud.timo.TimoCloud.core.TimoCloudCore;
import cloud.timo.TimoCloud.lib.sockets.PacketLengthPrepender;
import cloud.timo.TimoCloud.lib.sockets.PacketLengthSplitter;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;

public class CorePipeline extends ChannelInitializer<Channel> {

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ch.pipeline().addLast("splitter", new PacketLengthSplitter());
        ch.pipeline().addLast(TimoCloudCore.getInstance().getSocketServerHandler());
        ch.pipeline().addLast("rsaHandshakeHandler", new CoreRSAHandshakeHandler());
        ch.pipeline().addLast("prepender", new PacketLengthPrepender());
    }

}