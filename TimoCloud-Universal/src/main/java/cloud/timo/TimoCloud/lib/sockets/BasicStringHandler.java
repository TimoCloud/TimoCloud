package cloud.timo.TimoCloud.lib.sockets;

import cloud.timo.TimoCloud.lib.global.logging.TimoCloudLogger;
import cloud.timo.TimoCloud.lib.protocol.Message;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public abstract class BasicStringHandler extends SimpleChannelInboundHandler<String> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String message) throws Exception {
        Channel channel = ctx.channel();
        try {
            handleMessage(Message.createFromJsonString(message), message, channel);
        } catch (Throwable e) {
            TimoCloudLogger.getLogger().severe("Error while parsing JSON message: " + message);
            TimoCloudLogger.getLogger().severe(e);
        }
    }

    public abstract void handleMessage(Message message, String originalMessage, Channel channel);

    public void closeChannel(Channel channel) {
        channel.close();
    }

    /*
    private Map<Channel, Integer> open;
    private Map<Channel, StringBuilder> parsed;
    private Map<Channel, Boolean> isString;

    public BasicStringHandler() {
        open = new HashMap<>();
        parsed = new HashMap<>();
        isString = new HashMap<>();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String message) throws Exception {
        read(channelHandlerContext.channel(), message);
    }

    public void read(Channel channel, String message) {
        for (String c : message.split("")) {
            getParsed(channel).append(c);
            if (c.equals("\"") &&
                    (getParsed(channel).length() < 2 || ! Character.toString(getParsed(channel).charAt(getParsed(channel).length() - 2)).equals("\\"))) {
                setIsString(channel, !isString(channel));
            }
            if (isString(channel)) continue;
            if (c.equals("{")) open.put(channel, getOpen(channel) + 1);
            if (c.equals("}")) {
                open.put(channel, getOpen(channel) - 1);
                if (getOpen(channel) == 0) {
                    try {
                        String parsed = getParsed(channel).toString();
                        handleMessage(Message.createFromJsonString(parsed), parsed, channel);
                    } catch (Throwable e) {
                        TimoCloudLogger.getLogger().severe("Error while parsing JSON message: " + getParsed(channel));
                        TimoCloudLogger.getLogger().severe(e);
                    }
                    parsed.put(channel, new StringBuilder());
                }
            }
        }
    }

    public abstract void handleMessage(Message message, String originalMessage, Channel channel);

    private int getOpen(Channel channel) {
        open.putIfAbsent(channel, 0);
        return open.get(channel);
    }

    private StringBuilder getParsed(Channel channel) {
        parsed.putIfAbsent(channel, new StringBuilder());
        return parsed.get(channel);
    }

    private boolean isString(Channel channel) {
        return this.isString.getOrDefault(channel, false);
    }

    private void setIsString(Channel channel, boolean isString) {
        this.isString.put(channel, isString);
    }

    public void closeChannel(Channel channel) {
        channel.close();
        open.remove(channel);
        parsed.remove(channel);
        isString.remove(channel);
    }
    */

}
