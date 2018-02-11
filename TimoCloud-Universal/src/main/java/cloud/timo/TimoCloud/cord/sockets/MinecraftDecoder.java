package cloud.timo.TimoCloud.cord.sockets;

import cloud.timo.TimoCloud.api.objects.ProxyGroupObject;
import cloud.timo.TimoCloud.api.objects.ProxyObject;
import cloud.timo.TimoCloud.cord.TimoCloudCord;
import cloud.timo.TimoCloud.cord.objects.ConnectionState;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.LinkedList;

import static cloud.timo.TimoCloud.cord.utils.PacketUtil.*;


@ChannelHandler.Sharable
public class MinecraftDecoder extends ChannelInboundHandlerAdapter {

    public MinecraftDecoder() {
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            final ByteBuf buf = (ByteBuf) msg;
            ByteBuf bufClone = Unpooled.copiedBuffer(buf);
            ConnectionState connectionState = ctx.channel().attr(CONNECTION_STATE).get();
            if (connectionState == null) {
                ctx.channel().attr(CONNECTION_STATE).set(ConnectionState.HANDSHAKE);
                ctx.channel().attr(LOGIN_PACKETS).set(new LinkedList<>());
                ctx.channel().attr(RECONNECTED).set(false);
                final int packetLength = readVarInt(buf);
                final int packetID = readVarInt(buf);
                if (packetID == 0) {
                    final int clientVersion = readVarInt(buf);
                    final String hostName = readString(buf);
                    final int port = buf.readUnsignedShort();
                    final int state = readVarInt(buf);
                    if (state == 2) {
                        ctx.channel().attr(LOGIN_PACKETS).get().add(Unpooled.copiedBuffer(bufClone));
                    }
                    ctx.channel().attr(HOSTNAME).set(hostName);
                    ctx.channel().attr(LOGIN_PACKET).set(Unpooled.copiedBuffer(bufClone));
                    buf.readBytes(buf.readableBytes());
                    connectClient(ctx.channel(), false);
                }
            } else {
                Channel proxiedChannel = ctx.channel().attr(PROXY_CHANNEL).get();
                ByteBuf bufClone2 = Unpooled.copiedBuffer(bufClone);

                try {
                    final int packetLength = readVarInt(bufClone2);
                    final int packetID = readVarInt(bufClone2);
                    if (packetID == 0 || packetID == 1 || packetID == 2) {
                        ctx.channel().attr(LOGIN_PACKETS).get().add(Unpooled.copiedBuffer(bufClone2));
                    }
                } catch (Exception e) {}

                byte[] bytes = new byte[buf.readableBytes()];
                buf.readBytes(bytes);
                proxiedChannel.writeAndFlush(Unpooled.buffer().writeBytes(bytes));
            }
        } catch (Exception e) {
            TimoCloudCord.getInstance().severe("Error while handling incoming connection: ");
            e.printStackTrace();
        }
    }

    public static void connectClient(Channel channel, boolean reconnect) {
        String hostName = channel.attr(HOSTNAME).get();

        ProxyGroupObject proxyGroupObject = TimoCloudCord.getInstance().getProxyManager().getProxyGroupByHostName(hostName);
        if (proxyGroupObject == null) {
            TimoCloudCord.getInstance().severe("Error: No proxy group found for hostname '" + hostName + "'");
            channel.close();
            return;
        }
        connectClient(channel, reconnect, proxyGroupObject);
    }

    public static void connectClient(Channel channel, boolean reconnect, ProxyGroupObject proxyGroupObject) {
        ProxyObject proxyObject = TimoCloudCord.getInstance().getProxyManager().getFreeProxy(proxyGroupObject);
        if (proxyObject == null) {
            TimoCloudCord.getInstance().severe("No free proxy of group '" + proxyGroupObject.getName() + "' found. Disconnecting client.");
            channel.close();
            return;
        }
        connectClient(channel, reconnect, proxyObject);
    }

    public static void connectClient(Channel channel, boolean reconnect, ProxyObject proxyObject) {
        ProxyDownstreamHandler downstreamHandler = channel.attr(DOWNSTREAM_HANDLER).get() == null ? new ProxyDownstreamHandler(channel) : channel.attr(DOWNSTREAM_HANDLER).get();
        channel.attr(DOWNSTREAM_HANDLER).set(downstreamHandler);
        channel.attr(RECONNECTED).set(reconnect);
        Bootstrap b = new Bootstrap();
        b
                .group(TimoCloudCord.getInstance().getWorkerGroup())
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 1000)
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    public void initChannel(Channel ch) throws Exception {
                        ch.pipeline().addLast(downstreamHandler);
                    }
                });


        final ChannelFuture cf = b.connect(proxyObject.getSocketAddress());
        cf.addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                TimoCloudCord.getInstance().info("[" + channel.remoteAddress() + "] connected to hostname '" + channel.attr(HOSTNAME).get() + "'. Using proxy " + proxyObject.getName() + " of group " + proxyObject.getGroup().getName() + ".");
                if (channel.attr(UPSTREAM_HANDLER).get() == null) {
                    channel.pipeline().addLast(new ProxyUpstreamHandler(cf.channel(), downstreamHandler));
                } else {
                    channel.attr(UPSTREAM_HANDLER).get().setChannel(cf.channel());
                }
                if (channel.pipeline().get("minecraftdecoder") != null) channel.pipeline().remove("minecraftdecoder");
                if (reconnect) {
                    for (ByteBuf byteBuf : channel.attr(LOGIN_PACKETS).get()) {
                        ByteBuf byteBufClone = Unpooled.copiedBuffer(byteBuf);
                        channel.writeAndFlush(byteBufClone);
                    }
                } else {
                    future.channel().writeAndFlush(Unpooled.copiedBuffer(channel.attr(LOGIN_PACKET).get())); // Send login packet
                }
                channel.attr(CONNECTION_STATE).set(ConnectionState.PROXY);
                channel.attr(PROXY_CHANNEL).set(cf.channel());
            } else {
                channel.close();
                cf.channel().close();
            }
        });
    }

}
