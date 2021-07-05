package cloud.timo.TimoCloud.core.managers;

import cloud.timo.TimoCloud.api.core.commands.CommandHandler;
import cloud.timo.TimoCloud.api.core.commands.CommandSender;
import cloud.timo.TimoCloud.common.utils.ChatColorUtil;
import cloud.timo.TimoCloud.core.TimoCloudCore;
import cloud.timo.TimoCloud.core.commands.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CommandManager {

    private final Map<String, CommandHandler> commandHandlers = new HashMap<>();
    private final Map<String, CommandHandler> pluginCommandHandlers = new HashMap<>();

    public CommandManager() {
        registerDefaultCommands();
    }

    public void registerPluginCommandHandler(String command, CommandHandler commandHandler) {
        pluginCommandHandlers.put(command.toLowerCase(), commandHandler);
    }

    public void registerCommandHandler(String command, CommandHandler commandHandler) {
        commandHandlers.put(command.toLowerCase(), commandHandler);
    }

    public void unregisterCommandHandler(String command) {
        commandHandlers.remove(command.toLowerCase());
        pluginCommandHandlers.remove(command.toLowerCase());
    }

    private CommandHandler getHandlerByCommand(String command) {
        if (pluginCommandHandlers.containsKey(command.toLowerCase())) {
            return pluginCommandHandlers.get(command.toLowerCase());
        }
        return commandHandlers.get(command.toLowerCase());
    }

    private void registerCommand(CommandHandler commandHandler, String ... commands) {
        for (String command : commands) {
            registerCommandHandler(command, commandHandler);
        }
    }

    private void registerDefaultCommands() {
        registerCommand(new BaseInfoCommandHandler(), "baseinfo", "base");
        registerCommand(new CreateGroupCommandHandler(), "creategroup", "addgroup");
        registerCommand(new DebugCommandHandler(), "debug");
        registerCommand(new DeleteGroupCommand(), "deletegroup", "removegroup");
        registerCommand(new EditGroupCommandHandler(), "editgroup");
        registerCommand(new GroupInfoCommandHandler(), "groupinfo", "group");
        registerCommand(new HelpCommandHandler(), "help", "?");
        registerCommand(new ListBasesCommand(), "listbases", "bases", "showbases");
        registerCommand(new ListGroupsCommandHandler(), "listgroups", "groups", "showgroups");
        registerCommand(new ReloadCommandHandler(), "reload");
        registerCommand(new ReloadPluginsCommandHandler(), "reloadPlugins");
        registerCommand(new RestartCommandHandler(), "stop", "restart", "restartgroup");
        registerCommand(new SendCommandCommandHandler(), "sendcommand", "executecommand", "send");
        registerCommand(new ShutdownCommandHandler(), "shutdown", "end", "quit", "exit");
        registerCommand(new VersionCommandHandler(), "version", "info");
        registerCommand(new AddBaseCommandHandler(), "addbase");
    }

    public void sendHelp(CommandSender sender) {
        getHandlerByCommand("help").onCommand("help", sender);
    }

    private void sendMessage(String message) {
        TimoCloudCore.getInstance().info(ChatColorUtil.toLegacyText(message));
    }

    private void sendError(String message) {
        TimoCloudCore.getInstance().severe(message);
    }

    public void onCommand(String command) {
        onCommand(command, new CommandSender() {
            @Override
            public void sendMessage(String message) {
                CommandManager.this.sendMessage(message + "&r");
            }

            @Override
            public void sendError(String message) {
                CommandManager.this.sendError(message);
            }
        });
    }

    public void onCommand(String command, CommandSender sender) {
        String[] split = command.split(" ");
        if (split.length < 1) return;
        String cmd = split[0];
        String[] args = split.length == 1 ? new String[0] : Arrays.copyOfRange(split, 1, split.length);
        onCommand(cmd, sender, args);
    }

    public void onCommand(String command, CommandSender commandSender, String ... args) {
        try {
            CommandHandler handler = getHandlerByCommand(command);
            if (handler == null) handler = getHandlerByCommand("help");

            handler.onCommand(command, commandSender, args);
        } catch (Exception e) {
            commandSender.sendError("An error occurred while executing the command. Please look up in the console for more details.");
            TimoCloudCore.getInstance().severe(e);
            sendHelp(commandSender);
        }
    }

    public Map<String, CommandHandler> getPluginCommandHandlers() {
        return pluginCommandHandlers;
    }
}
