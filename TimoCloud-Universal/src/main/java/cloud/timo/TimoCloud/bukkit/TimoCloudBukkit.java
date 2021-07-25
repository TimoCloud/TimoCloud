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
import cloud.timo.TimoCloud.bukkit.listeners.BlockEvents;
import cloud.timo.TimoCloud.bukkit.listeners.PlayerInteract;
import cloud.timo.TimoCloud.bukkit.listeners.PlayerJoin;
import cloud.timo.TimoCloud.bukkit.listeners.PlayerQuit;
import cloud.timo.TimoCloud.bukkit.listeners.ServerRegister;
import cloud.timo.TimoCloud.bukkit.listeners.SignChange;
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
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.File;
import java.security.KeyPair;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TimoCloudBukkit extends JavaPlugin implements TimoCloudLogger {

    @Getter
    private static TimoCloudBukkit instance;

    @Getter
    private BukkitFileManager fileManager;
    @Getter
    private BukkitSocketClientHandler socketClientHandler;
    @Getter
    private BukkitSocketMessageManager socketMessageManager;
    @Getter
    private BukkitStringHandler stringHandler;
    @Getter
    private SignManager signManager;
    @Getter
    private StateByEventManager stateByEventManager;
    @Getter
    private String prefix = "[TimoCloud] ";

    private boolean enabled = false;
    private boolean disabling = false;
    @Setter
    private boolean serverRegistered = false;

    @Override
    public void info(String message) {
        getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', getPrefix() + message));
    }

    @Override
    public void warning(String message) {
        getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', getPrefix() + message));
    }

    @Override
    public void severe(String message) {
        getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', getPrefix() + "&c" + message));
    }

    @Override
    public void onEnable() {
        this.disabling = false;

        registerListeners();
        registerTasks();
        registerChannel();

        if (!this.enabled) {
            try {
                info("&eEnabling &bTimoCloudBukkit&r &eversion &7[&6" + getDescription().getVersion() + "&7]&e...");

                makeInstances();
                registerCommands();

                LogInjectionUtil.saveSystemOutAndErr();
                Executors.newScheduledThreadPool(1).scheduleAtFixedRate(this::doEverySecond, 1L, 1L, TimeUnit.SECONDS);
                Executors.newSingleThreadExecutor().submit(this::connectToCore);

                long timeToTimeout = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(30);
                while (!((TimoCloudUniversalAPIBasicImplementation) TimoCloudAPI.getUniversalAPI()).gotAnyData()) {
                    //Timeout?
                    if (timeToTimeout < System.currentTimeMillis()) {
                        LogInjectionUtil.restoreSystemOutAndErr();
                        severe("&Connection to the core could not be established");
                        System.exit(0);
                        return;
                    }

                    try {
                        Thread.sleep(50); // Wait until we get the API data
                    } catch (Exception ignored) {
                    }
                }

                LogInjectionUtil.restoreSystemOutAndErr();
                this.enabled = true;
                info("&aTimoCloudBukkit has been enabled!");
            } catch (Exception e) {
                severe("Error while enabling TimoCloudBukkit: ");
                severe(e);
            }
        }
    }

    @Override
    public void onDisable() {
        this.disabling = true;
        info("&chas been disabled!");
    }

    // Run asynchronously because this thread will stay alive until the connection is closed
    private void connectToCore() {
        try {
            info("Connecting to TimoCloudCore socket on " + getTimoCloudCoreIP() + ":" + getTimoCloudCoreSocketPort() + "...");
            new BukkitSocketClient().init(getTimoCloudCoreIP(), getTimoCloudCoreSocketPort());
        } catch (Exception e) {
            severe(e);
        }
    }

    /**
     * Called when the server is fully ready
     */
    private void registerAtCore() {
        socketMessageManager.sendMessage(Message.create().setType(MessageType.SERVER_REGISTER).setTarget(getServerId()));
        socketMessageManager.sendMessage(Message.create().setType(MessageType.SERVER_SET_MAP).setData(getMapName()));
    }

    public void onSocketConnect(Channel channel) {
        try {
            KeyPair keyPair = new RSAKeyPairRetriever(new File(fileManager.getBaseDirectory(), "/keys/")).getKeyPair();
            new RSAHandshakeHandler(channel, keyPair, (aesKey -> {

                channel.pipeline().addBefore("prepender", "decrypter", new AESDecrypter(aesKey))
                        .addBefore("prepender", "decoder", new StringDecoder(CharsetUtil.UTF_8))
                        .addBefore("prepender", "handler", getStringHandler())
                        .addLast("encrypter", new AESEncrypter(aesKey))
                        .addLast("encoder", new StringEncoder(CharsetUtil.UTF_8));

                socketMessageManager.sendMessage(Message.create().setType(MessageType.SERVER_HANDSHAKE).setTarget(getServerId()));

            })).startHandshake();
        } catch (Exception e) {
            severe("Error during public key authentication, please report this!");
            e.printStackTrace();
        }
    }

    public void onSocketDisconnect(boolean connectionFailed) {
        LogInjectionUtil.restoreSystemOutAndErr();
        info("Disconnected from TimoCloudCore. Stopping server.");
        if (connectionFailed) {
            System.exit(0);
        } else if (isEnabled()) {
            stop();
        }
    }

    public void onHandshakeSuccess() {
        LogInjectionUtil.injectSystemOutAndErr(logEntry ->
                socketMessageManager.sendMessage(Message.create()
                        .setType(MessageType.SERVER_LOG_ENTRY)
                        .setData(logEntry)));

        requestApiData();
        doEverySecond();
    }

    public void stop() {
        getServer().shutdown();
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

    @SuppressWarnings("ConstantConditions")
    private void registerCommands() {
        PluginCommand command = getCommand("signs");
        command.setExecutor(new SignsCommand());
        command.setPermission("timocloud.command.signs");

        command = getCommand("timocloudbukkit");
        command.setExecutor(new TimoCloudBukkitCommand());
        command.setPermission("timocloud.command.bukkit");
    }

    private void registerListeners() {
        PluginManager pm = getServer().getPluginManager();

        pm.registerEvents(new SignChange(), this);
        pm.registerEvents(new PlayerInteract(), this);
        pm.registerEvents(new PlayerJoin(), this);
        pm.registerEvents(new PlayerQuit(), this);
        pm.registerEvents(new BlockEvents(), this);

        TimoCloudAPI.getEventAPI().registerListener(new ServerRegister());
    }

    private void registerChannel() {
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
    }

    @SuppressWarnings("UnstableApiUsage")
    public void sendPlayerToServer(Player player, String server) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        try {
            out.writeUTF("Connect");
            out.writeUTF(server);
        } catch (Exception e) {
            severe("Error while sending player &e" + player + " &c to server &e" + server + "&c. Please report this: ");
            severe(e);
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
        if (this.disabling || !this.serverRegistered) return;
        sendEverything();
    }

    private void registerTasks() {
        BukkitScheduler scheduler = getServer().getScheduler();
        scheduler.runTask(this, this::registerAtCore);
        scheduler.scheduleSyncRepeatingTask(this, () -> getSignManager().updateSigns(), 5L, 1L);
    }

    private void sendEverything() {
        sendMotds();
        getServer().getScheduler().runTaskAsynchronously(this, this::sendPlayers);
    }

    private void requestApiData() {
        socketMessageManager.sendMessage(Message.create().setType(MessageType.GET_API_DATA));
    }

    private void sendMotds() {
        try {
            ServerListPingEvent event = new ServerListPingEvent(InetAddressUtil.getLocalHost(), getServer().getMotd(), getServer().getOnlinePlayers().size(), getServer().getMaxPlayers());
            getServer().getPluginManager().callEvent(event);
            socketMessageManager.sendMessage(Message.create().setType(MessageType.SERVER_SET_MOTD).setData(event.getMotd()));
            getStateByEventManager().setStateByMotd(event.getMotd().trim());
        } catch (Exception e) {
            severe("Error while sending MOTD: ");
            severe(e);
            socketMessageManager.sendMessage(Message.create().setType(MessageType.SERVER_SET_MOTD).setData(getServer().getMotd()));
        }
    }

    public int getOnlinePlayersAmount() {
        try {
            ServerListPingEvent event = new ServerListPingEvent(InetAddressUtil.getLocalHost(), getServer().getMotd(), getServer().getOnlinePlayers().size(), getServer().getMaxPlayers());
            getServer().getPluginManager().callEvent(event);
            return event.getNumPlayers();
        } catch (Exception e) {
            severe("Error while calling ServerListPingEvent: ");
            severe(e);
            return getServer().getOnlinePlayers().size();
        }
    }

    public int getMaxPlayersAmount() {
        try {
            ServerListPingEvent event = new ServerListPingEvent(InetAddressUtil.getLocalHost(), getServer().getMotd(), getServer().getOnlinePlayers().size(), getServer().getMaxPlayers());
            getServer().getPluginManager().callEvent(event);
            return event.getMaxPlayers();
        } catch (Exception e) {
            severe("Error while calling ServerListPingEvent: ");
            severe(e);
            return getServer().getMaxPlayers();
        }
    }

    /**
     * Must be called asynchronously
     */
    public void sendPlayers() {
        socketMessageManager.sendMessage(Message.create().setType(MessageType.SERVER_SET_PLAYERS).setData(getOnlinePlayersAmount() + "/" + getMaxPlayersAmount()));
    }

    public void setPrefix(String prefix) {
        this.prefix = ChatColor.translateAlternateColorCodes('&', prefix) + " ";
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
