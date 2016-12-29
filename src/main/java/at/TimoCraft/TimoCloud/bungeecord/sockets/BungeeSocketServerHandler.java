package at.TimoCraft.TimoCloud.bungeecord.sockets;

/**
 * Created by Timo on 28.12.16.
 */

import at.TimoCraft.TimoCloud.bungeecord.TimoCloud;
import at.TimoCraft.TimoCloud.bungeecord.objects.TemporaryServer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.util.List;
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

    /*
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        String message = ((ByteBuf) msg).toString(CharsetUtil.UTF_8);

        List<JSONObject> jsons = split(message);
        for (JSONObject json : jsons) {
            try {
                handleJSON(json, message, ctx.channel());
            } catch (Exception e) {
                TimoCloud.severe("Error while parsing JSON message: " + message);
            }
        }
    }

    public List<JSONObject> split(String message) {
        if (! (message.startsWith("{") && message.endsWith("}"))) {
            TimoCloud.severe("Could not parse JSON message: " + message);
            return new ArrayList<>();
        }
        List<JSONObject> jsons = new ArrayList<>();
        int open = 0;
        String parsed = "";
        for (String c : message.split("")) {
            if (c.equals("{")) {
                open++;
            }
            if (c.equals("}")) {
                open--;
            }
            parsed = parsed + c;
            if (open == 0) {
                jsons.add((JSONObject) JSONValue.parse(parsed));
                parsed = "";
            }
        }
        return jsons;
    }
*/
    public void sendMessage(Channel channel, String server, String type, String data) {
        try {
            queue.put(getJSON(server, type, data), channel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void flush() {
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
            queue.remove(key);
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
        server.unregister(!TimoCloud.getInstance().isShuttingDown());
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