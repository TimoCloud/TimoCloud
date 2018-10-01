package cloud.timo.TimoCloud.base;

import cloud.timo.TimoCloud.base.managers.BaseFileManager;
import cloud.timo.TimoCloud.base.managers.BaseInstanceManager;
import cloud.timo.TimoCloud.base.managers.BaseResourceManager;
import cloud.timo.TimoCloud.base.managers.BaseTemplateManager;
import cloud.timo.TimoCloud.base.sockets.BaseSocketClient;
import cloud.timo.TimoCloud.base.sockets.BaseSocketClientHandler;
import cloud.timo.TimoCloud.base.sockets.BaseSocketMessageManager;
import cloud.timo.TimoCloud.base.sockets.BaseStringHandler;
import cloud.timo.TimoCloud.lib.messages.Message;
import cloud.timo.TimoCloud.lib.messages.MessageType;
import cloud.timo.TimoCloud.lib.modules.ModuleType;
import cloud.timo.TimoCloud.lib.modules.TimoCloudModule;
import cloud.timo.TimoCloud.lib.utils.options.OptionSet;
import org.apache.commons.io.FileDeleteStrategy;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
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
    private BaseInstanceManager instanceManager;
    private BaseTemplateManager templateManager;
    private BaseSocketClient socketClient;
    private BaseSocketClientHandler socketClientHandler;
    private BaseSocketMessageManager socketMessageManager;
    private BaseStringHandler stringHandler;
    private BaseResourceManager resourceManager;
    private ScheduledExecutorService scheduler;
    private boolean connected = false;

    public static String getTime() {
        return "[" + format.format(new Date()) + "] ";
    }

    private static String formatLog(String message, String color) {
        return (getTime() + getInstance().getPrefix() + color + message + ANSI_RESET);
    }

    @Override
    public void info(String message) {
        System.out.println(formatLog(message, ANSI_RESET));
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
    public void load(OptionSet optionSet) {
        this.options = optionSet;
        makeInstances();
        info(ANSI_GREEN + "Base has been loaded");
        scheduleConnecting();
    }

    @Override
    public void unload() {

    }

    private void makeInstances() {
        instance = this;
        fileManager = new BaseFileManager();
        instanceManager = new BaseInstanceManager(getServerManagerDelayMillis());
        templateManager = new BaseTemplateManager();
        socketClient = new BaseSocketClient();
        socketClientHandler = new BaseSocketClientHandler();
        socketMessageManager = new BaseSocketMessageManager();
        resourceManager = new BaseResourceManager();
        stringHandler = new BaseStringHandler();
        scheduler = Executors.newScheduledThreadPool(1);
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

    public void onSocketConnect() {
        if (isConnected()) return;
        setConnected(true);

        getSocketMessageManager().sendMessage(Message.create().setType(MessageType.BASE_HANDSHAKE).set("base", getName()).set("publicAddress", getPublicIpAddress()));
        info("Successfully connected to Core socket!");
    }

    public void onSocketDisconnect() {
        if (isConnected()) info("Disconnected from Core. Reconnecting...");
        setConnected(false);
    }

    public void onHandshakeSuccess() {
        deleteOldDirectories();
    }

    private String getPublicIpAddress() {
        try {
            return new BufferedReader(new InputStreamReader(new URL("http://checkip.amazonaws.com").openStream())).readLine();
        } catch (Exception e) {
        }
        try {
            return InetAddress.getLocalHost().getHostAddress();
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

    public String getName() {
        return (String) getFileManager().getConfig().get("name");
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
