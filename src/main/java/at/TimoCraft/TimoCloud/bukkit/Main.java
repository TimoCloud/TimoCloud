package at.TimoCraft.TimoCloud.bukkit;

import at.TimoCraft.TimoCloud.bukkit.commands.SignsCommand;
import at.TimoCraft.TimoCloud.bukkit.listeners.SignChangeEvent;
import at.TimoCraft.TimoCloud.bukkit.managers.FileManager;
import at.TimoCraft.TimoCloud.bukkit.managers.OtherServerPingManager;
import at.TimoCraft.TimoCloud.bukkit.managers.SignManager;
import at.TimoCraft.TimoCloud.bukkit.sockets.BukkitSocketClient;
import at.TimoCraft.TimoCloud.bukkit.sockets.BukkitSocketClientHandler;
import at.TimoCraft.TimoCloud.bukkit.sockets.SocketMessageManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

/**
 * Created by Timo on 27.12.16.
 */
public class Main extends JavaPlugin {

    private static Main instance;
    private FileManager fileManager;
    private BukkitSocketClientHandler socketClientHandler;
    private SocketMessageManager socketMessageManager;
    private SignManager signManager;
    private OtherServerPingManager otherServerPingManager;
    private String prefix = "[TimoCloud]";

    public static void log(String message) {
        Bukkit.getConsoleSender().sendMessage(getInstance().getPrefix() + message.replace("&", "§"));
    }

    public void onEnable() {
        makeInstances();
        registerCommands();
        registerListeners();
        registerTasks();
        log("&ahas been enabled!");
    }

    public void onDisable() {
        log("&chas been disabled!");
    }

    public void onSocketDisconnect() {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "stop");
    }

    private void makeInstances() {
        instance = this;
        fileManager = new FileManager();
        socketClientHandler = new BukkitSocketClientHandler();
        socketMessageManager = new SocketMessageManager();
        signManager = new SignManager();
        otherServerPingManager = new OtherServerPingManager();
    }

    private void registerCommands() {
        getCommand("signs").setExecutor(new SignsCommand());
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new SignChangeEvent(), this);
    }

    private void registerTasks() {
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            try {
                String host = "127.0.0.1";
                int port = getFileManager().getConfig().getInt("socket-port");
                log("Connecting to bungee socket on " + host + ":" + port + "...");
                new BukkitSocketClient().init(host, port);
            } catch (Exception e) {
                e.printStackTrace();
                onSocketDisconnect();
            }
        });

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, (Runnable) () -> getSocketClientHandler().flush(), 0L, 1L);

        Bukkit.getScheduler().scheduleAsyncRepeatingTask(this, () -> {
            getSignManager().updateSigns();
            getOtherServerPingManager().requestStates();
            getOtherServerPingManager().requestExtras();
        }, 20L, 20L);
    }

    public static Main getInstance() {
        return instance;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix.replace("&", "§") + " ";
    }

    public BukkitSocketClientHandler getSocketClientHandler() {
        return socketClientHandler;
    }

    public void setSocketClientHandler(BukkitSocketClientHandler socketClientHandler) {
        this.socketClientHandler = socketClientHandler;
    }

    public FileManager getFileManager() {
        return fileManager;
    }

    public SocketMessageManager getSocketMessageManager() {
        return socketMessageManager;
    }

    public SignManager getSignManager() {
        return signManager;
    }

    public OtherServerPingManager getOtherServerPingManager() {
        return otherServerPingManager;
    }

    public String getServerName() {
        return new File(".").getAbsoluteFile().getParentFile().getName();
    }

    public String getGroupByServer(String server) {
        String ret = "";
        String[] split = server.split("-");
        for (int i = 0; i<split.length-1; i++) {
            ret = ret + split[i];
            if (i < split.length-2) {
                ret = ret + "-";
            }
        }
        return ret;
    }
}
