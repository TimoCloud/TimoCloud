package cloud.timo.TimoCloud.bukkit;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.implementations.TimoCloudUniversalAPIBasicImplementation;
import cloud.timo.TimoCloud.api.implementations.managers.APIResponseManager;
import cloud.timo.TimoCloud.api.implementations.managers.EventManager;
import cloud.timo.TimoCloud.api.utils.APIInstanceUtil;
import cloud.timo.TimoCloud.bukkit.api.TimoCloudBukkitAPIImplementation;
import cloud.timo.TimoCloud.bukkit.api.TimoCloudInternalMessageAPIBukkitImplementation;
import cloud.timo.TimoCloud.bukkit.api.TimoCloudMessageAPIBukkitImplementation;
import cloud.timo.TimoCloud.bukkit.api.TimoCloudUniversalAPIBukkitImplementation;
import cloud.timo.TimoCloud.bukkit.commands.SignsCommand;
import cloud.timo.TimoCloud.bukkit.commands.TimoCloudBukkitCommand;
import cloud.timo.TimoCloud.bukkit.listeners.*;
import cloud.timo.TimoCloud.bukkit.managers.BukkitFileManager;
import cloud.timo.TimoCloud.bukkit.managers.SignManager;
import cloud.timo.TimoCloud.bukkit.managers.StateByEventManager;
import cloud.timo.TimoCloud.bukkit.sockets.BukkitSocketClient;
import cloud.timo.TimoCloud.bukkit.sockets.BukkitSocketClientHandler;
import cloud.timo.TimoCloud.bukkit.sockets.BukkitSocketMessageManager;
import cloud.timo.TimoCloud.bukkit.sockets.BukkitStringHandler;
import cloud.timo.TimoCloud.lib.logging.LoggingOutputStream;
import cloud.timo.TimoCloud.lib.messages.Message;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.PrintStream;
import java.net.InetAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TimoCloudBukkit extends JavaPlugin {

    private static TimoCloudBukkit instance;
    private BukkitFileManager fileManager;
    private BukkitSocketClientHandler socketClientHandler;
    private BukkitSocketMessageManager socketMessageManager;
    private BukkitStringHandler stringHandler;
    private SignManager signManager;
    private StateByEventManager stateByEventManager;
    private String prefix = "[TimoCloud] ";

    public void info(String message) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', getPrefix() + message));
    }

    public void warning(String message) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', getPrefix() + message));
    }

    public void severe(String message) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', getPrefix() + "&c" + message));
    }

    public void severe(Throwable throwable) {
        throwable.printStackTrace(new PrintStream(new LoggingOutputStream(this::severe)));
    }

    @Override
    public void onEnable() {
        try {
            info("&eEnabling &bTimoCloudBukkit&r &eversion &7[&6" + getDescription().getVersion() + "&7]&e...");
            makeInstances();
            registerCommands();
            registerListeners();
            registerTasks();
            registerChannel();
            Executors.newSingleThreadExecutor().submit(this::connectToCore);
            while (!((TimoCloudUniversalAPIBasicImplementation) TimoCloudAPI.getUniversalAPI()).gotAnyData()) {
                try {
                    Thread.sleep(50); // Wait until we get the API data
                } catch (Exception e) {}
            }
            info("&ahas been enabled!");
        } catch (Exception e) {
            severe("Error while enabling TimoCloudBukkit: ");
            TimoCloudBukkit.getInstance().severe(e);
        }
    }

    @Override
    public void onDisable() {
        info("&chas been disabled!");
    }

    private void connectToCore() {
        try {
            info("Connecting to TimoCloudCore socket on " + getTimoCloudCoreIP() + ":" + getTimoCloudCoreSocketPort() + "...");
            new BukkitSocketClient().init(getTimoCloudCoreIP(), getTimoCloudCoreSocketPort());
        } catch (Exception e) {
            TimoCloudBukkit.getInstance().severe(e);
        }
    }

    private void registerAtBungeeCord() {
        getSocketMessageManager().sendMessage(Message.create().setType("REGISTER").setTarget(getServerId()));
    }

    public void onSocketConnect() {
        getSocketMessageManager().sendMessage(Message.create().setType("SERVER_HANDSHAKE").setTarget(getServerId()));
    }

    public void onSocketDisconnect(boolean connectionFailed) {
        info("Disconnected from TimoCloudCore. Stopping server.");
        if (connectionFailed) {
            System.exit(0);
        } else {
            stop();
        }
    }

    public void onHandshakeSuccess() {
        getSocketMessageManager().sendMessage(Message.create().setType("SET_MAP").setData(getMapName()));
        doEverySecond();
    }

    private void stop() {
        Bukkit.getScheduler().runTask(this, () -> {
            Bukkit.shutdown();
            System.exit(0);
        });
    }

    private void makeInstances() throws Exception {
        instance = this;
        fileManager = new BukkitFileManager();
        socketClientHandler = new BukkitSocketClientHandler();
        socketMessageManager = new BukkitSocketMessageManager();
        stringHandler = new BukkitStringHandler();
        signManager = new SignManager();
        stateByEventManager = new StateByEventManager();

        APIInstanceUtil.setInternalMessageInstance(new TimoCloudInternalMessageAPIBukkitImplementation());
        APIInstanceUtil.setUniversalInstance(new TimoCloudUniversalAPIBukkitImplementation());
        APIInstanceUtil.setBukkitInstance(new TimoCloudBukkitAPIImplementation());
        APIInstanceUtil.setEventInstance(new EventManager());
        APIInstanceUtil.setMessageInstance(new TimoCloudMessageAPIBukkitImplementation());
        TimoCloudAPI.getMessageAPI().registerMessageListener(new APIResponseManager(), "TIMOCLOUD_API_RESPONSE");
    }

    private void registerCommands() {
        getCommand("signs").setExecutor(new SignsCommand());
        final TimoCloudBukkitCommand timoCloudBukkitCommand = new TimoCloudBukkitCommand();
        getCommand("timocloudbukkit").setExecutor(timoCloudBukkitCommand);
        getCommand("tcb").setExecutor(timoCloudBukkitCommand);
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new SignChange(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerInteract(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerJoin(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerQuit(), this);
        Bukkit.getPluginManager().registerEvents(new BlockPlace(), this);
    }

    private void registerChannel() {
        Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
    }

    public void sendPlayerToServer(Player player, String server) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        try {
            out.writeUTF("Connect");
            out.writeUTF(server);
        } catch (Exception e) {
            severe("Error while sending player &e" +  player + " &c to server &e" + server + "&c. Please report this: ");
            TimoCloudBukkit.getInstance().severe(e);
        }
        player.sendPluginMessage(this, "BungeeCord", out.toByteArray());
    }

    public boolean isRandomMap() {
        return Boolean.getBoolean("timocloud-randommap");
    }

    public boolean isStatic() {
        return Boolean.getBoolean("timocloud-static");
    }

    public String getTimoCloudCoreIP() {
        return System.getProperty("timocloud-corehost").split(":")[0];
    }

    public int getTimoCloudCoreSocketPort() {
        return Integer.parseInt(System.getProperty("timocloud-corehost").split(":")[1]);
    }

    public File getTemplateDirectory() {
        return new File(System.getProperty("timocloud-templatedirectory"));
    }

    public File getTemporaryDirectory() {
        return new File(System.getProperty("timocloud-temporarydirectory"));
    }

    private void doEverySecond() {
        sendEverything();
        requestApiData();
    }

    private void registerTasks() {
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, this::registerAtBungeeCord, 0L);
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(this::doEverySecond, 1L, 1L, TimeUnit.SECONDS);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            getSignManager().updateSigns();
        }, 5L, 1L);
    }

    private void sendEverything() {
        sendMotds();
        sendPlayers();
    }

    private void requestApiData() {
        getSocketMessageManager().sendMessage(Message.create().setType("GET_API_DATA"));
    }

    private void sendMotds() {
        try {
            ServerListPingEvent event = new ServerListPingEvent(InetAddress.getLocalHost(), Bukkit.getMotd(), Bukkit.getOnlinePlayers().size(), Bukkit.getMaxPlayers());
            Bukkit.getPluginManager().callEvent(event);
            getSocketMessageManager().sendMessage(Message.create().setType("SET_MOTD").setData(event.getMotd()));
            getStateByEventManager().setStateByMotd(event.getMotd().trim());
        } catch (Exception e) {
            severe("Error while sending MOTD: ");
            TimoCloudBukkit.getInstance().severe(e);
            getSocketMessageManager().sendMessage(Message.create().setType("SET_MOTD").setData(Bukkit.getMotd()));
        }
    }

    public int getOnlinePlayersAmount() {
        try {
            ServerListPingEvent event = new ServerListPingEvent(InetAddress.getLocalHost(), Bukkit.getMotd(), Bukkit.getOnlinePlayers().size(), Bukkit.getMaxPlayers());
            Bukkit.getPluginManager().callEvent(event);
            return event.getNumPlayers();
        } catch (Exception e) {
            severe("Error while calling ServerListPingEvent: ");
            TimoCloudBukkit.getInstance().severe(e);
            return Bukkit.getOnlinePlayers().size();
        }
    }

    public int getMaxPlayersAmount() {
        try {
            ServerListPingEvent event = new ServerListPingEvent(InetAddress.getLocalHost(), Bukkit.getMotd(), Bukkit.getOnlinePlayers().size(), Bukkit.getMaxPlayers());
            Bukkit.getPluginManager().callEvent(event);
            return event.getMaxPlayers();
        } catch (Exception e) {
            severe("Error while calling ServerListPingEvent: ");
            TimoCloudBukkit.getInstance().severe(e);
            return Bukkit.getMaxPlayers();
        }
    }

    public void sendPlayers() {
        getSocketMessageManager().sendMessage(Message.create().setType("SET_PLAYERS").setData(getOnlinePlayersAmount() + "/" + getMaxPlayersAmount()));
    }

    public static TimoCloudBukkit getInstance() {
        return instance;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = ChatColor.translateAlternateColorCodes('&', prefix) + " ";
    }

    public BukkitSocketClientHandler getSocketClientHandler() {
        return socketClientHandler;
    }

    public BukkitFileManager getFileManager() {
        return fileManager;
    }

    public BukkitSocketMessageManager getSocketMessageManager() {
        return socketMessageManager;
    }

    public BukkitStringHandler getStringHandler() {
        return stringHandler;
    }

    public SignManager getSignManager() {
        return signManager;
    }

    public StateByEventManager getStateByEventManager() {
        return stateByEventManager;
    }

    public String getServerName() {
        return System.getProperty("timocloud-servername");
    }

    public String getServerId() {
        return System.getProperty("timocloud-serverid");
    }

    public String getMapName() {
        if (isRandomMap()) {
            return System.getProperty("timocloud-mapname");
        }
        return getFileManager().getConfig().getString("defaultMapName");
    }
}
