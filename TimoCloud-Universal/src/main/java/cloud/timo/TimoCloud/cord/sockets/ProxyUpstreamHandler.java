package cloud.timo.TimoCloud.cord.sockets;

import cloud.timo.TimoCloud.cord.TimoCloudCord;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ProxyUpstreamHandler extends SimpleChannelInboundHandler<ByteBuf> {

    private Channel channel;
    private ProxyDownstreamHandler downstreamHandler;

    public ProxyUpstreamHandler(Channel channel, ProxyDownstreamHandler downstreamHandler) {
        this.channel = channel;
        this.downstreamHandler = downstreamHandler;
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, ByteBuf buf) throws Exception {
        getChannel().writeAndFlush(buf.retain());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        getChannel().close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //TimoCloudCord.getInstance().severe("Exception in UpStreamHandler");
        //cause.printStackTrace();
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public ProxyDownstreamHandler getDownstreamHandler() {
        return downstreamHandler;
    }
}
