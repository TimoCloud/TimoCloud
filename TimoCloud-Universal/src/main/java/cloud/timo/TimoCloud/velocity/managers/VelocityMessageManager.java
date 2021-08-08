package cloud.timo.TimoCloud.velocity.managers;

import cloud.timo.TimoCloud.common.utils.ChatColorUtil;
import cloud.timo.TimoCloud.velocity.TimoCloudVelocity;
import com.velocitypowered.api.command.SimpleCommand;
import net.kyori.adventure.text.Component;

public class VelocityMessageManager {

    public static void sendMessage(SimpleCommand.Invocation sender, String message) {
        if (message.isEmpty()) return;
        sender.source().sendMessage(Component.text(TimoCloudVelocity.getInstance().getPrefix()).content(ChatColorUtil.translateAlternateColorCodes('&', message)));
    }

    public static void noPermission(SimpleCommand.Invocation sender) {
        sendMessage(sender, "&cYou don't have any permission to do that!");
    }

    public static void onlyForPlayers(SimpleCommand.Invocation sender) {
        sendMessage(sender, "&cThis command is only for players!");
    }

}
