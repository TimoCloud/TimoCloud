package cloud.timo.TimoCloud.bukkit.commands;

import cloud.timo.TimoCloud.bukkit.managers.BukkitMessageManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class SendBungeeCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (! sender.equals(Bukkit.getConsoleSender())) {
            BukkitMessageManager.sendMessage(sender, "&cThis only makes sense if you are not a player. Players commands are proxied through bungeecord, so you don't need this function.");
            return false;
        }
        String cmd = "";
        for (int i = 0; i<args.length; i++) {
            cmd += args[i] + " ";
        }
        cmd = cmd.trim();
        //TimoCloudAPI.getBukkitInstance().sendCommandToBungeeCord(cmd); //TODO readd
        return false;
    }
}
