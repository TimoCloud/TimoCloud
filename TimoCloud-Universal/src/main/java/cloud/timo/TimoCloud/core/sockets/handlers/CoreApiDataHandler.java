package cloud.timo.TimoCloud.core.sockets.handlers;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.implementations.TimoCloudUniversalAPIBasicImplementation;
import cloud.timo.TimoCloud.api.objects.*;
import cloud.timo.TimoCloud.common.protocol.Message;
import cloud.timo.TimoCloud.common.protocol.MessageType;
import cloud.timo.TimoCloud.common.sockets.MessageHandler;
import cloud.timo.TimoCloud.core.TimoCloudCore;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.Channel;

import java.util.HashSet;
import java.util.Set;

public class CoreApiDataHandler extends MessageHandler {
    public CoreApiDataHandler() {
        super(MessageType.API_DATA);
    }

    @Override
    public void execute(Message message, Channel channel) {
        Set<String> serverGroups = new HashSet<>();
        Set<String> proxyGroups = new HashSet<>();
        Set<String> servers = new HashSet<>();
        Set<String> proxies = new HashSet<>();
        Set<String> bases = new HashSet<>();
        Set<String> players = new HashSet<>();
        Set<String> cords = new HashSet<>();
        ObjectMapper objectMapper = ((TimoCloudUniversalAPIBasicImplementation) TimoCloudAPI.getUniversalAPI()).getObjectMapper();
        try {
            for (ServerGroupObject serverGroupObject : TimoCloudAPI.getUniversalAPI().getServerGroups()) {
                serverGroups.add(objectMapper.writeValueAsString(serverGroupObject));
            }
            for (ProxyGroupObject proxyGroupObject : TimoCloudAPI.getUniversalAPI().getProxyGroups()) {
                proxyGroups.add(objectMapper.writeValueAsString(proxyGroupObject));
            }
            for (ServerObject serverObject : TimoCloudAPI.getUniversalAPI().getServers()) {
                servers.add(objectMapper.writeValueAsString(serverObject));
            }
            for (ProxyObject proxyObject : TimoCloudAPI.getUniversalAPI().getProxies()) {
                proxies.add(objectMapper.writeValueAsString(proxyObject));
            }
            for (PlayerObject playerObject : TimoCloudAPI.getUniversalAPI().getPlayers()) {
                players.add(objectMapper.writeValueAsString(playerObject));
            }
            for (BaseObject baseObject : TimoCloudAPI.getUniversalAPI().getBases()) {
                bases.add(objectMapper.writeValueAsString(baseObject));
            }
            for (CordObject cordObject : TimoCloudAPI.getUniversalAPI().getCords()) {
                cords.add(objectMapper.writeValueAsString(cordObject));
            }
            TimoCloudCore.getInstance().getSocketServerHandler().sendMessage(channel, Message.create()
                    .setType(MessageType.API_DATA)
                    .setData(
                            Message.create()
                                    .set("serverGroups", serverGroups)
                                    .set("proxyGroups", proxyGroups)
                                    .set("servers", servers)
                                    .set("proxies", proxies)
                                    .set("players", players)
                                    .set("bases", bases)
                                    .set("cords", cords)
                    ));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
