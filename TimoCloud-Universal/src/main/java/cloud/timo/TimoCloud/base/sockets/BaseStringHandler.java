package cloud.timo.TimoCloud.base.sockets;

import cloud.timo.TimoCloud.base.TimoCloudBase;
import cloud.timo.TimoCloud.base.objects.BaseProxyObject;
import cloud.timo.TimoCloud.base.objects.BaseServerObject;
import cloud.timo.TimoCloud.sockets.BasicStringHandler;
import io.netty.channel.ChannelHandler;
import org.apache.commons.io.FileUtils;
import org.json.simple.JSONObject;

import java.io.File;
import java.util.Base64;
import java.util.Date;

@ChannelHandler.Sharable
public class BaseStringHandler extends BasicStringHandler {

    @Override
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
                    File file = new File(TimoCloudBase.getInstance().getFileManager().getCacheDirectory(), new Date().getTime() + "");
                    file.createNewFile();
                    byte[] content = Base64.getDecoder().decode(((String) json.get("file")).replace("\\", ""));
                    FileUtils.writeByteArrayToFile(file, content);
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
