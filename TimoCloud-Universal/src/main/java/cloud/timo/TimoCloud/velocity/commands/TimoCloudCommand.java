package cloud.timo.TimoCloud.velocity.commands;

import cloud.timo.TimoCloud.common.protocol.Message;
import cloud.timo.TimoCloud.common.protocol.MessageType;
import cloud.timo.TimoCloud.common.utils.ChatColorUtil;
import cloud.timo.TimoCloud.velocity.TimoCloudVelocity;
import cloud.timo.TimoCloud.velocity.managers.VelocityMessageManager;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class TimoCloudCommand implements SimpleCommand {

    private Map<String, Invocation> senders;

    public TimoCloudCommand() {
        senders = new HashMap<>();
    }

    @Override
    public void execute(Invocation invocation) {
        try {
            String[] args = invocation.arguments();
            if (args.length < 1) {
                sendVersion(invocation);
                return;
            }
            if (!invocation.source().hasPermission("timocloud.admin")) {
                VelocityMessageManager.noPermission(invocation);
                return;
            }
            if (args[0].equalsIgnoreCase("reload")) {
                TimoCloudVelocity.getInstance().getFileManager().load();
                VelocityMessageManager.sendMessage(invocation, "&aSuccessfully reloaded from configuration!");
                // Do not return because we want to reload the Core configuration as well
            }
            if (args[0].equalsIgnoreCase("version")) {
                sendVersion(invocation);
                return;
            }
            String command = Arrays.stream(args).collect(Collectors.joining(" "));
            String sendername = "console";
            if (invocation.source() instanceof Player) {
                sendername = ((Player) invocation.source()).getUsername();
            }
            senders.put(sendername, invocation);

            TimoCloudVelocity.getInstance().getSocketClientHandler().sendMessage(Message.create()
                    .setType(MessageType.CORE_PARSE_COMMAND)
                    .setData(command)
                    .set("sender", sendername)
                    .toString());
        } catch (Exception e) {
            invocation.source().sendMessage(Component.text(ChatColorUtil.translateAlternateColorCodes('&', "&cAn error occured while exeuting command. Please see console for more details.")));
            TimoCloudVelocity.getInstance().severe(e);
        }
    }

    private void sendVersion(Invocation sender) {
        VelocityMessageManager.sendMessage(sender, "&bTimoCloud Version &e[&6" + TimoCloudVelocity.getInstance().getServer().getPluginManager().getPlugin("timocloud").get().getDescription().getVersion().get() + "&e] &bby &6TimoCrafter");
    }

    public void sendMessage(String senderName, String message) {
        if (getSender(senderName) == null) return;
        getSender(senderName).source().sendMessage(Component.text(ChatColorUtil.translateAlternateColorCodes('&', message)));
    }

    private Invocation getSender(String name) {
        if (!senders.containsKey(name)) return null;
        return senders.get(name);
    }
}
