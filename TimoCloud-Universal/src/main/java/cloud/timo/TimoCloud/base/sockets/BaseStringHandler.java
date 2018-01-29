package cloud.timo.TimoCloud.base.sockets;

import cloud.timo.TimoCloud.base.TimoCloudBase;
import cloud.timo.TimoCloud.base.objects.BaseServerObject;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class BaseStringHandler extends SimpleChannelInboundHandler<String> {

    private String remaining = "";
    private String parsed = "";
    private int open = 0;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String message) {
        TimoCloudBase.getInstance().getSocketClientHandler().setChannel(ctx.channel());
        remaining = remaining + message;
        read();
    }

    public void read() {
        for (String c : remaining.split("")) {
            parsed += c;
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
            TimoCloudBase.severe("Error while parsing json: " + message);
            return;
        }
        String server = (String) json.get("server");
        String type = (String) json.get("type");
        String data = (String) json.get("data");
        String token = (String) json.get("token");
        switch (type) {
            case "STARTSERVER":
                int port = 0;
                port+= (Long) json.get("port");
                int ram = 0;
                ram += (Long) json.get("ram");
                boolean isStatic = (Boolean) json.get("static");
                String group = (String) json.get("group");
                TimoCloudBase.getInstance().getServerManager().addToQueue(new BaseServerObject(server, port, ram, isStatic, group, token));
                TimoCloudBase.info("Added server " + server + " to queue.");
                break;
            case "SERVERSTOPPED":
                TimoCloudBase.getInstance().getServerManager().onServerStopped(server);
                break;
            default:
                TimoCloudBase.severe("Could not categorize json message: " + message);
        }
    }



}
