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

/**
 * Created by Timo on 28.12.16.
 */
public class BukkitSocketClientHandler extends ChannelInboundHandlerAdapter {

    /**
     * Creates a client-side handler.
     */

    private Channel channel;

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        Main.log("Successfully connected to bungee socket!");
        this.channel = ctx.channel();
        Main.getInstance().getSocketMessageManager().sendMessage("HANDSHAKE", "I_JUST_CAME_ONLINE");
    }

    public void sendMessage(String message) {
        channel.writeAndFlush(Unpooled.copiedBuffer(message, CharsetUtil.UTF_8));
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        String message = ((ByteBuf) msg).toString(CharsetUtil.UTF_8);
        JSONObject json = (JSONObject) JSONValue.parse(message);
        String server = (String) json.get("server");
        String type = (String) json.get("type");
        String data = (String) json.get("data");
        switch (type) {
            case "STATE":
                Main.getInstance().getOtherServerPingManager().setState(server, data);
                break;
            case "EXTRA":
                Main.getInstance().getOtherServerPingManager().setExtra(server, data);
                break;
            default:
                Main.log("Error: Could not categorize json message: " + message);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
        Main.getInstance().onSocketDisconnect();
    }
}
