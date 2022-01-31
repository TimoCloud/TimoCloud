package cloud.timo.TimoCloud.base.sockets.handler;

import cloud.timo.TimoCloud.base.TimoCloudBase;
import cloud.timo.TimoCloud.base.objects.BaseServerObject;
import cloud.timo.TimoCloud.common.protocol.Message;
import cloud.timo.TimoCloud.common.protocol.MessageType;
import cloud.timo.TimoCloud.common.sockets.handler.MessageHandler;
import io.netty.channel.Channel;

import java.util.List;
import java.util.Map;

public class BaseStartServerHandler extends MessageHandler {
    public BaseStartServerHandler() {
        super(MessageType.BASE_START_SERVER);
    }

    @Override
    public void execute(Message message, Channel channel) {
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
        int timeout = ((Number) message.get("timeout")).intValue();
        TimoCloudBase.getInstance().getInstanceManager().addToServerQueue(new BaseServerObject(serverName, id, ram, isStatic, map, group, templateHash, mapHash, globalHash, javaParameters, spigotParameters, jrePath, timeout));
        TimoCloudBase.getInstance().info("Added server " + serverName + " to queue.");
    }
}
