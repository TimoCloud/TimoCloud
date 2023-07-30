package cloud.timo.TimoCloud.bungeecord.sockets.handler;

import cloud.timo.TimoCloud.bungeecord.TimoCloudBungee;
import cloud.timo.TimoCloud.common.protocol.Message;
import cloud.timo.TimoCloud.common.protocol.MessageType;
import cloud.timo.TimoCloud.common.sockets.handler.MessageHandler;
import io.netty.channel.Channel;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class ProxySendPlayerHandler extends MessageHandler {
    public ProxySendPlayerHandler() {
        super(MessageType.PROXY_SEND_PLAYER);
    }

    @Override
    public void execute(Message message, Channel channel) {
        Map<String, Object> information = (Map<String, Object>) message.getData();
        String playerUUID = (String) information.get("playerUUID");
        String serverObject = (String) information.get("serverObject");
        ProxiedPlayer proxiedPlayer = TimoCloudBungee.getInstance().getProxy().getPlayer(UUID.fromString(playerUUID));
        if (Objects.isNull(proxiedPlayer))
            return;

        proxiedPlayer.connect(TimoCloudBungee.getInstance().getProxy().getServerInfo(serverObject));
    }
}
