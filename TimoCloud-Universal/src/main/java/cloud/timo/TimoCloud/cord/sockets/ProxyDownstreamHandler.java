package cloud.timo.TimoCloud.cord.sockets;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;

import static cloud.timo.TimoCloud.cord.utils.PacketUtil.RECONNECTED;
import static cloud.timo.TimoCloud.cord.utils.PacketUtil.isCompressed;

public class ProxyDownstreamHandler extends SimpleChannelInboundHandler<ByteBuf> {

    private Channel channel;

    public ProxyDownstreamHandler(Channel channel) {
        this.channel = channel;
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, ByteBuf buf) throws Exception {
        ByteBuf clone = Unpooled.copiedBuffer(buf);
        if (getChannel().attr(RECONNECTED).get() && ! isCompressed(buf)) return;
        getChannel().writeAndFlush(clone);
        buf.release();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (getChannel().isActive()) MinecraftDecoder.connectClient(channel, true);
    }

    public Channel getChannel() {
        return channel;
    }
}
