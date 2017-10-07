package at.TimoCraft.TimoCloud.base;

import at.TimoCraft.TimoCloud.base.managers.BaseFileManager;
import at.TimoCraft.TimoCloud.base.managers.BaseServerManager;
import at.TimoCraft.TimoCloud.base.sockets.BaseSocketClient;
import at.TimoCraft.TimoCloud.base.sockets.BaseSocketClientHandler;
import at.TimoCraft.TimoCloud.base.sockets.BaseSocketMessageManager;
import at.TimoCraft.TimoCloud.base.sockets.BaseStringHandler;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Base {

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

    private static Base instance;
    private String prefix = ANSI_YELLOW + "[" +ANSI_CYAN + "Timo" + ANSI_RESET + "Cloud" + ANSI_YELLOW + "]" + ANSI_RESET;
    private BaseFileManager fileManager;
    private BaseServerManager serverManager;
    private BaseSocketClient socketClient;
    private BaseSocketClientHandler socketClientHandler;
    private BaseSocketMessageManager socketMessageManager;
    private BaseStringHandler stringHandler;
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

    public Base() {
        onEnable();
    }

    private void onEnable() {
        makeInstances();
        info(ANSI_GREEN + "has been enabled");
        scheduleConnecting();
    }

    private void makeInstances() {
        instance = this;
        fileManager = new BaseFileManager();
        serverManager = new BaseServerManager(getServerManagerDelayMillis());
        socketClient = new BaseSocketClient();
        socketClientHandler = new BaseSocketClientHandler();
        socketMessageManager = new BaseSocketMessageManager();
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
            delay = Long.parseLong(prop);
        } catch (Exception e) {}
        return delay;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public void alertConnecting() {
        info("Connecting to BungeeCord...");
    }

    public void connectToSocket() {
        if (isConnected()) return;
        try {
            getSocketClient().init(getBungeeSocketIP(), getBungeeSocketPort());
        } catch (Exception e) {}
    }

    public void onSocketConnect() {
        setConnected(true);
        getSocketMessageManager().sendMessage("BASE_HANDSHAKE", null);
        info("Successfully connected to BungeeCord socket!");
    }

    public void onSocketDisconnect() {
        if (isConnected()) info("Disconnected from bungeecord. Reconnecting...");
        setConnected(false);
    }

    public String getFileName() {
        return new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().getFile()).getName();
    }

    public String getName() {
        return (String) getFileManager().getConfig().get("name");
    }

    public String getBungeeSocketIP() {
        return (String) getFileManager().getConfig().get("bungeecord-ip");
    }

    public Integer getBungeeSocketPort() {
        return (Integer) getFileManager().getConfig().get("socket-port");
    }

    public static Base getInstance() {
        return instance;
    }

    public BaseFileManager getFileManager() {
        return fileManager;
    }

    public BaseServerManager getServerManager() {
        return serverManager;
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

    public BaseStringHandler getStringHandler() {
        return stringHandler;
    }

    public String getPrefix() {
        return prefix + " ";
    }
}
