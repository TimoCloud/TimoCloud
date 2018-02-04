package cloud.timo.TimoCloud.bungeecord.sockets;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.bungeecord.TimoCloudBungee;
import cloud.timo.TimoCloud.bungeecord.api.TimoCloudUniversalAPIBungeeImplementation;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.md_5.bungee.api.config.ServerInfo;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.net.InetSocketAddress;

public class BungeeStringHandler extends SimpleChannelInboundHandler<String> {

    private StringBuilder parsed;
    private int open = 0;

    public BungeeStringHandler() {
        parsed = new StringBuilder();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String message) {
        TimoCloudBungee.getInstance().getSocketClientHandler().setChannel(ctx.channel());
        read(message);
    }

    public void read(String message) {
        for (String c : message.split("")) {
            parsed.append(c);
            if (c.equals("{")) open++;
            if (c.equals("}")) {
                open--;
                if (open == 0) {
                    handleJSON((JSONObject) JSONValue.parse(parsed.toString()), parsed.toString());
                    parsed = new StringBuilder();
                }
            }
        }
    }

    public void handleJSON(JSONObject json, String message) {
        if (json == null) {
            TimoCloudBungee.severe("Error while parsing json: " + message);
            return;
        }
        String server = (String) json.get("target");
        String type = (String) json.get("type");
        Object data = json.get("data");
        switch (type) {
            case "APIDATA":
                ((TimoCloudUniversalAPIBungeeImplementation) TimoCloudAPI.getUniversalInstance()).setData((String) data);
                break;
            case "EXECUTE_COMMAND":
                TimoCloudBungee.getInstance().getProxy().getPluginManager().dispatchCommand(TimoCloudBungee.getInstance().getProxy().getConsole(), (String) data);
                break;
            case "ADD_SERVER":
                TimoCloudBungee.getInstance().getProxy().getServers().put(server, TimoCloudBungee.getInstance().getProxy().constructServerInfo(server, new InetSocketAddress((String) json.get("address"), (int) json.get("port")), "", false));
                break;
            case "REMOVE_SERVER":
                TimoCloudBungee.getInstance().getProxy().getServers().remove(server);
                break;
            default:
                TimoCloudBungee.severe("Could not categorize json message: " + message);
        }
    }

}
