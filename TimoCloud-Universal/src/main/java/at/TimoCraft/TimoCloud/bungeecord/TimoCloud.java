package at.TimoCraft.TimoCloud.bungeecord;


import at.TimoCraft.TimoCloud.bungeecord.commands.LobbyCommand;
import at.TimoCraft.TimoCloud.bungeecord.commands.TimoCloudCommand;
import at.TimoCraft.TimoCloud.bungeecord.listeners.LobbyJoin;
import at.TimoCraft.TimoCloud.bungeecord.listeners.ServerKick;
import at.TimoCraft.TimoCloud.bungeecord.managers.FileManager;
import at.TimoCraft.TimoCloud.bungeecord.managers.ServerManager;
import at.TimoCraft.TimoCloud.bungeecord.sockets.BungeeSocketServer;
import at.TimoCraft.TimoCloud.bungeecord.sockets.BungeeSocketServerHandler;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.concurrent.TimeUnit;
import java.util.List;

/**
 * Created by Timo on 26.12.16.
 */
public class TimoCloud extends Plugin {

    private static TimoCloud instance;
    private ServerManager serverManager;
    private FileManager fileManager;
    private BungeeSocketServer socketServer;
    private BungeeSocketServerHandler socketServerHandler;
    private String prefix;
    private boolean shuttingDown = false;

    public static void info(String message) {
        getInstance().getLogger().info(" " + message);
    }

    public static void severe(String message) {
        getInstance().getLogger().severe(" " + message);
    }

    @Override
    public void onEnable() {
        instance = this;
        getProxy().getScheduler().schedule(this, () -> enableDelayed(), 1, 0, TimeUnit.SECONDS);
    }

    private void enableDelayed() {
        makeInstances();
        registerCommands();
        registerListeners();
        registerTasks();
        getServerManager().init();

        info("Successfully started TimoCloud!");
    }

    @Override
    public void onDisable() {
        setShuttingDown(true);
        getServerManager().stopAllServers();
        info("Successfully stopped TimoCloud!");
    }

    private void makeInstances() {
        fileManager = new FileManager();
        serverManager = new ServerManager();
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
        getProxy().getScheduler().schedule(this, () -> everySecond(), 1L, 1L, TimeUnit.SECONDS);
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

    public ServerManager getServerManager() {
        return serverManager;
    }

    public FileManager getFileManager() {
        return fileManager;
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
