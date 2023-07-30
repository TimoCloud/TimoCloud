package cloud.timo.TimoCloud.base.sockets.handler;

import cloud.timo.TimoCloud.base.TimoCloudBase;
import cloud.timo.TimoCloud.base.objects.BaseProxyObject;
import cloud.timo.TimoCloud.common.protocol.Message;
import cloud.timo.TimoCloud.common.protocol.MessageType;
import cloud.timo.TimoCloud.common.sockets.handler.MessageHandler;
import io.netty.channel.Channel;

import java.util.List;
import java.util.Map;

public class BaseStartProxyHandler extends MessageHandler {
    public BaseStartProxyHandler() {
        super(MessageType.BASE_START_PROXY);
    }

    @Override
    public void execute(Message message, Channel channel) {
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
    }
}
