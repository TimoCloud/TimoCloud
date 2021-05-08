package cloud.timo.TimoCloud.base.sockets;

import cloud.timo.TimoCloud.base.TimoCloudBase;
import cloud.timo.TimoCloud.base.objects.BaseProxyObject;
import cloud.timo.TimoCloud.base.objects.BaseServerObject;
import cloud.timo.TimoCloud.common.protocol.Message;
import cloud.timo.TimoCloud.common.protocol.MessageType;
import cloud.timo.TimoCloud.common.sockets.BasicStringHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import org.apache.commons.io.FileDeleteStrategy;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@ChannelHandler.Sharable
public class BaseStringHandler extends BasicStringHandler {

    @Override
    public void handleMessage(Message message, String originalMessage, Channel channel) {
        MessageType type = message.getType();
        Object data = message.getData();
        switch (type) {
            case BASE_HANDSHAKE_SUCCESS:
                TimoCloudBase.getInstance().onHandshakeSuccess();
                break;
            case BASE_START_SERVER: {
                String serverName = (String) message.get("name");
                String id = (String) message.get("id");
                int ram = ((Number) message.get("ram")).intValue();
                boolean isStatic = (Boolean) message.get("static");
                String group = (String) message.get("group");
                String map = (String) message.get("map");
                Map<String, Object> templateHash = (Map<String, Object>) message.get("templateHash");
                Map<String, Object> mapHash = message.containsKey("mapHash") ? (Map<String, Object>) message.get("mapHash") : null;
                Map<String, Object> globalHash = (Map<String, Object>) message.get("globalHash");
                List<String> javaParameters = (List<String>) message.get("javaParameters");
                List<String> spigotParameters = (List<String>) message.get("spigotParameters");
                String jrePath = (String) message.get("jrePath");
                TimoCloudBase.getInstance().getInstanceManager().addToServerQueue(new BaseServerObject(serverName, id, ram, isStatic, map, group, templateHash, mapHash, globalHash, javaParameters, spigotParameters, jrePath));
                TimoCloudBase.getInstance().info("Added server " + serverName + " to queue.");
                break;
            }
            case BASE_START_PROXY: {
                String proxyName = (String) message.get("name");
                String id = (String) message.get("id");
                int ram = ((Number) message.get("ram")).intValue();
                boolean isStatic = (Boolean) message.get("static");
                String group = (String) message.get("group");
                String motd = (String) message.get("motd");
                int maxPlayers = ((Number) message.get("maxplayers")).intValue();
                int maxPlayersPerProxy = ((Number) message.get("maxplayersperproxy")).intValue();
                Map<String, Object> templateHash = (Map<String, Object>) message.get("templateHash");
                Map<String, Object> globalHash = (Map<String, Object>) message.get("globalHash");
                List<String> javaParameters = (List<String>) message.get("javaParameters");
                String jrePath = (String) message.get("jrePath");
                TimoCloudBase.getInstance().getInstanceManager().addToProxyQueue(new BaseProxyObject(proxyName, id, ram, isStatic, group, motd, maxPlayers, maxPlayersPerProxy, templateHash, globalHash, javaParameters, jrePath));
                TimoCloudBase.getInstance().info("Added proxy " + proxyName + " to queue.");
                break;
            }
            case BASE_SERVER_STOPPED:
                TimoCloudBase.getInstance().getInstanceManager().onServerStopped((String) data);
                break;
            case BASE_PROXY_STOPPED:
                TimoCloudBase.getInstance().getInstanceManager().onProxyStopped((String) data);
                break;
            case BASE_DELETE_DIRECTORY:
                File dir = new File((String) data);
                if (dir.exists() && dir.isDirectory()) FileDeleteStrategy.FORCE.deleteQuietly(dir);
                break;
            case TRANSFER_TEMPLATE:
                try {
                    InputStream inputStream = new ByteArrayInputStream(stringToByteArray((String) message.get("file")));
                    switch ((String) message.get("transferType")) {
                        case "SERVER_TEMPLATE":
                            TimoCloudBase.getInstance().getTemplateManager().extractFiles(inputStream, new File(TimoCloudBase.getInstance().getFileManager().getServerTemplatesDirectory(), (String) message.get("template")));
                            TimoCloudBase.getInstance().getSocketMessageManager().sendMessage(Message.create().setType(MessageType.SERVER_TRANSFER_FINISHED).setTarget(message.getTarget()));
                            break;
                        case "SERVER_GLOBAL_TEMPLATE":
                            TimoCloudBase.getInstance().getTemplateManager().extractFiles(inputStream, TimoCloudBase.getInstance().getFileManager().getServerGlobalDirectory());
                            TimoCloudBase.getInstance().getSocketMessageManager().sendMessage(Message.create().setType(MessageType.SERVER_TRANSFER_FINISHED).setTarget(message.getTarget()));
                            break;
                        case "PROXY_TEMPLATE":
                            TimoCloudBase.getInstance().getTemplateManager().extractFiles(inputStream, new File(TimoCloudBase.getInstance().getFileManager().getProxyTemplatesDirectory(), (String) message.get("template")));
                            TimoCloudBase.getInstance().getSocketMessageManager().sendMessage(Message.create().setType(MessageType.PROXY_TRANSFER_FINISHED).setTarget(message.getTarget()));
                            break;
                        case "PROXY_GLOBAL_TEMPLATE":
                            TimoCloudBase.getInstance().getTemplateManager().extractFiles(inputStream, TimoCloudBase.getInstance().getFileManager().getProxyGlobalDirectory());
                            TimoCloudBase.getInstance().getSocketMessageManager().sendMessage(Message.create().setType(MessageType.PROXY_TRANSFER_FINISHED).setTarget(message.getTarget()));
                            break;
                    }
                    TimoCloudBase.getInstance().getInstanceManager().setDownloadingTemplate(false);
                    break;
                } catch (Exception e) {
                    TimoCloudBase.getInstance().severe("Error while unpacking transferred files: ");
                    TimoCloudBase.getInstance().severe(e);
                }
            default:
                TimoCloudBase.getInstance().severe("Could not categorize json message: " + originalMessage);
        }
    }

    private byte[] stringToByteArray(String input) {
        return Base64.getDecoder().decode(input.getBytes());
    }

}
