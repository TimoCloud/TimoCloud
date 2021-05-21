package cloud.timo.TimoCloud.base;

import cloud.timo.TimoCloud.api.utils.APIInstanceUtil;
import cloud.timo.TimoCloud.base.api.TimoCloudInternalMessageAPIBaseImplementation;
import cloud.timo.TimoCloud.base.managers.BaseFileManager;
import cloud.timo.TimoCloud.base.managers.BaseInstanceManager;
import cloud.timo.TimoCloud.base.managers.BaseResourceManager;
import cloud.timo.TimoCloud.base.managers.BaseTemplateManager;
import cloud.timo.TimoCloud.base.sockets.BaseSocketClient;
import cloud.timo.TimoCloud.base.sockets.BaseSocketClientHandler;
import cloud.timo.TimoCloud.base.sockets.BaseSocketMessageManager;
import cloud.timo.TimoCloud.base.sockets.BaseStringHandler;
import cloud.timo.TimoCloud.common.encryption.RSAKeyPairRetriever;
import cloud.timo.TimoCloud.common.encryption.RSAKeyUtil;
import cloud.timo.TimoCloud.common.modules.ModuleType;
import cloud.timo.TimoCloud.common.modules.TimoCloudModule;
import cloud.timo.TimoCloud.common.protocol.Message;
import cloud.timo.TimoCloud.common.protocol.MessageType;
import cloud.timo.TimoCloud.common.sockets.AESDecrypter;
import cloud.timo.TimoCloud.common.sockets.AESEncrypter;
import cloud.timo.TimoCloud.common.sockets.RSAHandshakeHandler;
import cloud.timo.TimoCloud.common.utils.network.InetAddressUtil;
import cloud.timo.TimoCloud.common.utils.options.OptionSet;
import io.netty.channel.Channel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;
import org.apache.commons.io.FileDeleteStrategy;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.KeyPair;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TimoCloudBase implements TimoCloudModule {

    public static final SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    private static TimoCloudBase instance;
    private OptionSet options;
    private String prefix = ANSI_YELLOW + "[" + ANSI_CYAN + "Timo" + ANSI_RESET + "Cloud" + ANSI_YELLOW + "]" + ANSI_RESET;
    private BaseFileManager fileManager;
    private RSAKeyPairRetriever rsaKeyPairRetriever;
    private BaseInstanceManager instanceManager;
    private BaseTemplateManager templateManager;
    private BaseSocketClient socketClient;
    private BaseSocketClientHandler socketClientHandler;
    private BaseSocketMessageManager socketMessageManager;
    private BaseStringHandler stringHandler;
    private BaseResourceManager resourceManager;
    private ScheduledExecutorService scheduler;
    private boolean connected = false;
    private boolean handshakePerformed = false;
    private boolean publicKeyPrinted;


    public static String getTime() {
        return "[" + format.format(new Date()) + "] ";
    }

    private static String formatLog(String message, String color) {
        return getTime() + getInstance().getPrefix() + color + message + ANSI_RESET;
    }

    @Override
    public void info(String message) {
        System.out.println(formatLog(message, ""));
    }

    @Override
    public void warning(String message) {
        System.err.println(formatLog(message, ANSI_YELLOW));
    }

    @Override
    public void severe(String message) {
        System.err.println(formatLog(message, ANSI_RED));
    }

    @Override
    public void load(OptionSet optionSet) throws Exception {
        this.options = optionSet;
        makeInstances();
        info(ANSI_GREEN + "Base has been loaded");
        scheduleConnecting();
    }

    @Override
    public void unload() {

    }

    private void makeInstances() throws Exception {
        instance = this;
        fileManager = new BaseFileManager();
        rsaKeyPairRetriever = new RSAKeyPairRetriever(new File(getFileManager().getBaseDirectory(), "keys/"));
        resourceManager = new BaseResourceManager();
        instanceManager = new BaseInstanceManager(getServerManagerDelayMillis());
        templateManager = new BaseTemplateManager();
        socketClient = new BaseSocketClient();
        socketClientHandler = new BaseSocketClientHandler();
        socketMessageManager = new BaseSocketMessageManager();
        stringHandler = new BaseStringHandler();
        scheduler = Executors.newScheduledThreadPool(1);
        APIInstanceUtil.setInternalMessageInstance(new TimoCloudInternalMessageAPIBaseImplementation());
    }

    private void scheduleConnecting() {
        scheduler.scheduleAtFixedRate(this::connectToSocket, 0, 1, TimeUnit.SECONDS);
        alertConnecting();
    }

    private long getServerManagerDelayMillis() {
        long delay = 300;
        String prop = System.getProperty("serverStartDelay");
        try {
            delay = Long.parseLong(prop);
        } catch (Exception e) {
        }
        return delay;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public void alertConnecting() {
        info("Connecting to Core...");
    }

    public void connectToSocket() {
        if (isConnected()) return;
        new Thread(() -> {
            try {
                getSocketClient().init(getCoreSocketIP(), getCoreSocketPort());
            } catch (Exception e) {
            }
        }).start();
    }

    public void onSocketConnect(Channel channel) {
        if (isConnected()) return;
        setConnected(true);
        try {
            if (! getRsaKeyPairRetriever().isValidKeyPairExisting()) {
                KeyPair keyPair = getRsaKeyPairRetriever().generateKeyPair();
                info(String.format("Successfully generated public key! Please register this base at the Core by executing the following command in the Core console: '%saddbase %s'", ANSI_RED, RSAKeyUtil.publicKeyToBase64(keyPair.getPublic()) + ANSI_RESET));
                this.publicKeyPrinted = true;
                disconnect(channel);
            }
            KeyPair keyPair = getRsaKeyPairRetriever().getKeyPair();
            new RSAHandshakeHandler(channel, keyPair, (aesKey -> {
                channel.pipeline().addBefore("prepender", "decrypter", new AESDecrypter(aesKey));
                channel.pipeline().addBefore("prepender", "decoder", new StringDecoder(CharsetUtil.UTF_8));
                channel.pipeline().addBefore("prepender", "handler", TimoCloudBase.getInstance().getStringHandler());
                channel.pipeline().addLast( "encrypter", new AESEncrypter(aesKey));
                channel.pipeline().addLast("encoder", new StringEncoder(CharsetUtil.UTF_8));

                getSocketMessageManager().sendMessage(Message.create().setType(MessageType.BASE_HANDSHAKE).set("publicAddress", getPublicIpAddress()));
                info("Successfully connected to Core socket!");
            })).startHandshake();
        } catch (Exception e) {
            severe(e);
            disconnect(channel);
        }
    }

    private void disconnect(Channel channel) {
        channel.close();
        setConnected(false);
    }

    public void onSocketDisconnect() {
        if (isConnected()) {
            if (handshakePerformed) info("Disconnected from Core. Reconnecting...");
            else {
                if (! publicKeyPrinted) {
                    try {
                        info(String.format("In order to be able to connect to the Core, you have to register this base by executing the command '%saddbase %s' in the Core console.", ANSI_RED, RSAKeyUtil.publicKeyToBase64(getRsaKeyPairRetriever().getKeyPair().getPublic()) + ANSI_RESET));
                    } catch (Exception e) {
                        severe(e);
                    }
                    publicKeyPrinted = true;
                }
            }
            handshakePerformed = false;
        }
        setConnected(false);
    }

    public void onHandshakeSuccess() {
        handshakePerformed = true;
        deleteOldDirectories();
    }

    private String getPublicIpAddress() {
        try {
            return new BufferedReader(new InputStreamReader(new URL("http://checkip.amazonaws.com").openStream())).readLine();
        } catch (Exception e) {
        }
        try {
            return InetAddressUtil.getLocalHost().getHostAddress();
        } catch (Exception e) {
            severe("Error while retrieving own IP address: ");
            severe(e);
        }
        return "127.0.0.1";
    }

    private void deleteOldDirectories() { // Some servers/proxies might be running, so we have to check if we can delete the directories
        for (File dir : Stream.concat(Arrays.stream(getFileManager().getServerTemporaryDirectory().listFiles()), Arrays.stream(getFileManager().getProxyTemporaryDirectory().listFiles())).collect(Collectors.toList())) {
            if (!dir.isDirectory()) {
                dir.delete();
                continue;
            }
            if (!dir.getName().contains("_") || dir.getName().split("_").length != 2)
                FileDeleteStrategy.FORCE.deleteQuietly(dir);
            getSocketMessageManager().sendMessage(Message.create().setType(MessageType.BASE_CHECK_IF_DELETABLE).setTarget(dir.getName()).setData(dir.getAbsolutePath()));
        }
    }

    public String getFileName() {
        return new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().getFile()).getName();
    }

    public String getCoreSocketIP() {
        return (String) getFileManager().getConfig().get("core-ip");
    }

    public Integer getCoreSocketPort() {
        return (Integer) getFileManager().getConfig().get("core-port");
    }

    public static TimoCloudBase getInstance() {
        return instance;
    }

    public BaseFileManager getFileManager() {
        return fileManager;
    }

    public RSAKeyPairRetriever getRsaKeyPairRetriever() {
        return rsaKeyPairRetriever;
    }

    public BaseInstanceManager getInstanceManager() {
        return instanceManager;
    }

    public BaseTemplateManager getTemplateManager() {
        return templateManager;
    }

    public BaseSocketClient getSocketClient() {
        return socketClient;
    }

    public BaseSocketClientHandler getSocketClientHandler() {
        return socketClientHandler;
    }

    public BaseSocketMessageManager getSocketMessageManager() {
        return socketMessageManager;
    }

    public BaseResourceManager getResourceManager() {
        return resourceManager;
    }

    public BaseStringHandler getStringHandler() {
        return stringHandler;
    }

    public String getPrefix() {
        return prefix + " ";
    }

    @Override
    public ModuleType getModuleType() {
        return ModuleType.BASE;
    }
}
