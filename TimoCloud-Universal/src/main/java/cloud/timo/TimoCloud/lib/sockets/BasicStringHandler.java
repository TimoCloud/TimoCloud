package cloud.timo.TimoCloud.lib.sockets;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.util.HashMap;
import java.util.Map;

public class BasicStringHandler extends SimpleChannelInboundHandler<String> {

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
            if (c.equals("\"") && (getParsed(channel).length() < 2 || !Character.toString(getParsed(channel).charAt(getParsed(channel).length() - 2)).equals("\\"))) setIsString(channel, !isString(channel));
            if (isString(channel)) continue;
            if (c.equals("{")) open.put(channel, getOpen(channel) + 1);
            if (c.equals("}")) {
                open.put(channel, getOpen(channel) - 1);
                if (getOpen(channel) == 0) {
                    try {
                        handleJSON((JSONObject) JSONValue.parse(getParsed(channel).toString()), getParsed(channel).toString(), channel);
                    } catch (Exception e) {
                        System.err.println("Error while parsing JSON message: " + getParsed(channel));
                        e.printStackTrace();
                    }
                    parsed.put(channel, new StringBuilder());
                }
            }
        }
    }

    public void handleJSON(JSONObject json, String message, Channel channel) {}

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

}
