package cloud.timo.TimoCloud.velocity;


import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.implementations.TimoCloudBungeeAPIImplementation;
import cloud.timo.TimoCloud.api.implementations.TimoCloudUniversalAPIBasicImplementation;
import cloud.timo.TimoCloud.api.implementations.internal.TimoCloudInternalImplementationAPIBasicImplementation;
import cloud.timo.TimoCloud.api.implementations.managers.APIResponseManager;
import cloud.timo.TimoCloud.api.implementations.managers.EventManager;
import cloud.timo.TimoCloud.api.utils.APIInstanceUtil;
import cloud.timo.TimoCloud.common.encryption.RSAKeyPairRetriever;
import cloud.timo.TimoCloud.common.global.logging.TimoCloudLogger;
import cloud.timo.TimoCloud.common.log.utils.LogInjectionUtil;
import cloud.timo.TimoCloud.common.manager.LobbyManager;
import cloud.timo.TimoCloud.common.protocol.Message;
import cloud.timo.TimoCloud.common.protocol.MessageType;
import cloud.timo.TimoCloud.common.sockets.AESDecrypter;
import cloud.timo.TimoCloud.common.sockets.AESEncrypter;
import cloud.timo.TimoCloud.common.sockets.RSAHandshakeHandler;
import cloud.timo.TimoCloud.common.utils.ChatColorUtil;
import cloud.timo.TimoCloud.velocity.api.TimoCloudInternalMessageAPIVelocityImplementation;
import cloud.timo.TimoCloud.velocity.api.TimoCloudMessageAPIVelocityImplementation;
import cloud.timo.TimoCloud.velocity.api.TimoCloudUniversalAPIVelocityImplementation;
import cloud.timo.TimoCloud.velocity.commands.FindCommand;
import cloud.timo.TimoCloud.velocity.commands.GlistCommand;
import cloud.timo.TimoCloud.velocity.commands.LobbyCommand;
import cloud.timo.TimoCloud.velocity.commands.TimoCloudCommand;
import cloud.timo.TimoCloud.velocity.listeners.*;
import cloud.timo.TimoCloud.velocity.managers.IpManager;
import cloud.timo.TimoCloud.velocity.managers.VelocityFileManager;
import cloud.timo.TimoCloud.velocity.sockets.VelocitySocketClient;
import cloud.timo.TimoCloud.velocity.sockets.VelocitySocketClientHandler;
import cloud.timo.TimoCloud.velocity.sockets.VelocitySocketMessageManager;
import cloud.timo.TimoCloud.velocity.sockets.VelocityStringHandler;
import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import io.netty.channel.Channel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;
import lombok.Getter;
import org.slf4j.Logger;

import java.io.File;
import java.security.KeyPair;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Getter
public class TimoCloudVelocity implements TimoCloudLogger {

    private static TimoCloudVelocity instance;
    private final ProxyServer server;
    private final Logger logger;
    private VelocityFileManager fileManager;
    private LobbyManager lobbyManager;
    private IpManager ipManager;
    private VelocitySocketClient socketClient;
    private VelocitySocketClientHandler socketClientHandler;
    private VelocitySocketMessageManager socketMessageManager;
    private VelocityStringHandler velocityStringHandler;
    private TimoCloudCommand timoCloudCommand;
    private String prefix;
    private boolean shuttingDown = false;

    @Inject
    public TimoCloudVelocity(ProxyServer server, Logger logger) {
        this.server = server;
        this.logger = logger;
    }

    public static TimoCloudVelocity getInstance() {
        return instance;
    }

    @Override
    public void info(String message) {
        getLogger().info(ChatColorUtil.translateAlternateColorCodes('&', message));
    }

    @Override
    public void warning(String message) {
        getLogger().warn(ChatColorUtil.translateAlternateColorCodes('&', message));
    }

