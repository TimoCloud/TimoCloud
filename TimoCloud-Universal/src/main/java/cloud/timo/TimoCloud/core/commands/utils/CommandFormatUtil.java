package cloud.timo.TimoCloud.core.commands.utils;

import cloud.timo.TimoCloud.api.core.commands.CommandSender;
import cloud.timo.TimoCloud.core.objects.*;

import java.util.Objects;

import java.net.InetAddress;

public class CommandFormatUtil {

    public void displayGroup(ServerGroup group, CommandSender sender) {
        sender.sendMessage("  &e" + group.getName() +
                " &7(&6RAM&7: &2" + group.getRam() + "MB" +
                "&7, &6Keep-Online-Amount&7: &2" + group.getOnlineAmount() +
                "&7, &6Max-Amount&7: &2" + group.getMaxAmount() +
                "&7, &6static&7: &2" + group.isStatic() +
                "&7)");
        sender.sendMessage("  &6Servers&7: &2" + group.getServers().size());
        for (Server server : group.getServers()) {
            sender.sendMessage("    &e" + server.getName() +
                    " &7(&6Base&7: &2" + server.getBase().getName() + "&7) " +
                    " &7(&6State&7: " + server.getState() + "&7) " +
                    (server.getMap() == null || server.getMap().equals("") ? "" : (" &7(&6Map&7: &e" + server.getMap() + "&7)")));
        }
    }

    public void displayGroup(ProxyGroup group, CommandSender sender) {
        sender.sendMessage("  &e" + group.getName() +
                " &7(&6RAM&7: &2" + group.getRam() + "MB" +
                "&7, &6Current-Online-Players&7: &2" + group.getOnlinePlayerCount() +
                "&7, &6Total-Max-Players&7: &2" + group.getMaxPlayerCount() +
                "&7, &6Players-Per-Proxy&7: &2" + group.getMaxPlayerCountPerProxy() +
                "&7, &6Keep-Free-Slots&7: &2" + group.getKeepFreeSlots() +
                "&7, &6Max-Amount&7: &2" + group.getMaxAmount() +
                "&7, &6Min-Amount&7: &2" + group.getMinAmount() +
                "&7, &6Priority&7: &2" + group.getPriority() +
                "&7, &6static&7: &2" + group.isStatic() +
                "&7)");
        sender.sendMessage("  &6Proxies&7: &2" + group.getProxies().size());
        for (Proxy proxy : group.getProxies()) {
            sender.sendMessage("    " + proxy.getName() +
                    " &7(&6Base&7: &2" + proxy.getBase().getName() + "&7) " +
                    " &7(&6Players&7: &2" + proxy.getOnlinePlayerCount() + "&7) ");
        }
    }

    public void displayBase(Base base, CommandSender sender) {
        sender.sendMessage("  &e" + base.getName() + " " +
                (base.isConnected() ? "&aConnected" : "&cNot connected") +
                "&7, (&6Free RAM&7: &2" + base.getAvailableRam() + "MB" +
                "&7, &6Max RAM&7: &2" + base.getMaxRam() + "MB" +
                "&7, &6CPU load&7: &2" + (int) base.getCpuLoad() + "%" +
                "&7, &6IP Address&7: &2" + formatIp(base.getPublicAddress()) +
                "&7, &6Ready&7: &2" + formatBoolean(base.isReady()) +
                "&7, &6Connected&7: &2" + formatBoolean(base.isConnected()) +
                "&7)");
    }

    public void displayGroup(Group group, CommandSender sender) {
        if (group instanceof ServerGroup) displayGroup((ServerGroup) group, sender);
        else if (group instanceof ProxyGroup) displayGroup((ProxyGroup) group, sender);
    }

    public void notEnoughArgs(CommandSender commandSender, String usage) {
        commandSender.sendError("Not enough arguments. Please use: " + usage);
    }

    public void invalidArgs(CommandSender commandSender, String usage) {
        commandSender.sendError("Invalid arguments. Please use: " + usage);
    }

    public static String formatBoolean(boolean b) {
        return b ? "&2true" : "&cfalse";
    }

    public static String formatIp(InetAddress ip) {
        if (ip == null) return "null";
        String s = ip.toString();
        if (s.startsWith("/")) s = s.substring(1);
        return s;
    }

}
