package cloud.timo.TimoCloud.velocity.commands;

import cloud.timo.TimoCloud.api.objects.ServerObject;
import cloud.timo.TimoCloud.velocity.TimoCloudVelocity;
import cloud.timo.TimoCloud.velocity.managers.VelocityMessageManager;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import java.util.Optional;

public class LobbyCommand implements SimpleCommand {

    @Override
    public void execute(Invocation invocation) {
        if (!(invocation.source() instanceof Player)) {
            VelocityMessageManager.sendMessage(invocation, "&cThis command is only for players!");
            return;
        }

        Player player = (Player) invocation.source();
        final ServerObject serverObject = TimoCloudVelocity.getInstance().getLobbyManager().searchFreeLobby(player.getUniqueId(),
                player.getCurrentServer().get().getServer().getServerInfo().getName());

        if (serverObject == null) {
            VelocityMessageManager.sendMessage(invocation, TimoCloudVelocity.getInstance().getFileManager().getMessages().getString("NoFreeLobbyFound"));
            return;
        }

        final Optional<RegisteredServer> server = TimoCloudVelocity.getInstance().getServer().getServer(
                serverObject.getName());

        if (!server.isPresent()) {
            VelocityMessageManager.sendMessage(invocation, TimoCloudVelocity.getInstance().getFileManager().getMessages().getString("NoFreeLobbyFound"));
            return;
        }

        player.createConnectionRequest(server.get()).fireAndForget();
        if (TimoCloudVelocity.getInstance().getFileManager().getConfig().getBoolean("sendLobbyCommandMessage"))
            VelocityMessageManager.sendMessage(invocation, TimoCloudVelocity.getInstance().getFileManager().getMessages().getString("LobbyConnect")
                    .replace("{server_name}", server.get().getServerInfo().getName()));
    }
}