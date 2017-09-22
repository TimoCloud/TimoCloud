package at.TimoCraft.TimoCloud.bukkit.managers;

import at.TimoCraft.TimoCloud.bukkit.TimoCloudBukkit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Timo on 28.12.16.
 */
public class OtherServerPingManager {
    private Map<String, String> states;
    private Map<String, String> extras;
    private Map<String, String> motds;
    private Map<String, String> maps;
    private Map<String, String> players;
    private Map<String, List<String>> servers;

    public OtherServerPingManager() {
        load();
    }

    public void load() {
        states = new HashMap<>();
        extras = new HashMap<>();
        motds = new HashMap<>();
        maps = new HashMap<>();
        players = new HashMap<>();
        servers = new HashMap<>();
    }

    public void requestEverything() {
        requestApiData();
        requestStates();
        requestExtras();
        requestMotds();
        requestMaps();
        requestPlayers();
        requestServers();
    }

    public void requestApiData() {
        TimoCloudBukkit.getInstance().getBukkitSocketMessageManager().sendMessage("GETAPIDATA", null);
    }

    public void requestStates() {
        for (String server : states.keySet()) {
            TimoCloudBukkit.getInstance().getBukkitSocketMessageManager().sendMessage("GETSTATE", server);
        }
    }

    public void requestExtras() {
        for (String server : extras.keySet()) {
            TimoCloudBukkit.getInstance().getBukkitSocketMessageManager().sendMessage("GETEXTRA", server);
        }
    }

    public void requestMotds() {
        for (String server : motds.keySet()) {
            TimoCloudBukkit.getInstance().getBukkitSocketMessageManager().sendMessage("GETMOTD", server);
        }
    }

    public void requestMaps() {
        for (String server : maps.keySet()) {
            TimoCloudBukkit.getInstance().getBukkitSocketMessageManager().sendMessage("GETMAP", server);
        }
    }

    public void requestPlayers() {
        for (String server : players.keySet()) {
            TimoCloudBukkit.getInstance().getBukkitSocketMessageManager().sendMessage("GETPLAYERS", server);
        }
    }

    public void requestServers() {
        for (String group : servers.keySet()) {
            TimoCloudBukkit.getInstance().getBukkitSocketMessageManager().sendMessage("GETSERVERS", group);
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

    public void setMap(String server, String data) {
        maps.put(server, data);
    }

    public String getMap(String server) {
        maps.putIfAbsent(server, TimoCloudBukkit.getInstance().getFileManager().getConfig().getString("defaultMapName"));
        return maps.get(server);
    }

    public void setMotd(String server, String data) {
        motds.put(server, data);
    }

    public int getCurrentPlayers(String server) {
        if (!players.containsKey(server)) {
            players.put(server, "0/0");
        }
        return Integer.parseInt(players.get(server).split("/")[0]);
    }

    public int getMaxPlayers(String server) {
        if (!players.containsKey(server)) {
            players.put(server, "0/0");
        }
        return Integer.parseInt(players.get(server).split("/")[1]);
    }

    public void setPlayers(String server, String data) {
        players.put(server, data);
    }

    public List<String> getServersFromGroup(String group) {
        servers.putIfAbsent(group, new ArrayList<>());
        return servers.get(group);
    }

    public void setServersToGroup(String group, List<String> servers) {
        this.servers.put(group, servers);
    }
}
