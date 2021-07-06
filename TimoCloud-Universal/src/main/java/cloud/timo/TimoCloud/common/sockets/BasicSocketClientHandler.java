package cloud.timo.TimoCloud.common.sockets;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class BasicSocketClientHandler extends ChannelInboundHandlerAdapter {
    private Channel channel;
    private final StringBuilder queue;

    public BasicSocketClientHandler() {
        queue = new StringBuilder();
    }

    public void resetQueue() {
        queue.setLength(0);
    }

    public void flush() {
        if (channel == null) {
            return;
        }
        channel.writeAndFlush(queue.toString());
        resetQueue();
    }

    public void sendMessage(String message) {
        if (channel == null) {
            queue.append(message);
        } else {
            channel.writeAndFlush(message);
        }
    }


    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }
}
