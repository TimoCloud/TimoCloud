package at.TimoCraft.TimoCloud.bungeecord.sockets;

/**
 * Created by Timo on 28.12.16.
 */

import at.TimoCraft.TimoCloud.bungeecord.TimoCloud;
import at.TimoCraft.TimoCloud.bungeecord.managers.ServerManager;
import at.TimoCraft.TimoCloud.bungeecord.objects.TemporaryServer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.util.HashMap;
import java.util.Map;

public class BungeeSocketServerHandler extends ChannelInboundHandlerAdapter {

    private Map<Channel, TemporaryServer> channels = new HashMap<>();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        String message = ((ByteBuf) msg).toString(CharsetUtil.UTF_8);
        JSONObject json = (JSONObject) JSONValue.parse(message);
        String serverName = (String) json.get("server");
        TemporaryServer server = TimoCloud.getInstance().getServerManager().getFromServerName(serverName);
        if (server == null) {
            TimoCloud.severe("Unknown server connected: " + serverName);
            ctx.close();
            return;
        }
        server.setChannel(ctx.channel());
        channels.put(ctx.channel(), server);
        String type = (String) json.get("type");
        String data = (String) json.get("data");
        switch (type) {
            case "HANDSHAKE":
                if (data.equals("I_JUST_CAME_ONLINE")) {
                    server.register();
                    break;
                }
                TimoCloud.severe("Uncategorized handshake message: " + message);
                break;
            case "SETSTATE":
                server.setState(data);
                break;
            case "GETSTATE":
                TemporaryServer requestedServer = TimoCloud.getInstance().getServerManager().getFromServerName(data);
                sendMessage(ctx.channel(), data, "STATE", requestedServer == null ? "Unknown" : (requestedServer.getState() == null ? "Unknown" : requestedServer.getState()));
                break;
            case "SETEXTRA":
                server.setExtra(data);
                break;
            case "GETEXTRA":
                TemporaryServer requestedServer2 = TimoCloud.getInstance().getServerManager().getFromServerName(data);
                sendMessage(ctx.channel(), data, "EXTRA", requestedServer2 == null ? "Unknown" : (requestedServer2.getExtra() == null ? "Unknown" : requestedServer2.getExtra()));
                break;
            default:
                TimoCloud.severe("Could not categorize json message: " + message);
        }
    }

    public void sendMessage(Channel channel, String server, String type, String data) {
        try {
            channel.writeAndFlush(Unpooled.copiedBuffer(getJSON(server, type, data), CharsetUtil.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
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
}