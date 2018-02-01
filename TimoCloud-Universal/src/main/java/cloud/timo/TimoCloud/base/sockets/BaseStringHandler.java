package cloud.timo.TimoCloud.base.sockets;

import cloud.timo.TimoCloud.base.TimoCloudBase;
import cloud.timo.TimoCloud.base.objects.BaseProxyObject;
import cloud.timo.TimoCloud.base.objects.BaseServerObject;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.File;

public class BaseStringHandler extends SimpleChannelInboundHandler<String> {

    private StringBuilder parsed;
    private int open = 0;

    public BaseStringHandler() {
        parsed = new StringBuilder();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String message) {
        TimoCloudBase.getInstance().getSocketClientHandler().setChannel(ctx.channel());
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
            TimoCloudBase.severe("Error while parsing json: " + message);
            return;
        }
        String name = (String) json.get("server");
        String type = (String) json.get("type");
        String data = (String) json.get("data");
        String token = (String) json.get("token");
        switch (type) {
            case "START_SERVER": {
                int ram = (int) json.get("ram");
                boolean isStatic = (Boolean) json.get("static");
                String group = (String) json.get("group");
                String map = (String) json.get("map");
                JSONObject templateHash = (JSONObject) json.get("templateHash");
                JSONObject mapHash = json.containsKey("mapHash") ? (JSONObject) json.get("mapHash") : null;
                JSONObject globalHash = (JSONObject) json.get("globalHash");
                TimoCloudBase.getInstance().getServerManager().addToServerQueue(new BaseServerObject(name, group, ram, isStatic, map, token, templateHash, mapHash, globalHash));
                TimoCloudBase.info("Added server " + name + " to queue.");
                break;
            }
            case "START_PROXY": {
                int ram = (int) json.get("ram");
                boolean isStatic = (Boolean) json.get("static");
                String group = (String) json.get("group");
                JSONObject templateHash = (JSONObject) json.get("templateHash");
                JSONObject globalHash = (JSONObject) json.get("globalHash");
                TimoCloudBase.getInstance().getServerManager().addToProxyQueue(new BaseProxyObject(name, group, ram, isStatic, token, templateHash, globalHash));
                TimoCloudBase.info("Added server " + name + " to queue.");
                break;
            }
            case "SERVER_STOPPED":
                TimoCloudBase.getInstance().getServerManager().onServerStopped(name);
                break;
            case "TRANSFER_FINISHED":
                try {
                    TimoCloudBase.getInstance().getFileChunkHandler().getFileOutputStream().close();
                    TimoCloudBase.getInstance().getFileChunkHandler().setFileOutputStream(null);
                    File file = TimoCloudBase.getInstance().getFileChunkHandler().getFile();
                    switch ((String) json.get("transferType")) {
                        case "SERVER_TEMPLATE":
                            TimoCloudBase.getInstance().getTemplateManager().extractFiles(file, new File(TimoCloudBase.getInstance().getFileManager().getServerTemplatesDirectory(), (String) json.get("template")));
                            break;
                        case "SERVER_GLOBAL_TEMPLATE":
                            TimoCloudBase.getInstance().getTemplateManager().extractFiles(file, TimoCloudBase.getInstance().getFileManager().getServerGlobalDirectory());
                            break;
                    }
                    break;
                } catch (Exception e) {
                    TimoCloudBase.severe("Error while unpacking transfered files: ");
                    e.printStackTrace();
                }
            default:
                TimoCloudBase.severe("Could not categorize json message: " + message);
        }
    }



}
