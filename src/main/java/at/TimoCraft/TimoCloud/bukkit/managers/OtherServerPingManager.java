package at.TimoCraft.TimoCloud.bukkit.managers;

import at.TimoCraft.TimoCloud.bukkit.Main;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Timo on 28.12.16.
 */
public class OtherServerPingManager {
    private Map<String, String> states;
    private Map<String, String> extras;

    public OtherServerPingManager() {
        load();
    }

    public void load() {
        states = new HashMap<>();
        extras = new HashMap<>();
    }

    public void requestStates() {
        for (String server : states.keySet()) {
            Main.getInstance().getSocketMessageManager().sendMessage("GETSTATE", server);
        }
    }

    public void requestExtras() {
        for (String server : extras.keySet()) {
            Main.getInstance().getSocketMessageManager().sendMessage("GETEXTRA", server);
        }
    }

    public String getState(String server) {
        if (states.get(server) == null) {
            states.put(server, "UNKNOWN");
        }
        return states.get(server);
    }

    public void setState(String server, String state) {
        states.put(server, state);
    }

    public String getExtra(String server) {
        if (extras.get(server) == null) {
            extras.put(server, "");
        }
        return extras.get(server);
    }

    public void setExtra(String server, String extra) {
        extras.put(server, extra);
    }
}
