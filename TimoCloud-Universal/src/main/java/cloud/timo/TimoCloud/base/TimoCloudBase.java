package cloud.timo.TimoCloud.base;

import cloud.timo.TimoCloud.ModuleType;
import cloud.timo.TimoCloud.TimoCloudModule;
import cloud.timo.TimoCloud.base.managers.BaseFileManager;
import cloud.timo.TimoCloud.base.managers.BaseResourceManager;
import cloud.timo.TimoCloud.base.managers.BaseServerManager;
import cloud.timo.TimoCloud.base.managers.BaseTemplateManager;
import cloud.timo.TimoCloud.base.sockets.BaseSocketClient;
import cloud.timo.TimoCloud.base.sockets.BaseSocketClientHandler;
import cloud.timo.TimoCloud.base.sockets.BaseSocketMessageManager;
import cloud.timo.TimoCloud.base.sockets.BaseStringHandler;
import cloud.timo.TimoCloud.utils.options.OptionSet;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
    private BaseServerManager serverManager;
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

    public static void info(String message) {
        System.out.println(formatLog(message, ANSI_RESET));
    }

    public static void severe(String message) {
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
        serverManager = new BaseServerManager(getServerManagerDelayMillis());
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
        long delay = 1000;
        String prop = System.getProperty("serverStartDelay");
        try {
            //delay = Long.parseLong(prop);
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
        try {
            getSocketClient().init(getCoreSocketIP(), getCoreSocketPort());
        } catch (Exception e) {}
    }

    public void onSocketConnect() {
        setConnected(true);
        getSocketMessageManager().sendMessage("BASE_HANDSHAKE", null);
        info("Successfully connected to Core socket!");
    }

    public void onSocketDisconnect() {
        if (isConnected()) info("Disconnected from Core. Reconnecting...");
        setConnected(false);
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

    public BaseServerManager getServerManager() {
        return serverManager;
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
