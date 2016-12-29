package at.TimoCraft.TimoCloud.bukkit.sockets;

import at.TimoCraft.TimoCloud.bukkit.Main;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import java.util.List;
import java.util.ArrayList;

/**
 * Created by Timo on 28.12.16.
 */
public class BukkitSocketClientHandler extends ChannelInboundHandlerAdapter {

    private Channel channel;
    private List<String> queue;

    public BukkitSocketClientHandler() {
        resetQueue();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        Main.log("Successfully connected to bungee socket!");
        this.channel = ctx.channel();
        Main.getInstance().getSocketMessageManager().sendMessage("HANDSHAKE", "I_JUST_CAME_ONLINE");
        Main.getInstance().getSocketMessageManager().sendMessage("SETSTATE", "ONLINE");
    }

    public void resetQueue() {
        queue = new ArrayList<>();
    }

    public void sendMessage(String message) {
        queue.add(message);
    }

    public void flush() {
        if (channel == null) {
            return;
        }
        ArrayList<String> q = (ArrayList<String>) ((ArrayList<String>)queue).clone();
        for (String message : q) {
            channel.writeAndFlush(message); //Unpooled.copiedBuffer(message, CharsetUtil.UTF_8)
        }
        queue.removeAll(q);
        if (queue.size() > 0) {
            flush();
        }
    }

    /*
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        String message = ((ByteBuf) msg).toString(CharsetUtil.UTF_8);
        List<JSONObject> jsons = split(message);
        for (JSONObject json : jsons) {
            handleJSON(json, message);
        }
    }

    public List<JSONObject> split(String message) {
        if (! (message.startsWith("{") && message.endsWith("}"))) {
            Main.log("Error: Got unreadable JSON message: " + message);
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
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }
*/

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
        Main.getInstance().onSocketDisconnect();
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }
}
