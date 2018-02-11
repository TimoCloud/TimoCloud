package cloud.timo.TimoCloud.bungeecord.commands;

import cloud.timo.TimoCloud.bungeecord.TimoCloudBungee;
import cloud.timo.TimoCloud.bungeecord.managers.BungeeMessageManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class LobbyCommand extends Command {

    public LobbyCommand(String command, String[] aliases) {
        super(command, null, aliases);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            BungeeMessageManager.sendMessage(sender, "&cThis command is only for players!");
            return;
        }
        ProxiedPlayer player = (ProxiedPlayer) sender;
        ServerInfo serverInfo = TimoCloudBungee.getInstance().getLobbyManager().searchFreeLobby(player.getUniqueId(), player.getServer().getInfo());
        if (serverInfo != null) player.connect(serverInfo);
    }
}
