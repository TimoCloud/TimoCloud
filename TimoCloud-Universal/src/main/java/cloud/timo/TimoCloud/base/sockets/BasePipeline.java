package cloud.timo.TimoCloud.base.sockets;

import cloud.timo.TimoCloud.base.TimoCloudBase;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

public class BasePipeline extends ChannelInitializer<Channel> {

    @Override
    protected void initChannel(Channel ch) {
        ch.pipeline().addLast(TimoCloudBase.getInstance().getSocketClientHandler());
        ch.pipeline().addLast("decoder", new StringDecoder(CharsetUtil.UTF_8));
        ch.pipeline().addLast("encoder", new StringEncoder(CharsetUtil.UTF_8));
        ch.pipeline().addLast("handler", TimoCloudBase.getInstance().getStringHandler());
    }

}