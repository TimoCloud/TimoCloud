package cloud.timo.TimoCloud.bungeecord;


import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.bungeecord.api.TimoCloudUniversalAPIBungeeImplementation;
import cloud.timo.TimoCloud.bungeecord.commands.LobbyCommand;
import cloud.timo.TimoCloud.bungeecord.commands.TimoCloudCommand;
import cloud.timo.TimoCloud.bungeecord.listeners.LobbyJoin;
import cloud.timo.TimoCloud.bungeecord.listeners.ServerKick;
import cloud.timo.TimoCloud.bungeecord.managers.BungeeFileManager;
import cloud.timo.TimoCloud.bungeecord.managers.LobbyManager;
import cloud.timo.TimoCloud.bungeecord.sockets.BungeeSocketClient;
import cloud.timo.TimoCloud.bungeecord.sockets.BungeeSocketClientHandler;
import cloud.timo.TimoCloud.bungeecord.sockets.BungeeSocketMessageManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class TimoCloudBungee extends Plugin {

    private static TimoCloudBungee instance;
    private BungeeFileManager fileManager;
    private LobbyManager lobbyManager;
    private BungeeSocketClient socketClient;
    private BungeeSocketClientHandler socketClientHandler;
    private BungeeSocketMessageManager socketMessageManager;
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
        info("&eEnabling &bTimoCloudBungee &eversion &7[&6" + getDescription().getVersion() + "&7]&e...");
        getProxy().getScheduler().schedule(this, this::enableDelayed, 1, 0, TimeUnit.SECONDS);
    }

    private void enableDelayed() {
        makeInstances();
        registerCommands();
        registerListeners();
        registerTasks();
        TimoCloudAPI.setUniversalImplementation(new TimoCloudUniversalAPIBungeeImplementation());

        info("&cPlease note that using TimoCloudBungee without having bought it before is a crime. Do not give TimoCloud to anybody else, because you are responsible for everybody who uses the plugin with your download ID.");
        info("&aSuccessfully started TimoCloudBungee!");
    }

    @Override
    public void onDisable() {
        setShuttingDown(true);
        info("Successfully stopped TimoCloudBungee!");
    }

    private void makeInstances() {
        fileManager = new BungeeFileManager();
        lobbyManager = new LobbyManager();
        socketClient = new BungeeSocketClient();
        socketClientHandler = new BungeeSocketClientHandler();
        socketMessageManager = new BungeeSocketMessageManager();
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
            info("Connecting to TimoCloudCore...");

            try {
                socketClient.init(getTimoCloudCoreIP(), getTimoCloudCoreSocketPort());
            } catch (Exception e) {
                severe("Error while initializing socketServer:");
                e.printStackTrace();
            }
        });
        getProxy().getScheduler().schedule(this, this::everySecond, 1L, 1L, TimeUnit.SECONDS);
    }

    public String getTimoCloudCoreIP() {
        return System.getProperty("timocloud-corehost").split(":")[0];
    }

    public int getTimoCloudCoreSocketPort() {
        return Integer.parseInt(System.getProperty("timocloud-corehost").split(":")[1]);
    }

    public void onSocketConnect() {
        getSocketMessageManager().sendMessage("PROXY_HANDSHAKE", System.getProperty("timocloud-token"));
        everySecond();
    }

    public void onSocketDisconnect() {
        info("Disconnected from TimoCloudCore. Shutting down....");
        stop();
    }

    private void stop() {
        getProxy().stop();
    }

    private void everySecond() {
        sendEverything();
        requestApiData();
    }

    private void requestApiData() {
        getSocketMessageManager().sendMessage("GET_API_DATA", "");
    }

    private void sendEverything() {
        getSocketMessageManager().sendMessage("SET_PLAYER_COUNT", getProxy().getOnlineCount());
    }

    private void registerListeners() {
        getProxy().getPluginManager().registerListener(this, new LobbyJoin());
        getProxy().getPluginManager().registerListener(this, new ServerKick());
    }

    public String getProxyName() {
        return System.getProperty("timocloud-proxyname");
    }

    public static TimoCloudBungee getInstance() {
        return instance;
    }

    public BungeeFileManager getFileManager() {
        return fileManager;
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

    public BungeeSocketClient getSocketClient() {
        return socketClient;
    }

    public BungeeSocketClientHandler getSocketClientHandler() {
        return socketClientHandler;
    }

    public BungeeSocketMessageManager getSocketMessageManager() {
        return socketMessageManager;
    }

    public boolean isShuttingDown() {
        return shuttingDown;
    }

    public void setShuttingDown(boolean shuttingDown) {
        this.shuttingDown = shuttingDown;
    }


}
