package cloud.timo.TimoCloud.cord.sockets;

import cloud.timo.TimoCloud.api.objects.ProxyGroupObject;
import cloud.timo.TimoCloud.api.objects.ProxyObject;
import cloud.timo.TimoCloud.common.protocol.Message;
import cloud.timo.TimoCloud.common.protocol.MessageType;
import cloud.timo.TimoCloud.cord.TimoCloudCord;
import cloud.timo.TimoCloud.cord.objects.ConnectionState;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.*;
import java.net.InetSocketAddress;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

import static cloud.timo.TimoCloud.cord.utils.PacketUtil.*;


@ChannelHandler.Sharable
public class MinecraftDecoder extends SimpleChannelInboundHandler<ByteBuf> {

    public MinecraftDecoder() {
    }

    @Override
    public void channelRead0(final ChannelHandlerContext ctx, ByteBuf buf) throws Exception {
        try {
            final int packetLength = readVarInt(buf);
            final int packetID = readVarInt(buf);
            if (packetID == 0) {
                final int clientVersion = readVarInt(buf);
                final String hostName = readString(buf);
                final int port = buf.readUnsignedShort();
                final int state = readVarInt(buf);
                buf.retain();
                connectClient(ctx.channel(), hostName, buf);
            } else {
                //TimoCloudCord.getInstance().severe("Received non-status packet: " + packetID);
            }
        } catch (Exception e) {
            buf.resetReaderIndex(); // Wait until we receive the full packet
            return;
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        TimoCloudCord.getInstance().severe("Exception in MinecraftDecoder");
        TimoCloudCord.getInstance().severe(cause);
    }

    public static void connectClient(Channel channel, String hostName, ByteBuf loginPacket) {
        List<ProxyGroupObject> proxyGroupObject = TimoCloudCord.getInstance().getProxyManager().getProxyGroupByHostName(hostName);
        if (proxyGroupObject.isEmpty()) {
            TimoCloudCord.getInstance().severe("Error: No proxy group found for hostname '" + hostName + "'");
            channel.close();
            return;
        }
        connectClient(channel, proxyGroupObject, hostName, loginPacket);
    }

    public static void connectClient(Channel channel, List<ProxyGroupObject> proxyGroupObject, String hostName, ByteBuf loginPacket) {
        ProxyObject proxyObject = TimoCloudCord.getInstance().getProxyManager().getFreeProxy(proxyGroupObject);
        if (proxyObject == null) {
            TimoCloudCord.getInstance().severe("No free proxy of group(s) '" + proxyGroupObject.stream().map(ProxyGroupObject::getName).collect(Collectors.joining(", ")) + "' found. Disconnecting client.");
            channel.close();
            return;
        }
        connectClient(channel, proxyObject, hostName, loginPacket);
    }

    public static void connectClient(Channel channel, ProxyObject proxyObject, String hostName, ByteBuf loginPacket) {
        ProxyDownstreamHandler downstreamHandler = channel.attr(DOWNSTREAM_HANDLER).get() == null ? new ProxyDownstreamHandler(channel) : channel.attr(DOWNSTREAM_HANDLER).get();
        channel.attr(DOWNSTREAM_HANDLER).set(downstreamHandler);
        channel.attr(CONNECTION_STATE).set(ConnectionState.HANDSHAKE);
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
                TimoCloudCord.getInstance().info("[" + channel.remoteAddress() + "] connected to hostname '" + hostName + "'. Using proxy " + proxyObject.getName() + " of group " + proxyObject.getGroup().getName() + ".");
                if (channel.attr(UPSTREAM_HANDLER).get() == null) {
                    ProxyUpstreamHandler upstreamHandler = new ProxyUpstreamHandler(cf.channel(), downstreamHandler);
                    channel.pipeline().addLast(upstreamHandler);
                    channel.attr(UPSTREAM_HANDLER).set(upstreamHandler);
                } else {
                    channel.attr(UPSTREAM_HANDLER).get().setChannel(cf.channel());
                }
                if (channel.pipeline().get("minecraftdecoder") != null) channel.pipeline().remove("minecraftdecoder");

                sendIpToBungee(proxyObject, (InetSocketAddress) channel.remoteAddress(), (InetSocketAddress) cf.channel().localAddress());

                loginPacket.resetReaderIndex();
                future.channel().writeAndFlush(loginPacket.retain());
                channel.attr(CONNECTION_STATE).set(ConnectionState.PROXY);
            } else {
                channel.close();
                cf.channel().close();
            }
        });
    }

    private static void sendIpToBungee(ProxyObject proxyObject, InetSocketAddress clientAddress, InetSocketAddress channelAddress) {
        TimoCloudCord.getInstance().getSocketMessageManager().sendMessage(Message.create()
                .setType(MessageType.CORD_SET_IP)
                .setTarget(proxyObject.getId())
                .set("CLIENT_ADDRESS", toString(clientAddress))
                .set("CHANNEL_ADDRESS", channelAddress.toString()));
    }

    /**
     * Write the object to a Base64 string.
     */
    private static String toString(Serializable o) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(baos);
            oos.writeObject(o);
            oos.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return Base64.getEncoder().encodeToString(baos.toByteArray());
    }

}
