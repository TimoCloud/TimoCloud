package cloud.timo.TimoCloud.common.utils.network;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class NettyUtil {

    public static EventLoopGroup getEventLoopGroup() {
        switch (getTransportType()) {
            case EPOLL:
                return new EpollEventLoopGroup();
            case NIO:
                return new NioEventLoopGroup();
            default:
                return null;
        }
    }

    public static Class<? extends ServerSocketChannel> getServerSocketChannelClass() {
        switch (getTransportType()) {
            case EPOLL:
                return EpollServerSocketChannel.class;
            case NIO:
                return NioServerSocketChannel.class;
            default:
                return null;
        }
    }

    public static Class<? extends SocketChannel> getSocketChannelClass() {
        switch (getTransportType()) {
            case EPOLL:
                return EpollSocketChannel.class;
            case NIO:
                return NioSocketChannel.class;
            default:
                return null;
        }
    }

    public static NettyTransportType getTransportType() {
        if (epollAvailable()) return NettyTransportType.EPOLL;
        return NettyTransportType.NIO;
    }

    private static boolean epollAvailable() {
        return Epoll.isAvailable();
    }

    private static enum NettyTransportType {
        NIO, EPOLL
    }
}
