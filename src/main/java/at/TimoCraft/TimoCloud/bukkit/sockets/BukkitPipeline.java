package at.TimoCraft.TimoCloud.bukkit.sockets;

import at.TimoCraft.TimoCloud.bukkit.Main;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

/**
 * Created by Timo on 29.12.16.
 */
public class BukkitPipeline extends ChannelInitializer<Channel> {

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ch.pipeline().addLast(Main.getInstance().getSocketClientHandler());
        ch.pipeline().addLast("decoder", new StringDecoder(CharsetUtil.UTF_8));
        ch.pipeline().addLast("encoder", new StringEncoder(CharsetUtil.UTF_8));
        ch.pipeline().addLast("handler", new BukkitStringHandler());
    }

}