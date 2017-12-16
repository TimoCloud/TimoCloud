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

    @Override
    public ServerObject getThisServer() {
        return TimoCloudAPI.getUniversalInstance().getServer(TimoCloudBukkit.getInstance().getServerName());
    }

    @Override
    public void sendCommandToBungeeCord(String command) {
        TimoCloudBukkit.getInstance().getBukkitSocketMessageManager().sendMessage("EXECUTE_COMMAND", command);
    }
}
