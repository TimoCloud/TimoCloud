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

@ChannelHandler.Sharable
public class BungeeSocketServerHandler extends ChannelInboundHandlerAdapter {

    private Map<Channel, TemporaryServer> channels = new HashMap<>();
    private Map<String, Channel> queue;
    
    public BungeeSocketServerHandler() {
        resetQueue();
    }
    
    public void resetQueue() {
        queue = new HashMap<>();
    }

    public void sendMessage(Channel channel, String server, String type, String data) {
        try {
            queue.put(getJSON(server, type, data), channel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void flush() {
        if (queue.keySet().size() < 1) {
            return;
        }
        ArrayList<String> q = (ArrayList<String>) (new ArrayList<>(queue.keySet())).clone();
        for (String message : q) {
            try {
                queue.get(message).writeAndFlush(message);
            } catch (Exception e) {
                TimoCloud.severe("Error while sending message to server " + channels.get(queue.get(message)).getName() + ": " + message);
                e.printStackTrace();
            }
        }
        for (String key : q) {
            if (queue.containsKey(key)) {
                queue.remove(key);
            }
        }
        if (queue.size() > 0) {
            flush();
        }
    }

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
        if (TimoCloud.getInstance().isShuttingDown()) {
            server.unregister(false);
        } else {
            server.unregister(! server.isOnce());
        }
        removeChannel(channel);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
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