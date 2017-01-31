package at.TimoCraft.TimoCloud.base;

import at.TimoCraft.TimoCloud.base.managers.FileManager;
import at.TimoCraft.TimoCloud.base.managers.ServerManager;
import at.TimoCraft.TimoCloud.base.sockets.BaseSocketClient;
import at.TimoCraft.TimoCloud.base.sockets.BaseSocketClientHandler;
import at.TimoCraft.TimoCloud.base.sockets.BaseSocketMessageManager;
import at.TimoCraft.TimoCloud.base.sockets.BaseStringHandler;

/**
 * Created by Timo on 31.01.17.
 */
public class Base {

    private static Base instance;
    private String prefix = "\\e[1;33m[\\e[1;36mTimo[0mCloud\\e[1;33m][0m";
    private FileManager fileManager;
    private ServerManager serverManager;
    private BaseSocketClient socketClient;
    private BaseSocketClientHandler socketClientHandler;
    private BaseSocketMessageManager socketMessageManager;
    private BaseStringHandler stringHandler;

    public static void info(String message) {
        System.out.println(getInstance().getPrefix() + message);
    }

    public static void severe(String message) {
        System.err.println(getInstance().getPrefix() + "\\e[0;31m" + message);
    }

    public Base() {
        onEnable();
    }

    private void onEnable() {
        makeInstances();
        connectToSocket();
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
            try {
                Thread.sleep(3000);
                connectToSocket();
            } catch (Exception e2) {
                severe("Please do not interrupt while waiting.");
            }
        }
    }

    public void onSocketDisconnect() {
        info("Disconnected from bungeecord. Reconnecting...");
        connectToSocket();
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
