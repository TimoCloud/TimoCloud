package cloud.timo.TimoCloud.velocity.commands;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.objects.PlayerObject;
import cloud.timo.TimoCloud.common.utils.ChatColorUtil;
import cloud.timo.TimoCloud.velocity.TimoCloudVelocity;
import com.velocitypowered.api.command.SimpleCommand;
import net.kyori.text.TextComponent;

public class FindCommand implements SimpleCommand {

    @Override
    public void execute(Invocation invocation) {
        String[] args = invocation.arguments();
        if (args.length == 0) {
            sendMessage(invocation, "Please specify the name of the player you want to find");
            return;
        }
        PlayerObject playerObject = TimoCloudAPI.getUniversalAPI().getPlayer(args[0]);
        if (playerObject == null) {
            sendMessage(invocation, "&cThe player '&e" + args[0] + "&c' is not online.");
            return;
        }
        sendMessage(invocation, "&e" + playerObject.getName() + " &ais online at &6" + playerObject.getServer().getName());
    }

    private static void sendMessage(Invocation sender, String message) {
        sender.source().sendMessage(TextComponent.builder(TimoCloudVelocity.getInstance().getPrefix()).content(ChatColorUtil.translateAlternateColorCodes('&', message)).build());
    }
}