    @Override
    public void severe(String message) {
        getLogger().error(ChatColorUtil.translateAlternateColorCodes('&', message));
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        try {
            instance = this;
            info("&eEnabling &bTimoCloudVelocity &eversion &7[&6" + server.getPluginManager().getPlugin("timocloud").get().getDescription().getVersion().get() + "&7]&e...");
            makeInstances();
            registerCommands();
            registerListeners();
            registerTasks();
            Executors.newSingleThreadExecutor().submit(this::connectToCore);
            while (!((TimoCloudUniversalAPIBasicImplementation) TimoCloudAPI.getUniversalAPI()).gotAnyData()) {
                try {
                    Thread.sleep(50); // Wait until we get the API data
                } catch (Exception e) {
                }
            }
            info("&aSuccessfully started TimoCloudVelocity!");
        } catch (Exception e) {
            severe("Error while enabling TimoCloudVelocity: ");
            TimoCloudVelocity.getInstance().severe(e);
        }
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        setShuttingDown(true);
        info("&cSuccessfully stopped &bTimoCloudVelocity!");
    }

    private void makeInstances() throws Exception {
        TimoCloudLogger.setLogger(this);
        fileManager = new VelocityFileManager();
        lobbyManager = new LobbyManager(getFileManager().getConfig().getString("fallbackGroup"), getFileManager().getConfig().getString("LobbyChooseStrategy"), getFileManager().getConfig().getString("emergencyFallback"));
        ipManager = new IpManager();
        socketClient = new VelocitySocketClient();
        socketClientHandler = new VelocitySocketClientHandler();
        socketMessageManager = new VelocitySocketMessageManager();
        velocityStringHandler = new VelocityStringHandler();
        timoCloudCommand = new TimoCloudCommand();

        APIInstanceUtil.setInternalMessageInstance(new TimoCloudInternalMessageAPIVelocityImplementation());
        APIInstanceUtil.setEventInstance(new EventManager());
        APIInstanceUtil.setUniversalInstance(new TimoCloudUniversalAPIVelocityImplementation());
        APIInstanceUtil.setBungeeInstance(new TimoCloudBungeeAPIImplementation(getProxyName()));
        APIInstanceUtil.setMessageInstance(new TimoCloudMessageAPIVelocityImplementation());
        APIInstanceUtil.setInternalImplementationAPIInstance(new TimoCloudInternalImplementationAPIBasicImplementation());
        TimoCloudAPI.getMessageAPI().registerMessageListener(new APIResponseManager(), "TIMOCLOUD_API_RESPONSE");
    }

    private void registerCommands() {
        getServer().getCommandManager().register(server.getCommandManager().metaBuilder("timocloud").aliases("tc").build(), getTimoCloudCommand());
        getServer().getCommandManager().register(server.getCommandManager().metaBuilder("glist").aliases("redisbungee", "rglist").build(), new GlistCommand());
        getServer().getCommandManager().register(server.getCommandManager().metaBuilder("find").aliases("rfind").build(), new FindCommand());
        List<String> lobbyCommands = getFileManager().getConfig().getList("lobbyCommands");
        if (lobbyCommands.size() > 0) {
            String[] aliases = lobbyCommands.subList(1, lobbyCommands.size()).toArray(new String[0]);
            server.getCommandManager().register(server.getCommandManager().metaBuilder(lobbyCommands.get(0)).aliases(aliases).build(), new LobbyCommand());
        }
    }

    private void connectToCore() {
        info("&6Connecting to TimoCloudCore...");
        try {
            socketClient.init(getTimoCloudCoreIP(), getTimoCloudCoreSocketPort());
        } catch (Exception e) {
            severe("Error while connecting to Core:");
            TimoCloudVelocity.getInstance().severe(e);
            onSocketDisconnect();
        }
    }

    private void registerTasks() {
        server.getScheduler().buildTask(this, this::everySecond).delay(1L, TimeUnit.SECONDS).repeat(1L, TimeUnit.SECONDS);
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
                channel.pipeline().addBefore("prepender", "handler", getVelocityStringHandler());
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
        server.shutdown();
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
        getSocketMessageManager().sendMessage(Message.create().setType(MessageType.PROXY_SET_PLAYER_COUNT).setData(getServer().getPlayerCount()));
    }

    private void registerListeners() {
        getServer().getEventManager().register(this, new LobbyJoin());
        getServer().getEventManager().register(this, new ServerKick());
        getServer().getEventManager().register(this, new ProxyPing());
        getServer().getEventManager().register(this, new EventMonitor());
        getServer().getEventManager().register(this, new IpInjector());
    }

    public String getProxyName() {
        return System.getProperty("timocloud-proxyname");
    }

    public String getProxyId() {
        return System.getProperty("timocloud-proxyid");
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }


    public void setShuttingDown(boolean shuttingDown) {
        this.shuttingDown = shuttingDown;
    }


}
