package cloud.timo.TimoCloud.bungeecord.commands;

import cloud.timo.TimoCloud.bungeecord.TimoCloudBungee;
import cloud.timo.TimoCloud.bungeecord.managers.BungeeMessageManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.PluginDescription;

public class TimoCloudCommand extends Command {

    public TimoCloudCommand() {
        super("TimoCloudBungee");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

    }

/**
    @Override
    public void execute(CommandSender sender, String[] args) {
        try {
            if (args.length < 1) {
                sendVersion(sender);
                return;
            }

            if (args[0].equalsIgnoreCase("check")) {
                String user = "%%__USER__%%", nonce = "%%__NONCE__%%";
                if (user.startsWith("%%")) {
                    BungeeMessageManager.sendMessage(sender, "&cNot downloaded from spigotmc.org.");
                    return;
                }
                BungeeMessageManager.sendMessage(sender, "&6Downloaded by &ehttps://www.spigotmc.org/members/" + user + "/");
                BungeeMessageManager.sendMessage(sender, "&b" + nonce);
                return;
            }

            if (!sender.hasPermission("timocloud.admin")) {
                BungeeMessageManager.noPermission(sender);
                return;
            }
            if (args[0].equalsIgnoreCase("reload")) {
                TimoCloudBungee.getInstance().getFileManager().load();
                TimoCloudBungee.getInstance().getServerManager().loadGroups();
                BungeeMessageManager.sendMessage(sender, "&aSuccessfully reloaded from configuration!");
                return;
            }
            if (args[0].equalsIgnoreCase("version")) {
                sendVersion(sender);
                return;
            }
            if (args[0].equalsIgnoreCase("help")) {
                sendHelp(sender);
                return;
            }

            if (args[0].equalsIgnoreCase("listgroups")) {
                List<ServerGroup> groups = TimoCloudBungee.getInstance().getServerManager().getGroups();
                if (groups.size() == 0) {
                    BungeeMessageManager.sendMessage(sender, "&cNo groups yet!");
                    return;
                }
                BungeeMessageManager.sendMessage(sender, "&6Groups (" + groups.size() + "):");
                for (ServerGroup group : groups) {
                    displayGroup(group, sender);
                }
                return;
            }
            if (args[0].equalsIgnoreCase("removegroup")) {
                String name = args[1];
                try {
                    ServerGroup group = TimoCloudBungee.getInstance().getServerManager().getGroupByName(name);
                    if (group == null) {
                        BungeeMessageManager.sendMessage(sender, "&cThe group &e" + name + "&c does not exist. Type &b/timocloud listgroups &cfor a list of all groups.");
                        return;
                    }
                    TimoCloudBungee.getInstance().getServerManager().removeGroup(group);
                    BungeeMessageManager.sendMessage(sender, "&aSuccessfully deleted group &e" + name);
                } catch (Exception e) {
                    BungeeMessageManager.sendMessage(sender, "&cError while saving groups.yml. See console for mor information.");
                    e.printStackTrace();
                }
                return;
            }
            if (args[0].equalsIgnoreCase("restartgroup")) {
                ServerGroup group = TimoCloudBungee.getInstance().getServerManager().getGroupByName(args[1]);
                if (group == null) {
                    BungeeMessageManager.sendMessage(sender, "&cGroup &e" + args[1] + " &cdoes not exist.");
                    return;
                }
                group.stopAllServers();
                BungeeMessageManager.sendMessage(sender, "&aAll servers of group &e" + group.getName() + "&a have successfully been restarted.");
                return;
            }
            if (args[0].equalsIgnoreCase("stopserver") || args[0].equalsIgnoreCase("restartserver")) {
                Server server = TimoCloudBungee.getInstance().getServerManager().getServerByName(args[1]);
                if (server == null) {
                    BungeeMessageManager.sendMessage(sender, "&cServer &e" + args[1] + " &cdoes not exist.");
                    return;
                }
                server.stop();
                BungeeMessageManager.sendMessage(sender, "&aServer &e" + server.getName() + "&a has successfully been stopped.");
                return;
            }
            if (args[0].equalsIgnoreCase("sendcommand")) {
                Server server = TimoCloudBungee.getInstance().getServerManager().getServerByName(args[1]);
                ServerGroup group = TimoCloudBungee.getInstance().getServerManager().getGroupByName(args[1]);
                if (group == null && server == null) {
                    BungeeMessageManager.sendMessage(sender, "&cServer or group &e" + args[1] + " &cdoes not exist.");
                    return;
                }
                String command = "";
                for (int i = 2; i<args.length; i++) {
                    command += args[i] + " ";
                }
                command = command.trim();
                if (command.length() == 0) {
                    BungeeMessageManager.sendMessage(sender, "&cPlease provide a command.");
                    sendHelp(sender);
                    return;
                }
                List<Server> servers = group == null ? Collections.singletonList(server) : group.getServers();
                for (Server server1 : servers) {
                    server1.executeCommand(command);
                }
                BungeeMessageManager.sendMessage(sender, "&aCommand &e/" + command + "&a has successfully been executed on the following servers:");
                for (Server server1 : servers) {
                    BungeeMessageManager.sendMessage(sender, "&b    " + server1.getName());
                }
                return;
            }
            if (args[0].equalsIgnoreCase("addgroup")) {
                String name = args[1];
                int amount = Integer.parseInt(args[2]);
                int maxAmount = Integer.parseInt(args[3]);
                int ram = Integer.parseInt(args[4]);
                boolean isStatic = Boolean.parseBoolean(args[5]);
                String base = args[6];

                if (TimoCloudBungee.getInstance().getServerManager().getGroupByExactName(name) != null) {
                    BungeeMessageManager.sendMessage(sender, "&cThis group already exists.");
                    return;
                }

                ServerGroup group = new ServerGroup(name, amount, maxAmount, ram, isStatic, base, Arrays.asList("OFFLINE", "STARTING", "INGAME"));

                try {
                    TimoCloudBungee.getInstance().getServerManager().saveGroup(group);
                    BungeeMessageManager.sendMessage(sender, "&aGroup &e" + group.getName() + "&a has successfully been created.");
                    displayGroup(group, sender);
                } catch (Exception e) {
                    BungeeMessageManager.sendMessage(sender, "&cError while saving groups.yml. See console for mor information.");
                    e.printStackTrace();
                }
                return;
            }
            if (args[0].equalsIgnoreCase("editgroup")) {
                String groupName = args[1];
                ServerGroup group = TimoCloudBungee.getInstance().getServerManager().getGroupByName(groupName);
                if (group == null) {
                    BungeeMessageManager.sendMessage(sender, "&cGroup &e" + groupName + " &cnot found. Get a list of all groups with §e/timocloud listgroups");
                    return;
                }
                String key = args[2];
                switch (key.toLowerCase()) {
                    case "onlineamount":
                        group.setOnlineAmount(Integer.parseInt(args[3]));
                        TimoCloudBungee.getInstance().getServerManager().saveGroup(group);
                        break;
                    case "maxamount":
                        group.setMaxAmount(Integer.parseInt(args[3]));
                        TimoCloudBungee.getInstance().getServerManager().saveGroup(group);
                        break;
                    case "base":
                        String baseName = args[3];
                        Base base = TimoCloudBungee.getInstance().getServerManager().getBase(args[3]);
                        if (base == null) {
                            BungeeMessageManager.sendMessage(sender, "&cBase &e" + baseName + "&c is not connected or does not exist.");
                            return;
                        }
                        group.setBase(base);
                        TimoCloudBungee.getInstance().getServerManager().saveGroup(group);
                        break;
                    case "ram":
                        int ram = Integer.parseInt(args[3]);
                        group.setRam(ram);
                        TimoCloudBungee.getInstance().getServerManager().saveGroup(group);
                        break;
                    case "static":
                        boolean isStatic = Boolean.parseBoolean(args[3]);
                        group.setStatic(isStatic);
                        TimoCloudBungee.getInstance().getServerManager().saveGroup(group);
                        break;
                    default:
                        BungeeMessageManager.sendMessage(sender, "&cNo valid argument found. Please use \n" +
                                "/TimoCloudBungee editgroup <name> <onlineAmount (int), base (String), ram (int), static (boolean)> <value>");
                        return;
                }
                BungeeMessageManager.sendMessage(sender, "&aGroup &e" + group.getName() + "&a has successfully been edited. New data: ");
                displayGroup(group, sender);
                return;
            }
            sendHelp(sender);
        } catch (Exception e) {
            sendHelp(sender);
        }
    }

    private void displayGroup(ServerGroup group, CommandSender sender) {
        BungeeMessageManager.sendMessage(sender,
                "  &b" + group.getName() +
                        " &e(&7RAM: &6" + group.getRam() + (group.getRam() < 128 ? "G" : "M") +
                        "&e, &7Keep-Online-Amount: &6" + group.getOnlineAmount() +
                        "&e, &7Max-Amount: &6" + group.getMaxAmount() +
                        "&e, &7static: &6" + group.isStatic() +
                        "&e)");

        BungeeMessageManager.sendMessage(sender, "  &3Servers: &6" + group.getServers().size());
        for (Server server : group.getServers()) {
            BungeeMessageManager.sendMessage(sender, "    &b" + server.getName() +
                    " &b(&6State: &e" + server.getState() + "&b) " +
                    (server.getMap() == null || server.getMap().equals("") ? "" : (" &b(&6Map: &e" + server.getMap() + "&b)")));
        }
    }
*/
    private void sendVersion(CommandSender sender) {
        PluginDescription description = TimoCloudBungee.getInstance().getDescription();
        BungeeMessageManager.sendMessage(sender, "&bTimoCloud Version &e[&6" + description.getVersion() + "&e] &bby &6TimoCrafter");
    }

