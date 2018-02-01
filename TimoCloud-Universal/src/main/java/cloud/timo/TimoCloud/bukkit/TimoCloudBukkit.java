package cloud.timo.TimoCloud.bukkit;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.bukkit.api.TimoCloudBukkitAPIImplementation;
import cloud.timo.TimoCloud.bukkit.api.TimoCloudUniversalAPIBukkitImplementation;
import cloud.timo.TimoCloud.bukkit.commands.SendBungeeCommand;
import cloud.timo.TimoCloud.bukkit.commands.SignsCommand;
import cloud.timo.TimoCloud.bukkit.commands.TimoCloudBukkitCommand;
import cloud.timo.TimoCloud.bukkit.listeners.*;
import cloud.timo.TimoCloud.bukkit.managers.BukkitFileManager;
import cloud.timo.TimoCloud.bukkit.managers.BukkitMessageManager;
import cloud.timo.TimoCloud.bukkit.managers.SignManager;
import cloud.timo.TimoCloud.bukkit.managers.StateByEventManager;
import cloud.timo.TimoCloud.bukkit.sockets.BukkitSocketClient;
import cloud.timo.TimoCloud.bukkit.sockets.BukkitSocketClientHandler;
import cloud.timo.TimoCloud.bukkit.sockets.BukkitSocketMessageManager;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.net.InetAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TimoCloudBukkit extends JavaPlugin {

    private static TimoCloudBukkit instance;
    private BukkitFileManager fileManager;
    private BukkitSocketClientHandler socketClientHandler;
    private BukkitSocketMessageManager socketMessageManager;
    private SignManager signManager;
    private StateByEventManager stateByEventManager;
    private String prefix = "[TimoCloudBukkit]";

    public static void log(String message) {
        BukkitMessageManager.sendMessage(Bukkit.getConsoleSender(), message);
    }

    @Override
    public void onEnable() {
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&',
                "&eEnabling &bTimoCloud &oBukkit&r &eversion &7[&6" + getDescription().getVersion() + "&7]&e..."));
        makeInstances();
        registerCommands();
        registerListeners();
        registerTasks();
        registerChannel();
        TimoCloudAPI.setBukkitImplementation(new TimoCloudBukkitAPIImplementation());
        TimoCloudAPI.setUniversalImplementation(new TimoCloudUniversalAPIBukkitImplementation());
        Executors.newSingleThreadExecutor().submit(this::connectToCore);
        log("&ahas been enabled!");
    }

    @Override
    public void onDisable() {
        log("&chas been disabled!");
    }

    private void connectToCore() {
        try {
            log("Connecting to TimoCloudCore socket on " + getTimoCloudCoreIP() + ":" + getTimoCloudCoreSocketPort() + "...");
            new BukkitSocketClient().init(getTimoCloudCoreIP(), getTimoCloudCoreSocketPort());
        } catch (Exception e) {
            e.printStackTrace();
            onSocketDisconnect();
        }
    }

    private void registerAtBungeeCord() {
        getSocketMessageManager().sendMessage("REGISTER", System.getProperty("timocloud-token"));
    }

    public void onSocketConnect() {
        getSocketMessageManager().sendMessage("HANDSHAKE", System.getProperty("timocloud-token"));
        doEverySecond();
    }

    public void onSocketDisconnect() {
        log("Disconnected from TimoCloudCore. Stopping server.");
        stop();
    }

    private void stop() {
        if (isStatic()) {
            Bukkit.shutdown();
        } else {
            Runtime.getRuntime().halt(0);
        }
    }

    private void makeInstances() {
        instance = this;
        fileManager = new BukkitFileManager();
        socketClientHandler = new BukkitSocketClientHandler();
        socketMessageManager = new BukkitSocketMessageManager();
        signManager = new SignManager();
        stateByEventManager = new StateByEventManager();
    }

    private void registerCommands() {
        getCommand("signs").setExecutor(new SignsCommand());
        getCommand("sendbungee").setExecutor(new SendBungeeCommand());
        TimoCloudBukkitCommand timoCloudBukkitCommand = new TimoCloudBukkitCommand();
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
            log("&cError while sending player &e" +  player + " &c to server &e" + server + "&c. Please report this: ");
            e.printStackTrace();
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
        getSocketMessageManager().sendMessage("GET_API_DATA", "");
    }

    private void sendMotds() {
        try {
            ServerListPingEvent event = new ServerListPingEvent(InetAddress.getLocalHost(), Bukkit.getMotd(), Bukkit.getOnlinePlayers().size(), Bukkit.getMaxPlayers());
            Bukkit.getPluginManager().callEvent(event);
            getSocketMessageManager().sendMessage("SET_MOTD", event.getMotd());
            getStateByEventManager().setStateByMotd(event.getMotd().trim());
        } catch (Exception e) {
            log("Error while sending MOTD: ");
            e.printStackTrace();
            getSocketMessageManager().sendMessage("SET_MOTD", Bukkit.getMotd());
        }
    }

    public int getOnlinePlayersAmount() {
        try {
            ServerListPingEvent event = new ServerListPingEvent(InetAddress.getLocalHost(), Bukkit.getMotd(), Bukkit.getOnlinePlayers().size(), Bukkit.getMaxPlayers());
            Bukkit.getPluginManager().callEvent(event);
            return event.getNumPlayers();
        } catch (Exception e) {
            TimoCloudBukkit.log("&cError while calling ServerListPingEvent: ");
            e.printStackTrace();
            return Bukkit.getOnlinePlayers().size();
        }
    }

    public int getMaxPlayersAmount() {
        try {
            ServerListPingEvent event = new ServerListPingEvent(InetAddress.getLocalHost(), Bukkit.getMotd(), Bukkit.getOnlinePlayers().size(), Bukkit.getMaxPlayers());
            Bukkit.getPluginManager().callEvent(event);
            return event.getMaxPlayers();
        } catch (Exception e) {
            TimoCloudBukkit.log("&cError while calling ServerListPingEvent: ");
            e.printStackTrace();
            return Bukkit.getMaxPlayers();
        }
    }

    public void sendPlayers() {
        getSocketMessageManager().sendMessage("SET_PLAYERS", getOnlinePlayersAmount() + "/" + getMaxPlayersAmount());
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

    public SignManager getSignManager() {
        return signManager;
    }

    public StateByEventManager getStateByEventManager() {
        return stateByEventManager;
    }

    public String getServerName() {
        return System.getProperty("timocloud-servername");
    }

    public String getMapName() {
        if (isRandomMap()) {
            return System.getProperty("timocloud-mapname");
        }
        return getFileManager().getConfig().getString("defaultMapName");
    }
}
