package cloud.timo.TimoCloud.cord.sockets;

import cloud.timo.TimoCloud.common.sockets.PacketLengthPrepender;
import cloud.timo.TimoCloud.common.sockets.PacketLengthSplitter;
import cloud.timo.TimoCloud.cord.TimoCloudCord;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

public class CordPipeline extends ChannelInitializer<Channel> {

    @Override
    protected void initChannel(Channel ch) {
        ch.pipeline().addLast(TimoCloudCord.getInstance().getSocketClientHandler())
                .addLast("prepender", new PacketLengthPrepender())
                .addLast("splitter", new PacketLengthSplitter())
                .addLast("decoder", new StringDecoder(CharsetUtil.UTF_8))
                .addLast("encoder", new StringEncoder(CharsetUtil.UTF_8))
                .addLast("handler", TimoCloudCord.getInstance().getStringHandler());
    }

}