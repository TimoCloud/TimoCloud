package at.TimoCraft.TimoCloud.bukkit.api;

import at.TimoCraft.TimoCloud.api.TimoCloudAPI;
import at.TimoCraft.TimoCloud.api.TimoCloudBukkitAPI;
import at.TimoCraft.TimoCloud.api.objects.ServerObject;
import at.TimoCraft.TimoCloud.bukkit.TimoCloudBukkit;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class TimoCloudBukkitAPIImplementation implements TimoCloudBukkitAPI {

    private String state;
    private String extra;

    @Override
    public ServerObject getThisServer() {
        return TimoCloudAPI.getUniversalInstance().getServer(getServerName());
    }

    @Override
    public String getServerName() {
        return TimoCloudBukkit.getInstance().getServerName();
    }

    @Deprecated
    @Override
    public String getState() {
        return state;
    }

    @Deprecated
    @Override
    public void setState(String state) {
        this.state = state;
        TimoCloudBukkit.getInstance().getBukkitSocketMessageManager().sendMessage("SET_STATE", state);
    }

    @Deprecated
    @Override
    public boolean isRandomMap() {
        return TimoCloudBukkit.getInstance().isRandomMap();
    }

    @Deprecated
    @Override
    public String getMapName() {
        return TimoCloudBukkit.getInstance().getMapName();
    }

    @Deprecated
    @Override
    public String getExtra() {
        return extra;
    }

    @Deprecated
    @Override
    public void setExtra(String extra) {
        this.extra = extra;
        TimoCloudBukkit.getInstance().getBukkitSocketMessageManager().sendMessage("SET_EXTRA", extra);
    }

    @Deprecated
    @Override
    public String getState(String server) {
        return TimoCloudBukkit.getInstance().getOtherServerPingManager().getState(server);
    }

    @Deprecated
    @Override
    public String getMapName(String server) {
        return TimoCloudBukkit.getInstance().getOtherServerPingManager().getMap(server);
    }

    @Deprecated
    @Override
    public String getExtra(String server) {
        return TimoCloudBukkit.getInstance().getOtherServerPingManager().getExtra(server);
    }

    @Deprecated
    @Override
    public int getCurrentPlayers(String server) {
        return TimoCloudBukkit.getInstance().getOtherServerPingManager().getCurrentPlayers(server);
    }

    @Deprecated
    @Override
    public int getMaxPlayers(String server) {
        return TimoCloudBukkit.getInstance().getOtherServerPingManager().getMaxPlayers(server);
    }

    @Deprecated
    @Override
    public List<String> getOnlineServersByGroup(String group) {
        return TimoCloudBukkit.getInstance().getOtherServerPingManager().getServersFromGroup(group);
    }

    @Deprecated
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
        TimoCloudBukkit.getInstance().getBukkitSocketMessageManager().sendMessage("EXECUTE_COMMAND", command);
    }
}
