package at.TimoCraft.TimoCloud.bungeecord.commands;

import at.TimoCraft.TimoCloud.bungeecord.TimoCloud;
import at.TimoCraft.TimoCloud.bungeecord.managers.BungeeMessageManager;
import net.md_5.bungee.api.CommandSender;
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
        }
        ProxiedPlayer player = (ProxiedPlayer) sender;
        player.connect(TimoCloud.getInstance().getServerManager().getRandomLobbyServer(null));
    }
}
