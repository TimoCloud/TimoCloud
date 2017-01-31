package at.TimoCraft.TimoCloud.bungeecord.sockets;

/**
 * Created by Timo on 28.12.16.
 */

import at.TimoCraft.TimoCloud.bungeecord.TimoCloud;
import at.TimoCraft.TimoCloud.bungeecord.objects.TemporaryServer;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

@ChannelHandler.Sharable
public class BungeeSocketServerHandler extends ChannelInboundHandlerAdapter {

    private Map<Channel, TemporaryServer> channels = new HashMap<>();
    //private Map<String, Channel> queue;
    
    public BungeeSocketServerHandler() {
        //resetQueue();
    }
    
    /*
    public void resetQueue() {
        queue = new HashMap<>();
    }
    */

    public void sendMessage(Channel channel, String server, String type, String data) {
        try {
            channel.writeAndFlush(getJSON(server, type, data));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
/*
    public void flush() {
        Map<String, Channel> q = new HashMap<>(queue);
        queue = new HashMap<>();
        for (String message : q.keySet()) {
            try {
                q.get(message).writeAndFlush(message);
            } catch (Exception e) {
                TimoCloud.severe("Error while sending message to server " + channels.get(q.get(message)).getName() + ": " + message);
                e.printStackTrace();
            }
        }
    }
*/
    public String getJSON(String server, String type, String data) {
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
        TemporaryServer server = channels.get(channel);
        if (server == null) {
            return;
        }
        server.unregister();
        removeChannel(channel);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    public TemporaryServer getByChannel(Channel channel) {
        return channels.get(channel);
    }

    public void removeChannel(Channel channel) {
        if (channels.containsKey(channel)) {
            channels.remove(channel);
            return;
        }
        TimoCloud.severe("Tried to remove not existing channel " + channel);
    }

    public Map<Channel, TemporaryServer> getChannels() {
        return channels;
    }
}