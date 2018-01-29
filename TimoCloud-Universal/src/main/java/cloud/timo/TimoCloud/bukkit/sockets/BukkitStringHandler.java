package cloud.timo.TimoCloud.bukkit.sockets;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.bukkit.TimoCloudBukkit;
import cloud.timo.TimoCloud.bukkit.api.TimoCloudUniversalAPIBukkitImplementation;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class BukkitStringHandler extends SimpleChannelInboundHandler<String> {

    String remaining = "";
    String parsed = "";
    int open = 0;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String message) {
        TimoCloudBukkit.getInstance().getSocketClientHandler().setChannel(ctx.channel());
        remaining = remaining + message;
        read();
    }

    public void read() {
        for (String c : remaining.split("")) {
            parsed = parsed + c;
            remaining = remaining.substring(1);
            if (c.equals("{")) {
                open++;
            }
            if (c.equals("}")) {
                open--;
                if (open == 0) {
                    handleJSON((JSONObject) JSONValue.parse(parsed), parsed);
                    parsed = "";
                }
            }
        }
    }

    public void handleJSON(JSONObject json, String message) {
        if (json == null) {
            TimoCloudBukkit.log("Error while parsing json: " + message);
            return;
        }
        String server = (String) json.get("server");
        String type = (String) json.get("type");
        Object data = json.get("data");
        switch (type) {
            case "APIDATA":
                ((TimoCloudUniversalAPIBukkitImplementation) TimoCloudAPI.getUniversalInstance()).setData((String) data);
                TimoCloudBukkit.getInstance().getStateByEventManager().setStateByPlayerCount();
                break;
            case "EXECUTE_COMMAND":
                TimoCloudBukkit.getInstance().getServer().dispatchCommand(TimoCloudBukkit.getInstance().getServer().getConsoleSender(), (String) data);
                break;
            default:
                TimoCloudBukkit.log("Error: Could not categorize json message: " + message);
        }
    }

}
