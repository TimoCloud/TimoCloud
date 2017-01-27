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
    private Map<String, String> motds;
    private Map<String, String> players;

    public OtherServerPingManager() {
        load();
    }

    public void load() {
        states = new HashMap<>();
        extras = new HashMap<>();
        motds = new HashMap<>();
        players = new HashMap<>();
    }

    public void requestEverything() {
        requestStates();
        requestExtras();
        requestMotds();
        requestPlayers();
    }

    public void requestStates() {
        for (String server : states.keySet()) {
            Main.getInstance().getBukkitSocketMessageManager().sendMessage("GETSTATE", server);
        }
    }

    public void requestExtras() {
        for (String server : extras.keySet()) {
            Main.getInstance().getBukkitSocketMessageManager().sendMessage("GETEXTRA", server);
        }
    }
    public void requestMotds() {
        for (String server : motds.keySet()) {
            Main.getInstance().getBukkitSocketMessageManager().sendMessage("GETMOTD", server);
        }
    }

    public void requestPlayers() {
        for (String server : players.keySet()) {
            Main.getInstance().getBukkitSocketMessageManager().sendMessage("GETPLAYERS", server);
        }
    }

    public String getState(String server) {
        states.putIfAbsent(server, "OFFLINE");
        return states.get(server);
    }

    public void setState(String server, String state) {
        states.put(server, state);
    }

    public String getExtra(String server) {
        extras.putIfAbsent(server, "");
        return extras.get(server);
    }

    public void setExtra(String server, String data) {
        extras.put(server, data);
    }

    public String getMotd(String server) {
        motds.putIfAbsent(server, "");
        return motds.get(server);
    }

    public void setMotd(String server, String data) {
        motds.put(server, data);
    }

    public int getCurrentPlayers(String server) {
        if (! players.containsKey(server)) {
            players.put(server, "0/0");
        }
        return Integer.parseInt(players.get(server).split("/")[0]);
    }

    public int getMaxPlayers(String server) {
        if (! players.containsKey(server)) {
            players.put(server, "0/0");
        }
        return Integer.parseInt(players.get(server).split("/")[1]);
    }

    public void setPlayers(String server, String data) {
        players.put(server, data);
    }
}
