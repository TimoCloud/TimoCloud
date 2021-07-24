package cloud.timo.TimoCloud.bungeecord;


import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.implementations.TimoCloudBungeeAPIImplementation;
import cloud.timo.TimoCloud.api.implementations.TimoCloudUniversalAPIBasicImplementation;
import cloud.timo.TimoCloud.api.implementations.internal.TimoCloudInternalImplementationAPIBasicImplementation;
import cloud.timo.TimoCloud.api.implementations.managers.APIResponseManager;
import cloud.timo.TimoCloud.api.implementations.managers.EventManager;
import cloud.timo.TimoCloud.api.utils.APIInstanceUtil;
import cloud.timo.TimoCloud.bungeecord.api.TimoCloudInternalMessageAPIBungeeImplementation;
import cloud.timo.TimoCloud.bungeecord.api.TimoCloudMessageAPIBungeeImplementation;
import cloud.timo.TimoCloud.bungeecord.api.TimoCloudUniversalAPIBungeeImplementation;
import cloud.timo.TimoCloud.bungeecord.commands.FindCommand;
import cloud.timo.TimoCloud.bungeecord.commands.GlistCommand;
import cloud.timo.TimoCloud.bungeecord.commands.LobbyCommand;
import cloud.timo.TimoCloud.bungeecord.commands.TimoCloudCommand;
import cloud.timo.TimoCloud.bungeecord.listeners.EventMonitor;
import cloud.timo.TimoCloud.bungeecord.listeners.IpInjector;
import cloud.timo.TimoCloud.bungeecord.listeners.LobbyJoin;
import cloud.timo.TimoCloud.bungeecord.listeners.ProxyPing;
import cloud.timo.TimoCloud.bungeecord.listeners.ServerKick;
import cloud.timo.TimoCloud.bungeecord.managers.BungeeFileManager;
import cloud.timo.TimoCloud.bungeecord.managers.IpManager;
import cloud.timo.TimoCloud.bungeecord.sockets.BungeeSocketClient;
import cloud.timo.TimoCloud.bungeecord.sockets.BungeeSocketClientHandler;
import cloud.timo.TimoCloud.bungeecord.sockets.BungeeSocketMessageManager;
import cloud.timo.TimoCloud.bungeecord.sockets.BungeeStringHandler;
import cloud.timo.TimoCloud.common.encryption.RSAKeyPairRetriever;
import cloud.timo.TimoCloud.common.global.logging.TimoCloudLogger;
import cloud.timo.TimoCloud.common.log.utils.LogInjectionUtil;
import cloud.timo.TimoCloud.common.manager.LobbyManager;
import cloud.timo.TimoCloud.common.protocol.Message;
import cloud.timo.TimoCloud.common.protocol.MessageType;
import cloud.timo.TimoCloud.common.sockets.AESDecrypter;
import cloud.timo.TimoCloud.common.sockets.AESEncrypter;
import cloud.timo.TimoCloud.common.sockets.RSAHandshakeHandler;
import io.netty.channel.Channel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.File;
import java.security.KeyPair;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TimoCloudBungee extends Plugin implements TimoCloudLogger {

    @Getter
    private static TimoCloudBungee instance;

    @Getter
    private BungeeFileManager fileManager;
    @Getter
    private LobbyManager lobbyManager;
    @Getter
    private IpManager ipManager;
    @Getter
    private BungeeSocketClient socketClient;
    @Getter
    private BungeeSocketClientHandler socketClientHandler;
    @Getter
    private BungeeSocketMessageManager socketMessageManager;
    @Getter
    private BungeeStringHandler bungeeStringHandler;
    @Getter
    private TimoCloudCommand timoCloudCommand;
    @Getter
    @Setter
    private String prefix;
    @Getter
    @Setter
    private boolean shuttingDown = false;

    @Override
    public void info(String message) {
        getLogger().info(ChatColor.translateAlternateColorCodes('&', message));
    }

    @Override
    public void warning(String message) {
        getLogger().warning(ChatColor.translateAlternateColorCodes('&', " " + message));
    }

    @Override
    public void severe(String message) {
        getLogger().severe(ChatColor.translateAlternateColorCodes('&', " &c" + message));
    }

    @Override
    public void onEnable() {
        try {
            instance = this;
            info("&eEnabling &bTimoCloudBungee &eversion &7[&6" + getDescription().getVersion() + "&7]&e...");
            makeInstances();
            registerCommands();
            registerListeners();
            registerTasks();
            Executors.newSingleThreadExecutor().submit(this::connectToCore);
            while (!((TimoCloudUniversalAPIBasicImplementation) TimoCloudAPI.getUniversalAPI()).gotAnyData()) {
                try {
                    Thread.sleep(50); // Wait until we get the API data
                } catch (Exception ignored) {
                }
            }
            info("&aSuccessfully started TimoCloudBungee!");
        } catch (Exception e) {
            severe("Error while enabling TimoCloudBungee: ");
            TimoCloudBungee.getInstance().severe(e);
        }
    }

    @Override
    public void onDisable() {
        setShuttingDown(true);
        info("&cSuccessfully stopped TimoCloudBungee!");
    }

    private void makeInstances() throws Exception {
        TimoCloudLogger.setLogger(this);

        fileManager = new BungeeFileManager();
        lobbyManager = new LobbyManager(getFileManager().getConfig().getString("fallbackGroup"), getFileManager().getConfig().getString("LobbyChooseStrategy"), getFileManager().getConfig().getString("emergencyFallback"));
        ipManager = new IpManager();
        socketClient = new BungeeSocketClient();
        socketClientHandler = new BungeeSocketClientHandler();
        socketMessageManager = new BungeeSocketMessageManager();
        bungeeStringHandler = new BungeeStringHandler();
        timoCloudCommand = new TimoCloudCommand();

        APIInstanceUtil.setInternalMessageInstance(new TimoCloudInternalMessageAPIBungeeImplementation());
        APIInstanceUtil.setEventInstance(new EventManager());
        APIInstanceUtil.setUniversalInstance(new TimoCloudUniversalAPIBungeeImplementation());
        APIInstanceUtil.setBungeeInstance(new TimoCloudBungeeAPIImplementation(getProxyName()));
        APIInstanceUtil.setMessageInstance(new TimoCloudMessageAPIBungeeImplementation());
        APIInstanceUtil.setInternalImplementationAPIInstance(new TimoCloudInternalImplementationAPIBasicImplementation());
        TimoCloudAPI.getMessageAPI().registerMessageListener(new APIResponseManager(), "TIMOCLOUD_API_RESPONSE");
    }

    private void registerCommands() {
        getProxy().getPluginManager().registerCommand(this, getTimoCloudCommand());
        getProxy().getPluginManager().registerCommand(this, new GlistCommand());
        getProxy().getPluginManager().registerCommand(this, new FindCommand());
        List<String> lobbyCommands = getFileManager().getConfig().getStringList("lobbyCommands");
        if (lobbyCommands.size() > 0) {
            String[] aliases = lobbyCommands.subList(1, lobbyCommands.size()).toArray(new String[0]);
            getProxy().getPluginManager().registerCommand(this, new LobbyCommand(lobbyCommands.get(0), aliases));
        }
    }

    private void connectToCore() {
        info("&6Connecting to TimoCloudCore...");
        try {
            socketClient.init(getTimoCloudCoreIP(), getTimoCloudCoreSocketPort());
        } catch (Exception e) {
            severe("Error while connecting to Core:");
            TimoCloudBungee.getInstance().severe(e);
            onSocketDisconnect();
        }
    }

    private void registerTasks() {
        getProxy().getScheduler().schedule(this, this::everySecond, 1L, 1L, TimeUnit.SECONDS);
    }

    public String getTimoCloudCoreIP() {
        return System.getProperty("timocloud-corehost").split(":")[0];
    }

    public int getTimoCloudCoreSocketPort() {
        return Integer.parseInt(System.getProperty("timocloud-corehost").split(":")[1]);
    }

    public void onSocketConnect(Channel channel) {
        try {
            KeyPair keyPair = new RSAKeyPairRetriever(new File(getFileManager().getBaseDirectory(), "/keys/")).getKeyPair();
            new RSAHandshakeHandler(channel, keyPair, (aesKey -> {
                channel.pipeline().addBefore("prepender", "decrypter", new AESDecrypter(aesKey));
                channel.pipeline().addBefore("prepender", "decoder", new StringDecoder(CharsetUtil.UTF_8));
                channel.pipeline().addBefore("prepender", "handler", getBungeeStringHandler());
                channel.pipeline().addLast("encrypter", new AESEncrypter(aesKey));
                channel.pipeline().addLast("encoder", new StringEncoder(CharsetUtil.UTF_8));

                getSocketMessageManager().sendMessage(Message.create().setType(MessageType.PROXY_HANDSHAKE).setTarget(getProxyId()));
            })).startHandshake();
        } catch (Exception e) {
            severe("Error during public key authentification, please report this!");
            e.printStackTrace();
        }
    }

    public void onSocketDisconnect() {
        LogInjectionUtil.restoreSystemOutAndErr();
        info("Disconnected from TimoCloudCore. Shutting down....");
        stop();
    }

    public void onHandshakeSuccess() {
        LogInjectionUtil.injectSystemOutAndErr(logEntry ->
                getSocketMessageManager().sendMessage(Message.create()
                        .setType(MessageType.PROXY_LOG_ENTRY)
                        .setData(logEntry))
        );
        requestApiData();
        everySecond();
    }

    public void stop() {
        ProxyServer.getInstance().stop();
    }

    private void everySecond() {
        if (isShuttingDown()) return;
        sendEverything();
    }

    private void requestApiData() {
        getSocketMessageManager().sendMessage(Message.create().setType(MessageType.GET_API_DATA));
    }

    private void sendEverything() {
        sendPlayerCount();
    }

    public void sendPlayerCount() {
        getSocketMessageManager().sendMessage(Message.create().setType(MessageType.PROXY_SET_PLAYER_COUNT).setData(getProxy().getOnlineCount()));
    }

    private void registerListeners() {
        getProxy().getPluginManager().registerListener(this, new LobbyJoin());
        getProxy().getPluginManager().registerListener(this, new ServerKick());
        getProxy().getPluginManager().registerListener(this, new ProxyPing());
        getProxy().getPluginManager().registerListener(this, new EventMonitor());
        getProxy().getPluginManager().registerListener(this, new IpInjector());
    }

    public String getProxyName() {
        return System.getProperty("timocloud-proxyname");
    }

    public String getProxyId() {
        return System.getProperty("timocloud-proxyid");
    }

}
