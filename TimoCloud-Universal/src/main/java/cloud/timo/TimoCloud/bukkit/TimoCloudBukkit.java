package cloud.timo.TimoCloud.bukkit;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.implementations.TimoCloudUniversalAPIBasicImplementation;
import cloud.timo.TimoCloud.api.implementations.internal.TimoCloudInternalImplementationAPIBasicImplementation;
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
import cloud.timo.TimoCloud.common.encryption.RSAKeyPairRetriever;
import cloud.timo.TimoCloud.common.global.logging.TimoCloudLogger;
import cloud.timo.TimoCloud.common.log.utils.LogInjectionUtil;
import cloud.timo.TimoCloud.common.protocol.Message;
import cloud.timo.TimoCloud.common.protocol.MessageType;
import cloud.timo.TimoCloud.common.sockets.AESDecrypter;
import cloud.timo.TimoCloud.common.sockets.AESEncrypter;
import cloud.timo.TimoCloud.common.sockets.RSAHandshakeHandler;
import cloud.timo.TimoCloud.common.utils.network.InetAddressUtil;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import io.netty.channel.Channel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.security.KeyPair;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TimoCloudBukkit extends JavaPlugin implements TimoCloudLogger {

    private static TimoCloudBukkit instance;
    private BukkitFileManager fileManager;
    private BukkitSocketClientHandler socketClientHandler;
    private BukkitSocketMessageManager socketMessageManager;
    private BukkitStringHandler stringHandler;
    private SignManager signManager;
    private StateByEventManager stateByEventManager;
    private String prefix = "[TimoCloud] ";
    private boolean enabled = false;
    private boolean disabling = false;

    @Override
    public void info(String message) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', getPrefix() + message));
    }

    @Override
    public void warning(String message) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', getPrefix() + message));
    }

    @Override
    public void severe(String message) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', getPrefix() + "&c" + message));
    }

    @Override
    public void onEnable() {
        this.disabling = false;
        if (this.enabled) {
            registerCommands();
            registerListeners();
            registerTasks();
        } else {
            try {
                info("&eEnabling &bTimoCloudBukkit&r &eversion &7[&6" + getDescription().getVersion() + "&7]&e...");
                makeInstances();
                registerCommands();
                registerListeners();
                registerTasks();
                Executors.newScheduledThreadPool(1).scheduleAtFixedRate(this::doEverySecond, 1L, 1L, TimeUnit.SECONDS);
                registerChannel();
                Executors.newSingleThreadExecutor().submit(this::connectToCore);
                while (!((TimoCloudUniversalAPIBasicImplementation) TimoCloudAPI.getUniversalAPI()).gotAnyData()) {
                    try {
                        Thread.sleep(50); // Wait until we get the API data
                    } catch (Exception e) {
                    }
                }
                this.enabled = true;
                info("&aTimoCloudBukkit has been enabled!");
            } catch (Exception e) {
                severe("Error while enabling TimoCloudBukkit: ");
                TimoCloudBukkit.getInstance().severe(e);
            }
        }
    }

    @Override
    public void onDisable() {
        this.disabling = true;
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

    /**
     * Called when the server is fully ready
     */
    private void registerAtCore() {
        getSocketMessageManager().sendMessage(Message.create().setType(MessageType.SERVER_REGISTER).setTarget(getServerId()));
        getSocketMessageManager().sendMessage(Message.create().setType(MessageType.SERVER_SET_MAP).setData(getMapName()));
    }

    public void onSocketConnect(Channel channel) {
        try {
            KeyPair keyPair = new RSAKeyPairRetriever(new File(getFileManager().getBaseDirectory(), "/keys/")).getKeyPair();
            new RSAHandshakeHandler(channel, keyPair, (aesKey -> {
                channel.pipeline().addBefore("prepender", "decrypter", new AESDecrypter(aesKey));
                channel.pipeline().addBefore("prepender", "decoder", new StringDecoder(CharsetUtil.UTF_8));
                channel.pipeline().addBefore("prepender", "handler", getStringHandler());
                channel.pipeline().addLast("encrypter", new AESEncrypter(aesKey));
                channel.pipeline().addLast("encoder", new StringEncoder(CharsetUtil.UTF_8));

                getSocketMessageManager().sendMessage(Message.create().setType(MessageType.SERVER_HANDSHAKE).setTarget(getServerId()));
            })).startHandshake();
        } catch (Exception e) {
            severe("Error during public key authentification, please report this!");
            e.printStackTrace();
        }
    }

    public void onSocketDisconnect(boolean connectionFailed) {
        LogInjectionUtil.restoreSystemOutAndErr();
        info("Disconnected from TimoCloudCore. Stopping server.");
        if (connectionFailed) {
            System.exit(0);
        } else {
            if (isEnabled()) stop();
        }
    }

    public void onHandshakeSuccess() {
        LogInjectionUtil.injectSystemOutAndErr(logEntry ->
                getSocketMessageManager().sendMessage(Message.create()
                        .setType(MessageType.SERVER_LOG_ENTRY)
                        .setData(logEntry)));
        requestApiData();
        doEverySecond();
    }

    public void stop() {
        Bukkit.getScheduler().runTask(this, () -> {
            Bukkit.getServer().shutdown();
            System.exit(0);
        });
    }

    private void makeInstances() throws Exception {
        instance = this;

        TimoCloudLogger.setLogger(this);

        fileManager = new BukkitFileManager();
        socketClientHandler = new BukkitSocketClientHandler();
        socketMessageManager = new BukkitSocketMessageManager();
        stringHandler = new BukkitStringHandler();
        signManager = new SignManager();
        stateByEventManager = new StateByEventManager();

        APIInstanceUtil.setInternalMessageInstance(new TimoCloudInternalMessageAPIBukkitImplementation());
        APIInstanceUtil.setEventInstance(new EventManager());
        APIInstanceUtil.setUniversalInstance(new TimoCloudUniversalAPIBukkitImplementation());
        APIInstanceUtil.setBukkitInstance(new TimoCloudBukkitAPIImplementation());
        APIInstanceUtil.setMessageInstance(new TimoCloudMessageAPIBukkitImplementation());
        APIInstanceUtil.setInternalImplementationAPIInstance(new TimoCloudInternalImplementationAPIBasicImplementation());
        TimoCloudAPI.getMessageAPI().registerMessageListener(new APIResponseManager(), "TIMOCLOUD_API_RESPONSE");
    }

    //Check if running on version 1.13 or above by accessing a material only available since 1.13
    public boolean isVersion113OrAbove() {
        try {
            //1.13 Item
            Material material = Material.DEAD_FIRE_CORAL_BLOCK;
        } catch (NoSuchFieldError e) {
            return false;
        }
        return true;
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
            severe("Error while sending player &e" + player + " &c to server &e" + server + "&c. Please report this: ");
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
        if (this.disabling) return;
        sendEverything();
    }

    private void registerTasks() {
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, this::registerAtCore, 0L);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> getSignManager().updateSigns(), 5L, 1L);
    }

    private void sendEverything() {
        sendMotds();
        TimoCloudBukkit.getInstance().getServer().getScheduler().runTaskAsynchronously(TimoCloudBukkit.getInstance(), () -> TimoCloudBukkit.getInstance().sendPlayers());
    }

    private void requestApiData() {
        getSocketMessageManager().sendMessage(Message.create().setType(MessageType.GET_API_DATA));
    }

    private void sendMotds() {
        try {
            ServerListPingEvent event = new ServerListPingEvent(InetAddressUtil.getLocalHost(), Bukkit.getMotd(), Bukkit.getOnlinePlayers().size(), Bukkit.getMaxPlayers());
            Bukkit.getPluginManager().callEvent(event);
            getSocketMessageManager().sendMessage(Message.create().setType(MessageType.SERVER_SET_MOTD).setData(event.getMotd()));
            getStateByEventManager().setStateByMotd(event.getMotd().trim());
        } catch (Exception e) {
            severe("Error while sending MOTD: ");
            TimoCloudBukkit.getInstance().severe(e);
            getSocketMessageManager().sendMessage(Message.create().setType(MessageType.SERVER_SET_MOTD).setData(Bukkit.getMotd()));
        }
    }

    public int getOnlinePlayersAmount() {
        try {
            ServerListPingEvent event = new ServerListPingEvent(InetAddressUtil.getLocalHost(), Bukkit.getMotd(), Bukkit.getOnlinePlayers().size(), Bukkit.getMaxPlayers());
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
            ServerListPingEvent event = new ServerListPingEvent(InetAddressUtil.getLocalHost(), Bukkit.getMotd(), Bukkit.getOnlinePlayers().size(), Bukkit.getMaxPlayers());
            Bukkit.getPluginManager().callEvent(event);
            return event.getMaxPlayers();
        } catch (Exception e) {
            severe("Error while calling ServerListPingEvent: ");
            TimoCloudBukkit.getInstance().severe(e);
            return Bukkit.getMaxPlayers();
        }
    }

    /**
     * Must be called asynchronously
     */
    public void sendPlayers() {
        getSocketMessageManager().sendMessage(Message.create().setType(MessageType.SERVER_SET_PLAYERS).setData(getOnlinePlayersAmount() + "/" + getMaxPlayersAmount()));
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
