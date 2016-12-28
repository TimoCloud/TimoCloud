package at.TimoCraft.TimoCloud.bukkit;

import at.TimoCraft.TimoCloud.bukkit.listeners.ProxyPingEvent;
import at.TimoCraft.TimoCloud.bukkit.managers.FileManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Timo on 27.12.16.
 */
public class Main extends JavaPlugin {

    private static Main instance;
    private FileManager fileManager;
    private String prefix = "[TimoCloud]";

    public static void log(String message) {
        Bukkit.getConsoleSender().sendMessage(getInstance().getPrefix() + message);
    }

    public void onEnable() {
        makeInstances();
        registerListeners();
        log("&ahas been enabled!");
    }

    public void onDisable() {

        log("&chas been disabled!");
    }

    private void makeInstances() {
        instance = this;
        fileManager = new FileManager();
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new ProxyPingEvent(), this);
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
}
