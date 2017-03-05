package at.TimoCraft.TimoCloud.base;

import at.TimoCraft.TimoCloud.base.managers.FileManager;
import at.TimoCraft.TimoCloud.base.managers.ServerManager;
import at.TimoCraft.TimoCloud.base.sockets.BaseSocketClient;
import at.TimoCraft.TimoCloud.base.sockets.BaseSocketClientHandler;
import at.TimoCraft.TimoCloud.base.sockets.BaseSocketMessageManager;
import at.TimoCraft.TimoCloud.base.sockets.BaseStringHandler;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Timo on 31.01.17.
 */
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
    private FileManager fileManager;
    private ServerManager serverManager;
    private BaseSocketClient socketClient;
    private BaseSocketClientHandler socketClientHandler;
    private BaseSocketMessageManager socketMessageManager;
    private BaseStringHandler stringHandler;

    public static String getTime() {
        return "[" + format.format(new Date()) + "] ";
    }

    public static void info(String message) {
        System.out.println(getTime() + getInstance().getPrefix() + message);
    }

    public static void severe(String message) {
        System.err.println(getTime() + getInstance().getPrefix() + "\\e[0;31m" + message);
    }

    public Base() {
        onEnable();
    }

    private void onEnable() {
        makeInstances();
        connectToSocket();
        info(ANSI_GREEN + "has been enabled");
    }

    private void makeInstances() {
        instance = this;
        fileManager = new FileManager();
        serverManager = new ServerManager();
        socketClient = new BaseSocketClient();
        socketClientHandler = new BaseSocketClientHandler();
        socketMessageManager = new BaseSocketMessageManager();
        stringHandler = new BaseStringHandler();
    }

    public void connectToSocket() {
        try {
            getSocketClient().init(getBungeeSocketIP(), getBungeeSocketPort());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onSocketDisconnect() {
        info("Disconnected from bungeecord. Reconnecting...");
        try {
            Thread.sleep(3000);
        } catch (Exception e2) {
            severe("Please do not interrupt while waiting.");
        }
        connectToSocket();
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

    public FileManager getFileManager() {
        return fileManager;
    }

    public ServerManager getServerManager() {
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
