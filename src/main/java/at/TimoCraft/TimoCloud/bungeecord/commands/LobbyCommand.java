package at.TimoCraft.TimoCloud.bungeecord.commands;

import at.TimoCraft.TimoCloud.bungeecord.TimoCloud;
import at.TimoCraft.TimoCloud.bungeecord.managers.MessageManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

/**
 * Created by Timo on 28.12.16.
 */
public class LobbyCommand extends Command {

    public LobbyCommand() {
        super("lobby", "timocloud.lobby", "hub");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (! (sender instanceof ProxiedPlayer)) {
            MessageManager.sendMessage(sender, "&cThis command is only for players!");
        }
        ProxiedPlayer player = (ProxiedPlayer) sender;
        player.connect(TimoCloud.getInstance().getServerManager().getRandomLobbyServer(null));
    }
}
