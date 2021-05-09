package cloud.timo.TimoCloud.velocity.commands;

import cloud.timo.TimoCloud.velocity.TimoCloudVelocity;
import cloud.timo.TimoCloud.velocity.managers.VelocityMessageManager;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;

public class LobbyCommand implements SimpleCommand {

    @Override
    public void execute(Invocation invocation) {
        if (!(invocation.source() instanceof Player)) {
            VelocityMessageManager.sendMessage(invocation, "&cThis command is only for players!");
            return;
        }
        Player player = (Player) invocation.source();
        RegisteredServer serverInfo = TimoCloudVelocity.getInstance().getLobbyManager().searchFreeLobby(player.getUniqueId(), player.getCurrentServer().get().getServer().getServerInfo());

        if (serverInfo == null) {
            VelocityMessageManager.sendMessage(invocation, TimoCloudVelocity.getInstance().getFileManager().getMessages().getString("NoFreeLobbyFound"));
            return;
        }
        player.createConnectionRequest(serverInfo).fireAndForget();
        if (TimoCloudVelocity.getInstance().getFileManager().getConfig().getBoolean("sendLobbyCommandMessage"))
            VelocityMessageManager.sendMessage(invocation, TimoCloudVelocity.getInstance().getFileManager().getMessages().getString("LobbyConnect")
                    .replace("{server_name}", serverInfo.getServerInfo().getName()));
    }
}