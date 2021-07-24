package cloud.timo.TimoCloud.core.sockets;

import cloud.timo.TimoCloud.common.sockets.PacketLengthPrepender;
import cloud.timo.TimoCloud.common.sockets.PacketLengthSplitter;
import cloud.timo.TimoCloud.core.TimoCloudCore;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;

public class CorePipeline extends ChannelInitializer<Channel> {

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ch.pipeline().addLast("splitter", new PacketLengthSplitter())
                .addLast(TimoCloudCore.getInstance().getSocketServerHandler())
                .addLast("rsaHandshakeHandler", new CoreRSAHandshakeHandler())
                .addLast("prepender", new PacketLengthPrepender());
    }

}