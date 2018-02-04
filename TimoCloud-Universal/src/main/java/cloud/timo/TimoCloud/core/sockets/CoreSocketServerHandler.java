package cloud.timo.TimoCloud.core.sockets;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.Map;

@ChannelHandler.Sharable
public class CoreSocketServerHandler extends ChannelInboundHandlerAdapter {

    private Map<Channel, Communicatable> communicatables;

    public CoreSocketServerHandler() {
        communicatables = new HashMap<>();
    }

    public void sendMessage(Channel channel, String type, Object data) {
        try {
            channel.writeAndFlush(getJSON(type, data));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(Channel channel, String server, String type, Object data) {
        try {
            channel.writeAndFlush(getJSON(server, type, data));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getJSON(String type, Object data) {
        JSONObject json = new JSONObject();
        json.put("type", type);
        json.put("data", data);
        return json.toString();
    }

    public String getJSON(String server, String type, Object data) {
        JSONObject json = new JSONObject();
        json.put("target", server);
        json.put("type", type);
        json.put("data", data);
        return json.toString();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) {
        Channel channel = ctx.channel();
        Communicatable communicatable = getCommunicatable(channel);
        removeChannel(channel);
        if (communicatable == null) return;
        communicatable.onDisconnect();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    public void removeChannel(Channel channel) {
        if (communicatables.containsKey(channel)) communicatables.remove(channel);
    }

    public Communicatable getCommunicatable(Channel channel) {
        return communicatables.get(channel);
    }

    public void setCommunicatable(Channel channel, Communicatable communicatable) {
        communicatables.put(channel, communicatable);
    }

}