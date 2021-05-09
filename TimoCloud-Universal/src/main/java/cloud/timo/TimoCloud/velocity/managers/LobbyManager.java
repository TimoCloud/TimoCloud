package cloud.timo.TimoCloud.velocity.managers;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.objects.ServerGroupObject;
import cloud.timo.TimoCloud.api.objects.ServerObject;
import cloud.timo.TimoCloud.base.TimoCloudBase;
import cloud.timo.TimoCloud.velocity.TimoCloudVelocity;
import cloud.timo.TimoCloud.velocity.objects.LobbyChooseStrategy;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;

import java.util.*;
import java.util.stream.Collectors;

public class LobbyManager {

    private static final long INVALIDATE_CACHE_TIME = 2000;

    private final Map<UUID, List<String>> lobbyHistory;
    private final Map<UUID, Long> lastUpdate;

    public LobbyManager() {
        lobbyHistory = new HashMap<>();
        lastUpdate = new HashMap<>();
    }

    private List<String> getVisitedLobbies(UUID uuid) {
        lobbyHistory.putIfAbsent(uuid, new ArrayList<>());
        lastUpdate.putIfAbsent(uuid, 0L);
        if (new Date().getTime() - lastUpdate.get(uuid) >= INVALIDATE_CACHE_TIME) {
            lobbyHistory.put(uuid, new ArrayList<>());
        }
        lastUpdate.put(uuid, new Date().getTime());
        return lobbyHistory.get(uuid);
    }

    public void addToHistory(UUID uuid, String server) {
        lobbyHistory.putIfAbsent(uuid, new ArrayList<>());
        lobbyHistory.get(uuid).add(server);
    }

    private LobbyChooseStrategy getLobbyChooseStrategy() {
        return LobbyChooseStrategy.valueOf(TimoCloudVelocity.getInstance().getFileManager().getConfig().getString("LobbyChooseStrategy"));
    }

    public RegisteredServer searchFreeLobby(UUID uuid, ServerInfo notThis) {
        ServerGroupObject group = TimoCloudAPI.getUniversalAPI().getServerGroup(TimoCloudVelocity.getInstance().getFileManager().getConfig().getString("fallbackGroup"));
        if (group == null) {
            TimoCloudVelocity.getInstance().severe("Error while searching lobby: Could not find specified fallbackGroup '" + TimoCloudVelocity.getInstance().getFileManager().getConfig().getString("fallbackGroup") + "'");
            return null;
        }
        String notThisName = notThis == null ? "" : notThis.getName();
        List<ServerObject> servers = group.getServers().stream()
                .filter(server -> !server.getName().equals(notThisName))
                .filter(server -> server.getOnlinePlayerCount() < server.getMaxPlayerCount())
                .collect(Collectors.toList());
        List<ServerObject> removeServers = new ArrayList<>();
        ServerObject notThisServer = notThis == null ? null : TimoCloudAPI.getUniversalAPI().getServer(notThis.getName());
        if (notThisServer != null) removeServers.add(notThisServer);
        List<String> history = getVisitedLobbies(uuid);

        for (ServerObject server : servers) {
            if (history.contains(server.getName()) && !removeServers.contains(server)) removeServers.add(server);
        }
        servers.removeAll(removeServers);
        if (servers.size() == 0) {
            return null;
        }

        servers.sort(Comparator.comparingInt(ServerObject::getOnlinePlayerCount));
        ServerObject target = null;
        switch (getLobbyChooseStrategy()) {
            case RANDOM:
                target = servers.get(new Random().nextInt(servers.size()));
                break;
            case FILL:
                for (int i = servers.size() - 1; i >= 0; i--) {
                    ServerObject server = servers.get(i);
                    if (server.getOnlinePlayerCount() < server.getMaxPlayerCount()) {
                        target = server;
                        break;
                    }
                }
                break;
            case BALANCE:
                target = servers.get(0);
                break;
            default:
                TimoCloudBase.getInstance().warning("LobbyChooseStrategy error");
                break;
        }
        return TimoCloudVelocity.getInstance().getServer().getServer(target.getName()).get();
    }

    public RegisteredServer getFreeLobby(UUID uuid, boolean kicked) {
        ServerInfo notThis = null;
        if (!TimoCloudVelocity.getInstance().getServer().getPlayer(uuid).isPresent()) {
            ServerConnection serverConnection = TimoCloudVelocity.getInstance().getServer().getPlayer(uuid).get().getCurrentServer().get();
            notThis = serverConnection.getServerInfo();
        }
        Player player = TimoCloudVelocity.getInstance().getServer().getPlayer(uuid).get();
        ServerGroupObject serverGroupObject = TimoCloudAPI.getUniversalAPI().getServerGroup(TimoCloudVelocity.getInstance().getFileManager().getConfig().getString("emergencyFallback"));

        if (player != null && player.getCurrentServer().isPresent())
            notThis = player.getCurrentServer().get().getServerInfo();

        RegisteredServer registeredServer = searchFreeLobby(uuid, notThis);
        if (registeredServer == null) {
            if (serverGroupObject == null) return null;
            if (serverGroupObject.getServers().isEmpty()) return null;
            return TimoCloudVelocity.getInstance().getServer().getServer(serverGroupObject.getServers().stream().findFirst().get().getName()).get();
        }

        if (kicked) addToHistory(uuid, registeredServer.getServerInfo().getName());

        return registeredServer;
    }

    public RegisteredServer getFreeLobby(UUID uuid) {
        return getFreeLobby(uuid, false);
    }

}
