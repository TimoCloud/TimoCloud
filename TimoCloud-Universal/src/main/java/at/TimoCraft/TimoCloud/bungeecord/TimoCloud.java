package at.TimoCraft.TimoCloud.bungeecord;


import at.TimoCraft.TimoCloud.api.TimoCloudAPI;
import at.TimoCraft.TimoCloud.bungeecord.api.TimoCloudUniversalAPIBungeeImplementation;
import at.TimoCraft.TimoCloud.bungeecord.commands.LobbyCommand;
import at.TimoCraft.TimoCloud.bungeecord.commands.TimoCloudCommand;
import at.TimoCraft.TimoCloud.bungeecord.listeners.LobbyJoin;
import at.TimoCraft.TimoCloud.bungeecord.listeners.ServerKick;
import at.TimoCraft.TimoCloud.bungeecord.managers.BungeeFileManager;
import at.TimoCraft.TimoCloud.bungeecord.managers.BungeeServerManager;
import at.TimoCraft.TimoCloud.bungeecord.managers.LobbyManager;
import at.TimoCraft.TimoCloud.bungeecord.sockets.BungeeSocketServer;
import at.TimoCraft.TimoCloud.bungeecord.sockets.BungeeSocketServerHandler;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class TimoCloud extends Plugin {

    private static TimoCloud instance;
    private BungeeFileManager fileManager;
    private BungeeServerManager serverManager;
    private LobbyManager lobbyManager;
    private BungeeSocketServer socketServer;
    private BungeeSocketServerHandler socketServerHandler;
    private String prefix;
    private boolean shuttingDown = false;

    public static void info(String message) {
        getInstance().getLogger().info(" " + ChatColor.translateAlternateColorCodes('&', message));
    }

    public static void severe(String message) {
        getInstance().getLogger().severe(" " + ChatColor.translateAlternateColorCodes('&', message));
    }

    @Override
    public void onEnable() {
        instance = this;
        info("&eEnabling &bTimoCloud &eversion &7[&6" + getDescription().getVersion() + "&7]&e...");
        getProxy().getScheduler().schedule(this, this::enableDelayed, 1, 0, TimeUnit.SECONDS);
    }

    private void enableDelayed() {
        makeInstances();
        registerCommands();
        registerListeners();
        registerTasks();
        getServerManager().init();
        TimoCloudAPI.setUniversalImplementation(new TimoCloudUniversalAPIBungeeImplementation());

        info("&cPlease note that using TimoCloud without having bought it before is a crime. Do not give TimoCloud to anybody else, because you are responsible for everybody who uses the plugin with your download ID.");
        info("&aSuccessfully started TimoCloud!");
    }

    @Override
    public void onDisable() {
        setShuttingDown(true);
        getServerManager().stopAllServers();
        info("Successfully stopped TimoCloud!");
    }

    private void makeInstances() {
        fileManager = new BungeeFileManager();
        lobbyManager = new LobbyManager();
        serverManager = new BungeeServerManager();
        socketServer = new BungeeSocketServer();
        socketServerHandler = new BungeeSocketServerHandler();
    }

    private void registerCommands() {
        getProxy().getPluginManager().registerCommand(this, new TimoCloudCommand());
        List<String> lobbyCommands = getFileManager().getConfig().getStringList("lobbyCommands");
        if (lobbyCommands.size() > 0) {
            String[] aliases = lobbyCommands.subList(1, lobbyCommands.size()).toArray(new String[0]);
            getProxy().getPluginManager().registerCommand(this, new LobbyCommand(lobbyCommands.get(0), aliases));
        }
    }

    private void registerTasks() {
        getProxy().getScheduler().runAsync(this, () -> {
            info("Starting socket-server...");
            try {
                socketServer.init("0.0.0.0", getFileManager().getConfig().getInt("socket-port"));
            } catch (Exception e) {
                severe("Error while initializing socketServer:");
                e.printStackTrace();
            }
        });
        getProxy().getScheduler().schedule(this, this::everySecond, 1L, 1L, TimeUnit.SECONDS);
    }

    private void everySecond() {
        getServerManager().checkEnoughOnline();
    }

    private void registerListeners() {
        getProxy().getPluginManager().registerListener(this, new LobbyJoin());
        getProxy().getPluginManager().registerListener(this, new ServerKick());
    }

    public static TimoCloud getInstance() {
        return instance;
    }

    public BungeeFileManager getFileManager() {
        return fileManager;
    }

    public BungeeServerManager getServerManager() {
        return serverManager;
    }

    public LobbyManager getLobbyManager() {
        return lobbyManager;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public BungeeSocketServer getSocketServer() {
        return socketServer;
    }

    public boolean isShuttingDown() {
        return shuttingDown;
    }

    public void setShuttingDown(boolean shuttingDown) {
        this.shuttingDown = shuttingDown;
    }

    public BungeeSocketServerHandler getSocketServerHandler() {
        return socketServerHandler;
    }


}
