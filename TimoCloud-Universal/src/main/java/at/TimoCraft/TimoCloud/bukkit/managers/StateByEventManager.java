package at.TimoCraft.TimoCloud.bukkit.managers;

import at.TimoCraft.TimoCloud.api.TimoCloudAPI;
import at.TimoCraft.TimoCloud.bukkit.TimoCloudBukkit;
import org.bukkit.Bukkit;

/**
 * Created by Timo on 09.04.17.
 */
public class StateByEventManager {

    private String stateBefore = "ONLINE";
    private boolean playersFull = false;

    private void setState(String state) {
        TimoCloudAPI.getBukkitInstance().setState(state);
    }

    public void setStateByMotd(String state) {
        if (playersFull) {
            return;
        }
        setState(state);
    }

    public void onPlayerJoin() {
        if (! TimoCloudBukkit.getInstance().getFileManager().getConfig().getBoolean("PlayersToState.enabled")) {
            return;
        }
        if (Bukkit.getOnlinePlayers().size() >= Bukkit.getMaxPlayers()) {
            playersFull = true;
            stateBefore = TimoCloudAPI.getBukkitInstance().getState();
            setState(TimoCloudBukkit.getInstance().getFileManager().getConfig().getString("PlayersToState.full"));
        }
    }

    public void onPlayerQuit() {
        if (!TimoCloudBukkit.getInstance().getFileManager().getConfig().getBoolean("PlayersToState.enabled")) {
            return;
        }
        if (Bukkit.getOnlinePlayers().size() < Bukkit.getMaxPlayers()) {
            playersFull = false;
            setState(stateBefore);
        }
    }
}
