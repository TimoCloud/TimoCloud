package at.TimoCraft.TimoCloud.bungeecord.sockets;

import at.TimoCraft.TimoCloud.bungeecord.TimoCloud;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

public class BungeePipeline extends ChannelInitializer<Channel> {

    @Override
    protected void initChannel(Channel ch) throws Exception {

        ch.pipeline().addLast("decoder", new StringDecoder(CharsetUtil.UTF_8));
        ch.pipeline().addLast("encoder", new StringEncoder(CharsetUtil.UTF_8));
        ch.pipeline().addLast("handler", new BungeeStringHandler());
        ch.pipeline().addLast(TimoCloud.getInstance().getSocketServerHandler());
    }

}