package at.TimoCraft.TimoCloud.bungeecord;


import at.TimoCraft.TimoCloud.bungeecord.commands.TimoCloudCommand;
import at.TimoCraft.TimoCloud.bungeecord.managers.FileManager;
import at.TimoCraft.TimoCloud.bungeecord.managers.ServerManager;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.concurrent.TimeUnit;

/**
 * Created by Timo on 26.12.16.
 */
public class TimoCloud extends Plugin {

    private static TimoCloud instance;
    private ServerManager serverManager;
    private FileManager fileManager;
    private String prefix;

    public static void info(String message) {
        getInstance().getLogger().info(" " + message);
    }

    public static void severe(String message) {
        getInstance().getLogger().severe(" " + message);
    }

    public void onEnable() {
        makeInstances();
        registerCommands();
        getProxy().getScheduler().runAsync(this, () -> getServerManager().startAllServers());
        info("Successfully started TimoCloud!");
    }

    public void onDisable() {
        getServerManager().stopAllServers();
        info("Successfully stopped TimoCloud!");
    }

    private void makeInstances() {
        instance = this;
        fileManager = new FileManager();
        serverManager = new ServerManager();
    }

    private void registerCommands() {
        getProxy().getPluginManager().registerCommand(this, new TimoCloudCommand());
    }

    private void registerTasks() {
        getProxy().getScheduler().schedule(this, new Runnable() {
            @Override
            public void run() {
                everySecond();
            }
        }, 1, TimeUnit.SECONDS);
    }

    public void everySecond() {
        getServerManager().everySecond();
    }

    public static TimoCloud getInstance() {
        return instance;
    }

    public ServerManager getServerManager() {
        return serverManager;
    }

    public FileManager getFileManager() {
        return fileManager;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }


    public String getFileName() {
        Plugin plugin = (Plugin) this;
        String path = plugin.getClass().getProtectionDomain().getCodeSource().getLocation().getFile();
        String[] spl = path.split("/");
        String name = spl[spl.length - 1];
        return name;
    }
}
