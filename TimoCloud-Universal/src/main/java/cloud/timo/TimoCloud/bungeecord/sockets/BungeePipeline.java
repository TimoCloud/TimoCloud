package cloud.timo.TimoCloud.bungeecord.sockets;

import cloud.timo.TimoCloud.bungeecord.TimoCloudBungee;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

public class BungeePipeline extends ChannelInitializer<Channel> {

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ch.pipeline().addLast(TimoCloudBungee.getInstance().getSocketClientHandler());
        ch.pipeline().addLast("decoder", new StringDecoder(CharsetUtil.UTF_8));
        ch.pipeline().addLast("encoder", new StringEncoder(CharsetUtil.UTF_8));
        ch.pipeline().addLast("handler", TimoCloudBungee.getInstance().getBungeeStringHandler());
    }

}