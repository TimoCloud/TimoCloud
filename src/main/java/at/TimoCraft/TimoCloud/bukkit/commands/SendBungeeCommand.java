package at.TimoCraft.TimoCloud.bukkit.commands;

import at.TimoCraft.TimoCloud.bukkit.api.TimoCloudAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Created by Timo on 22.02.17.
 */
public class SendBungeeCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (! sender.equals(Bukkit.getConsoleSender())) {
            sender.sendMessage("§cThis makes only sense if you are no player. Players commands are proxied through bungeecord, so you don't need this function.");
            return false;
        }
        String cmd = "";
        for (int i = 0; i<args.length; i++) {
            cmd += args[i] + " ";
        }
        cmd = cmd.trim();
        TimoCloudAPI.sendCommandToBungeeCord(cmd);
        return false;
    }
}