    public void sendHelp(CommandSender sender) {
        BungeeMessageManager.sendMessage(sender, "&6Available commands for &bTimoCloud&6:");
        BungeeMessageManager.sendMessage(sender, "  &a/TimoCloudBungee help &7- shows this page");
        BungeeMessageManager.sendMessage(sender, "  &a/TimoCloudBungee version &7- shows the plugin version");
        BungeeMessageManager.sendMessage(sender, "  &a/TimoCloudBungee reload &7- reloads all configs");
        BungeeMessageManager.sendMessage(sender, "  &a/TimoCloudBungee addgroup <groupName (String)> <startupAmount (int)> <maxAmount (int)> <ram (int)> <static (boolean), use false if you don't know what you want> <base (String)> &7- creates a group, please make sure you created a template with the group name in the specific base");
        BungeeMessageManager.sendMessage(sender, "  &a/TimoCloudBungee removegroup <groupName> &7- deletes a group");
        BungeeMessageManager.sendMessage(sender, "  &a/TimoCloudBungee editgroup <name> <onlineAmount (int), maxAmount (int), base (String), ram (int), static (boolean)> <value>");
        BungeeMessageManager.sendMessage(sender, "  &a/TimoCloudBungee listgroups &7- lists all groups and started servers");
        BungeeMessageManager.sendMessage(sender, "  &a/TimoCloudBungee restartgroup <groupName> &7- restarts all servers in a given group");
        BungeeMessageManager.sendMessage(sender, "  &a/TimoCloudBungee restartserver <serverName> &7- restarts the given server");
        BungeeMessageManager.sendMessage(sender, "  &a/TimoCloudBungee sendcommand <groupName/serverName> <command> &7- sends the given command to all server of a given group or the given server");
    }
}
