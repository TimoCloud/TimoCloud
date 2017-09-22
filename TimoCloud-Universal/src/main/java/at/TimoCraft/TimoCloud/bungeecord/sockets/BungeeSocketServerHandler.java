package at.TimoCraft.TimoCloud.bungeecord.sockets;

/**
 * Created by Timo on 28.12.16.
 */

import at.TimoCraft.TimoCloud.bungeecord.TimoCloud;
import at.TimoCraft.TimoCloud.bungeecord.objects.BaseObject;
import at.TimoCraft.TimoCloud.bungeecord.objects.Server;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.Map;

@ChannelHandler.Sharable
public class BungeeSocketServerHandler extends ChannelInboundHandlerAdapter {

    private Map<Channel, Server> serverChannels = new HashMap<>();
    private Map<Channel, BaseObject> baseChannels = new HashMap<>();

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
        json.put("server", server);
        json.put("type", type);
        json.put("data", data);
        return json.toString();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        if (serverChannels.containsKey(channel)) {
            Server server = serverChannels.get(channel);
            if (server == null) {
                return;
            }
            server.unregister();
        }
        if (baseChannels.containsKey(channel)) {
            BaseObject base = baseChannels.get(channel);
            TimoCloud.getInstance().getServerManager().onBaseDisconnect(base);
        }
        removeChannel(channel);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    public Server getByChannel(Channel channel) {
        return serverChannels.get(channel);
    }

    public void removeChannel(Channel channel) {
        if (serverChannels.containsKey(channel)) {
            serverChannels.remove(channel);
            return;
        }
        if (baseChannels.containsKey(channel)) {
            baseChannels.remove(channel);
            return;
        }
        TimoCloud.severe("Tried to remove not existing channel " + channel);
    }

    public Map<Channel, Server> getServerChannels() {
        return serverChannels;
    }

    public Map<Channel, BaseObject> getBaseChannels() {
        return baseChannels;
    }
}