package cloud.timo.TimoCloud.base.sockets;

import cloud.timo.TimoCloud.base.TimoCloudBase;
import cloud.timo.TimoCloud.base.objects.BaseProxyObject;
import cloud.timo.TimoCloud.base.objects.BaseServerObject;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;

public class BaseStringHandler extends SimpleChannelInboundHandler<String> {

    private StringBuilder parsed;
    private int open = 0;
    boolean isString = false;

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
            if (c.equals("\"") && (parsed.length() < 2 ? true : ! Character.toString(parsed.charAt(parsed.length()-2)).equals("\\"))) isString = !isString;
            if (isString) continue;
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
        String type = (String) json.get("type");
        Object data = json.get("data");
        switch (type) {
            case "START_SERVER": {
                String serverName = (String) json.get("name");
                String token = (String) json.get("token");
                int ram = ((Long) json.get("ram")).intValue();
                boolean isStatic = (Boolean) json.get("static");
                String group = (String) json.get("group");
                String map = (String) json.get("map");
                JSONObject templateHash = (JSONObject) json.get("templateHash");
                JSONObject mapHash = json.containsKey("mapHash") ? (JSONObject) json.get("mapHash") : null;
                JSONObject globalHash = (JSONObject) json.get("globalHash");
                TimoCloudBase.getInstance().getServerManager().addToServerQueue(new BaseServerObject(serverName, group, ram, isStatic, map, token, templateHash, mapHash, globalHash));
                TimoCloudBase.info("Added server " + serverName + " to queue.");
                break;
            }
            case "START_PROXY": {
                String token = (String) json.get("token");
                String proxyName = (String) json.get("name");
                int ram = ((Long) json.get("ram")).intValue();
                boolean isStatic = (Boolean) json.get("static");
                String group = (String) json.get("group");
                String motd = (String) json.get("motd");
                int maxPlayers = ((Long) json.get("maxplayers")).intValue();
                int maxPlayersPerProxy = ((Long) json.get("maxplayersperproxy")).intValue();
                JSONObject templateHash = (JSONObject) json.get("templateHash");
                JSONObject globalHash = (JSONObject) json.get("globalHash");
                TimoCloudBase.getInstance().getServerManager().addToProxyQueue(new BaseProxyObject(proxyName, group, ram, isStatic, token, motd, maxPlayers, maxPlayersPerProxy, templateHash, globalHash));
                TimoCloudBase.info("Added proxy " + proxyName + " to queue.");
                break;
            }
            case "SERVER_STOPPED":
                TimoCloudBase.getInstance().getServerManager().onServerStopped((String) json.get("target"), (String) data);
                break;
            case "PROXY_STOPPED":
                TimoCloudBase.getInstance().getServerManager().onProxyStopped((String) json.get("target"), (String) data);
                break;
            case "TRANSFER":
                try {
                    /*
                    ByteBuf byteBuf = Unpooled.wrappedBuffer(((String) json.get("file")).getBytes(Charset.defaultCharset()));
                    System.out.println("Got ByteBuf");
                    int numberOfReadableBytes = byteBuf.readableBytes();
                    byte[] bytes = new byte[numberOfReadableBytes];
                    byteBuf.readBytes(bytes);
                    */
                    File file = new File(TimoCloudBase.getInstance().getFileManager().getCacheDirectory(), new Date().getTime() + "");
                    file.createNewFile();
                    byte[] content = Base64.getDecoder().decode(((String) json.get("file")).replace("\\", ""));
                    FileUtils.writeByteArrayToFile(file, content);
                    /*
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    fileOutputStream.write(bytes);
                    fileOutputStream.close();
                    */
                    switch ((String) json.get("transferType")) {
                        case "SERVER_TEMPLATE":
                            TimoCloudBase.getInstance().getTemplateManager().extractFilesAndDeleteZip(file, new File(TimoCloudBase.getInstance().getFileManager().getServerTemplatesDirectory(), (String) json.get("template")));
                            break;
                        case "SERVER_GLOBAL_TEMPLATE":
                            TimoCloudBase.getInstance().getTemplateManager().extractFilesAndDeleteZip(file, TimoCloudBase.getInstance().getFileManager().getServerGlobalDirectory());
                            break;
                        case "PROXY_TEMPLATE":
                            TimoCloudBase.getInstance().getTemplateManager().extractFilesAndDeleteZip(file, new File(TimoCloudBase.getInstance().getFileManager().getProxyTemplatesDirectory(), (String) json.get("template")));
                            break;
                        case "PROXY_GLOBAL_TEMPLATE":
                            TimoCloudBase.getInstance().getTemplateManager().extractFilesAndDeleteZip(file, TimoCloudBase.getInstance().getFileManager().getProxyGlobalDirectory());
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
