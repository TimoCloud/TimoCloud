package cloud.timo.TimoCloud.common.manager;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.objects.PlayerObject;
import cloud.timo.TimoCloud.api.objects.ServerGroupObject;
import cloud.timo.TimoCloud.api.objects.ServerObject;
import cloud.timo.TimoCloud.base.TimoCloudBase;
import cloud.timo.TimoCloud.velocity.TimoCloudVelocity;
import cloud.timo.TimoCloud.velocity.objects.LobbyChooseStrategy;

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

    public ServerObject searchFreeLobby(UUID uuid, String notThis) {
        ServerGroupObject group = TimoCloudAPI.getUniversalAPI().getServerGroup(TimoCloudVelocity.getInstance().getFileManager().getConfig().getString("fallbackGroup"));
        if (group == null) {
            TimoCloudVelocity.getInstance().severe("Error while searching lobby: Could not find specified fallbackGroup '" + TimoCloudVelocity.getInstance().getFileManager().getConfig().getString("fallbackGroup") + "'");
            return null;
        }
        List<ServerObject> servers = group.getServers().stream()
                .filter(server -> !server.getName().equals(notThis))
                .filter(server -> server.getOnlinePlayerCount() < server.getMaxPlayerCount())
                .collect(Collectors.toList());
        List<ServerObject> removeServers = new ArrayList<>();
        ServerObject notThisServer = notThis == null ? null : TimoCloudAPI.getUniversalAPI().getServer(notThis);
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
        return target;
    }

    public ServerObject getFreeLobby(UUID uuid, boolean kicked) {
        String notThis = null;
        if (!TimoCloudVelocity.getInstance().getServer().getPlayer(uuid).isPresent()) {
            ServerObject serverObject = TimoCloudAPI.getUniversalAPI().getPlayer(uuid).getServer();
            notThis = serverObject.getName();
        }
        PlayerObject player = TimoCloudAPI.getUniversalAPI().getPlayer(uuid);
        ServerGroupObject serverGroupObject = TimoCloudAPI.getUniversalAPI().getServerGroup(TimoCloudVelocity.getInstance().getFileManager().getConfig().getString("emergencyFallback"));

        if (player != null && player.getServer() != null)
            notThis = player.getServer().getName();

        ServerObject serverObject = searchFreeLobby(uuid, notThis);
        if (serverObject == null) {
            if (serverGroupObject == null) return null;
            if (serverGroupObject.getServers().isEmpty()) return null;
            return serverGroupObject.getServers().stream().findFirst().get();
        }

        if (kicked) addToHistory(uuid, serverObject.getName());

        return serverObject;
    }

    public ServerObject getFreeLobby(UUID uuid) {
        return getFreeLobby(uuid, false);
    }

}
