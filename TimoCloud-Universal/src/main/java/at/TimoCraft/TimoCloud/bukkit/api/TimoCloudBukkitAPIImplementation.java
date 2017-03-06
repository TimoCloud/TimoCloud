package at.TimoCraft.TimoCloud.bukkit.api;

import at.TimoCraft.TimoCloud.api.TimoCloudBukkitAPI;
import at.TimoCraft.TimoCloud.bukkit.TimoCloudBukkit;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class TimoCloudBukkitAPIImplementation implements TimoCloudBukkitAPI {
    private static String state = "ONLINE";
    private static String extra = "";

    @Override
    public String getServerName() {
        return TimoCloudBukkit.getInstance().getServerName();
    }

    @Override
    public String getState() {
        return state;
    }

    @Override
    public void setState(String state) {
        TimoCloudBukkitAPIImplementation.state = state;
        TimoCloudBukkit.getInstance().getBukkitSocketMessageManager().sendMessage("SETSTATE", state);
    }

    @Override
    public boolean isRandomMap() {
        return TimoCloudBukkit.getInstance().isRandomMap();
    }

    @Override
    public String getMapName() {
        return TimoCloudBukkit.getInstance().getMapName();
    }

    @Override
    public String getExtra() {
        return extra;
    }

    @Override
    public void setExtra(String extra) {
        TimoCloudBukkitAPIImplementation.extra = extra;
        TimoCloudBukkit.getInstance().getBukkitSocketMessageManager().sendMessage("SETEXTRA", extra);
    }

    @Override
    public String getState(String server) {
        return TimoCloudBukkit.getInstance().getOtherServerPingManager().getState(server);
    }

    @Override
    public String getMapName(String server) {
        return TimoCloudBukkit.getInstance().getOtherServerPingManager().getMap(server);
    }

    @Override
    public String getExtra(String server) {
        return TimoCloudBukkit.getInstance().getOtherServerPingManager().getExtra(server);
    }

    @Override
    public int getCurrentPlayers(String server) {
        return TimoCloudBukkit.getInstance().getOtherServerPingManager().getCurrentPlayers(server);
    }

    @Override
    public int getMaxPlayers(String server) {
        return TimoCloudBukkit.getInstance().getOtherServerPingManager().getMaxPlayers(server);
    }

    @Override
    public List<String> getOnlineServersByGroup(String group) {
        return TimoCloudBukkit.getInstance().getOtherServerPingManager().getServersFromGroup(group);
    }

    @Override
    public void sendPlayerToRandomServerOfGroup(Player player, String group) {
        List<String> servers = getOnlineServersByGroup(group);
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

    @Override
    public void sendCommandToBungeeCord(String command) {
        TimoCloudBukkit.getInstance().getBukkitSocketMessageManager().sendMessage("EXECUTECOMMAND", command);
    }
}
