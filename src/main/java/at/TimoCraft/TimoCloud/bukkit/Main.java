package at.TimoCraft.TimoCloud.bukkit;

import at.TimoCraft.TimoCloud.bukkit.commands.SignsCommand;
import at.TimoCraft.TimoCloud.bukkit.listeners.PlayerInteract;
import at.TimoCraft.TimoCloud.bukkit.listeners.SignChange;
import at.TimoCraft.TimoCloud.bukkit.managers.FileManager;
import at.TimoCraft.TimoCloud.bukkit.managers.OtherServerPingManager;
import at.TimoCraft.TimoCloud.bukkit.managers.SignManager;
import at.TimoCraft.TimoCloud.bukkit.sockets.BukkitSocketClient;
import at.TimoCraft.TimoCloud.bukkit.sockets.BukkitSocketClientHandler;
import at.TimoCraft.TimoCloud.bukkit.sockets.BukkitSocketMessageManager;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Timo on 27.12.16.
 */
public class Main extends JavaPlugin {

    private static Main instance;
    private FileManager fileManager;
    private BukkitSocketClientHandler socketClientHandler;
    private BukkitSocketMessageManager bukkitSocketMessageManager;
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
        registerChannel();
        log("&ahas been enabled!");
    }

    public void onDisable() {
        log("&chas been disabled!");
    }

    public void onSocketDisconnect() {
        log("Disconnected from TimoCloud. Stopping server.");
        try {
            kill();
        } catch (Exception e) {
            Main.log("Error while killing server:");
            e.printStackTrace();
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "stop");
        }
    }

    private void kill() throws IOException {
        Runtime.getRuntime().halt(0);
        /*
        for (World world : Bukkit.getWorlds()) {
            Bukkit.unloadWorld(world, false);
        }
        Process process = Runtime.getRuntime().exec(new String[] {"jps", "-m", "|grep", Bukkit.getPort() + ""});
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String response;
        while ((response = reader.readLine()) != null) {
            Main.log("Got response: " + response);
            String pNumber = response.split(" ")[0];
            Runtime.getRuntime().exec(new String[]{"pkill", "-f", pNumber});
            return;
        }
        Main.log("Got no response.");
        */
        /*
        Process process = Runtime.getRuntime().exec("screen -ls | awk '/\\." + getServerName() + "\t/ {print strtonum($1)}'");
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String response = reader.readLine().trim();
        System.out.println("Response:" + response);
        int processID = Integer.parseInt(response);
        Runtime.getRuntime().exec("kill -9 " + processID);
        */
    }

    private void makeInstances() {
        instance = this;
        fileManager = new FileManager();
        socketClientHandler = new BukkitSocketClientHandler();
        bukkitSocketMessageManager = new BukkitSocketMessageManager();
        signManager = new SignManager();
        otherServerPingManager = new OtherServerPingManager();
    }

    private void registerCommands() {
        getCommand("signs").setExecutor(new SignsCommand());
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new SignChange(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerInteract(), this);
    }

    private void registerChannel() {
        Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
    }

    public void sendPlayerToServer(Player p, String server) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        try {
            out.writeUTF("Connect");
            out.writeUTF(server);
        } catch (Exception e) {}
        p.sendPluginMessage(this, "BungeeCord", out.toByteArray());
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

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            sendPlayers();
            getOtherServerPingManager().requestEverything();
            getSignManager().updateSigns();
        }, 20L, 45L);
    }

    public void sendPlayers() {
        getBukkitSocketMessageManager().sendMessage("SETPLAYERS", Bukkit.getOnlinePlayers().size() + "/" + Bukkit.getMaxPlayers());
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

    public BukkitSocketMessageManager getBukkitSocketMessageManager() {
        return bukkitSocketMessageManager;
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
        if (! server.contains("-")) {
            return server;
        }
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
