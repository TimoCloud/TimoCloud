package at.TimoCraft.TimoCloud.bungeecord.sockets;

import at.TimoCraft.TimoCloud.bungeecord.TimoCloud;
import at.TimoCraft.TimoCloud.bungeecord.objects.TemporaryServer;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Timo on 29.12.16.
 */
public class StringHandler extends SimpleChannelInboundHandler<String> {

    private Map<Channel, Integer> open;
    private Map<Channel, String> remaining;
    private Map<Channel, String> parsed;

    public StringHandler() {
        open = new HashMap<>();
        remaining = new HashMap<>();;
        parsed = new HashMap<>();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String message) {
        try {
            remaining.put(ctx.channel(), getRemaining(ctx.channel()) + message);
            read(ctx.channel());
        } catch (Exception e) {
            TimoCloud.severe("Error while parsing JSON message: " + message);
            e.printStackTrace();
        }
    }

    public void read(Channel channel) {
        for (String c : getRemaining(channel).split("")) {
            parsed.put(channel, getParsed(channel) + c);
            remaining.put(channel, getRemaining(channel).substring(1));
            if (c.equals("{")) {
                open.put(channel, getOpen(channel) + 1);
            }
            if (c.equals("}")) {
                open.put(channel, getOpen(channel) - 1);
                if (getOpen(channel) == 0) {
                    handleJSON((JSONObject) JSONValue.parse(getParsed(channel)), getParsed(channel), channel);
                    parsed.put(channel, "");
                }
            }
        }
    }

    public void handleJSON(JSONObject json, String message, Channel channel) {
        String serverName = (String) json.get("server");
        TemporaryServer server = TimoCloud.getInstance().getServerManager().getFromServerName(serverName);
        if (server == null) {
            TimoCloud.severe("Unknown server connected: " + serverName);
            channel.close();
            return;
        }
        server.setChannel(channel);
        TimoCloud.getInstance().getSocketServerHandler().getChannels().put(channel, server);
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
                if (requestedServer == null) {
                    TimoCloud.severe("Requested server " + data + " not found.");
                }
                TimoCloud.getInstance().getSocketServerHandler().sendMessage(channel, data, "STATE", requestedServer == null ? "UNKNOWN" : (requestedServer.getState() == null ? "UNKNOWN" : requestedServer.getState()));
                break;
            case "SETEXTRA":
                server.setExtra(data);
                break;
            case "GETEXTRA":
                TemporaryServer requestedServer2 = TimoCloud.getInstance().getServerManager().getFromServerName(data);
                TimoCloud.getInstance().getSocketServerHandler().sendMessage(channel, data, "EXTRA", requestedServer2 == null ? "UNKNOWN" : (requestedServer2.getExtra() == null ? "UNKNOWN" : requestedServer2.getExtra()));
                break;
            default:
                TimoCloud.severe("Could not categorize json message: " + message);
        }
    }

    public int getOpen(Channel channel) {
        if (open.get(channel) == null) {
            open.put(channel, 0);
        }
        return open.get(channel);
    }

    public String getRemaining(Channel channel) {
        if (remaining.get(channel) == null) {
            remaining.put(channel, "");
        }
        return remaining.get(channel);
    }

    public String getParsed(Channel channel) {
        if (parsed.get(channel) == null) {
            parsed.put(channel, "");
        }
        return parsed.get(channel);
    }

}
