package at.TimoCraft.TimoCloud.bukkit.api;

import at.TimoCraft.TimoCloud.bukkit.TimoCloudBukkit;
import at.TimoCraft.TimoCloud.bungeecord.TimoCloud;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

/**
 * Provides functions to interact with TimoCloud
 */
public class TimoCloudAPI {
    private static String state = "ONLINE";
    private static String extra = "";

    /**
     * @return Server name
     */
    public static String getServerName() {
        return TimoCloudBukkit.getInstance().getServerName();
    }

    /**
     * @return Current server state
     */
    public static String getState() {
        return state;
    }

    /**
     * Will be sent to TimoCloud BungeeCord
     *
     * @param state Any string, e.g. "ONLINE", "INGAME", "RESTARTING"
     */
    public static void setState(String state) {
        TimoCloudAPI.state = state;
        TimoCloudBukkit.getInstance().getBukkitSocketMessageManager().sendMessage("SETSTATE", state);
    }

    /**
     * To enable random maps, please remove the folder base/templates/YOURGROUP and create directories like "YOURGROUP_VILLAGE" instead - they will be randomly chosen
     * @return If server (map) was randomly chosen
     */
    public static boolean isRandomMap() {
        return TimoCloudBukkit.getInstance().isRandomMap();
    }

    /**
     * @return If map was randomly chosen, this will return the map name, if you call the template "YOURGROUP_VILLAGE", it will return VILLAGE. If you don't have multiple maps, it will return the defaultMapName set in config
     */
    public static String getMapName() {
        return TimoCloudBukkit.getInstance().getMapName();
    }

    /**
     * @return Current server extra
     */
    public static String getExtra() {
        return extra;
    }

    /**
     * Will be sent to TimoCloud BungeeCord
     *
     * @param extra Any string, maybe a map name?
     */
    public static void setExtra(String extra) {
        TimoCloudAPI.extra = extra;
        TimoCloudBukkit.getInstance().getBukkitSocketMessageManager().sendMessage("SETEXTRA", extra);
    }

    /**
     *
     * @param server The servers name
     * @return The servers state - if not found, it will return OFFLINE
     */
    public static String getState(String server) {
        return TimoCloudBukkit.getInstance().getOtherServerPingManager().getState(server);
    }

    /**
     * @param server The servers name
     * @return The servers (randomly chosen?) map name
     */
    public static String getMapName(String server) {
        return TimoCloudBukkit.getInstance().getOtherServerPingManager().getMap(server);
    }

    /**
     *
     * @param server The servers name
     * @return The servers Extra - if not found, it will return an empty String ("")
     */
    public static String getExtra(String server) {
        return TimoCloudBukkit.getInstance().getOtherServerPingManager().getExtra(server);
    }

    /**
     *
     * @param server The servers name
     * @return The servers state - if not found, it will return 0
     */
    public static int getCurrentPlayers(String server) {
        return TimoCloudBukkit.getInstance().getOtherServerPingManager().getCurrentPlayers(server);
    }

    /**
     *
     * @param server The servers name
     * @return The servers state - if not found, it will return 0
     */
    public static int getMaxPlayers(String server) {
        return TimoCloudBukkit.getInstance().getOtherServerPingManager().getMaxPlayers(server);
    }

    /**
     * Returns a list of server names registered in BungeeCord by TimoCloud. For example, you can use it to send a player to a random lobby server:
     * Attention! When calling the first time, the method will return an empty list and will only add the group to the list of groups which should be queried. So call the method once you enable your plugin!
     * @param group The server group (e.g. "Lobby")
     * @return List of connected servers in this group
     */
    public static List<String> getOnlineServersByGroup(String group) {
        return TimoCloudBukkit.getInstance().getOtherServerPingManager().getServersFromGroup(group);
    }

    /**
     * Sends a player to a random server of a given group.
     * Attention! This calls the method getOnlineServersByGroup(String group), so you have to be aware of the same thing as mentioned above: Calling the method the first time will not have any affect, just to add the group to the list of groups which should be queried. So call the method getOnlineServersByGroup(String group) when loading your plugin.
     * @param player Player you want to send
     * @param group Group you want to randomly choose a server
     */
    public static void sendPlayerToRandomServerOfGroup(Player player, String group) {
        List<String> servers = TimoCloudAPI.getOnlineServersByGroup(group);
        if (servers.size() < 1) {
            TimoCloudBukkit.log("Error: No server of group " + group + " found!");
            return;
        }
        Collections.shuffle(servers);
        Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(TimoCloudBukkit.getInstance().getInstance(), "BungeeCord");
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        try {
            out.writeUTF("Connect");
            out.writeUTF(servers.get(0));
            player.sendPluginMessage(TimoCloudBukkit.getInstance(), "BungeeCord", out.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param command This command will be executed on BungeeCord
     */
    public static void sendCommandToBungeeCord(String command) {
        TimoCloudBukkit.getInstance().getBukkitSocketMessageManager().sendMessage("EXECUTECOMMAND", command);
    }
}
