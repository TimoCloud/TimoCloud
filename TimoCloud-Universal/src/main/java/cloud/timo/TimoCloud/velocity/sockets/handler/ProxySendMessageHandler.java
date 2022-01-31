package cloud.timo.TimoCloud.velocity.sockets.handler;

import cloud.timo.TimoCloud.bungeecord.TimoCloudBungee;
import cloud.timo.TimoCloud.common.protocol.Message;
import cloud.timo.TimoCloud.common.protocol.MessageType;
import cloud.timo.TimoCloud.common.sockets.handler.MessageHandler;
import cloud.timo.TimoCloud.velocity.TimoCloudVelocity;
import com.velocitypowered.api.proxy.Player;
import io.netty.channel.Channel;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.chat.ComponentSerializer;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class ProxySendMessageHandler extends MessageHandler {
    public ProxySendMessageHandler() {
        super(MessageType.PROXY_SEND_MESSAGE);
    }

    @Override
    public void execute(Message message, Channel channel) {
        Map<String, Object> information = (Map<String, Object>) message.getData();
        String playerUUID = (String) information.get("playerUUID");
        String chatMessage = (String) information.get("chatMessage");
        Optional<Player> player = TimoCloudVelocity.getInstance().getServer().getPlayer(UUID.fromString(playerUUID));
        player.ifPresent(it -> it.sendMessage(Component.text(chatMessage)));
    }
}
