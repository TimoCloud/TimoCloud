package cloud.timo.TimoCloud.bungeecord.commands;

import cloud.timo.TimoCloud.api.objects.ServerObject;
import cloud.timo.TimoCloud.bungeecord.TimoCloudBungee;
import cloud.timo.TimoCloud.bungeecord.managers.BungeeMessageManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
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
        final ServerObject serverObject = TimoCloudBungee.getInstance().getLobbyManager().searchFreeLobby(player.getUniqueId(), player.getServer().getInfo().getName());
        if (serverObject == null) {
            BungeeMessageManager.sendMessage(sender, TimoCloudBungee.getInstance().getFileManager().getMessages().getString("NoFreeLobbyFound"));
            return;
        }

        ServerInfo serverInfo = ProxyServer.getInstance().getServerInfo(serverObject.getName());
        if (serverInfo == null) {
            BungeeMessageManager.sendMessage(sender, TimoCloudBungee.getInstance().getFileManager().getMessages().getString("NoFreeLobbyFound"));
            return;
        }

        player.connect(serverInfo);
        if (TimoCloudBungee.getInstance().getFileManager().getConfig().getBoolean("sendLobbyCommandMessage"))
            BungeeMessageManager.sendMessage(sender, TimoCloudBungee.getInstance().getFileManager().getMessages().getString("LobbyConnect")
                    .replace("{server_name}", serverInfo.getName()));
    }

}