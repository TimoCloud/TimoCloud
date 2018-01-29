package cloud.timo.TimoCloud.bungeecord.managers;

import cloud.timo.TimoCloud.bungeecord.TimoCloudBungee;
import cloud.timo.TimoCloud.core.objects.ServerGroup;
import cloud.timo.TimoCloud.bungeecord.objects.LobbyChooseStrategy;
import cloud.timo.TimoCloud.core.objects.Server;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.*;
import java.util.stream.Collectors;

public class LobbyManager {

    private static final long INVALIDATE_CACHE_TIME = 5000;

    private Map<UUID, List<ServerInfo>> lobbyHistory;
    private Map<UUID, Long> lastUpdate;

    public LobbyManager() {
        lobbyHistory = new HashMap<>();
        lastUpdate = new HashMap<>();
    }

    private List<ServerInfo> getVisitedLobbies(UUID uuid) {
        lobbyHistory.putIfAbsent(uuid, new ArrayList<>());
        lastUpdate.putIfAbsent(uuid, 0L);
        if (new Date().getTime()-lastUpdate.get(uuid) >= INVALIDATE_CACHE_TIME) {
            lobbyHistory.put(uuid, new ArrayList<>());
        }
        lastUpdate.put(uuid, new Date().getTime());
        return lobbyHistory.get(uuid);
    }

    public void addToHistory(UUID uuid, ServerInfo server) {
        lobbyHistory.putIfAbsent(uuid, new ArrayList<>());
        lobbyHistory.get(uuid).add(server);
    }

    private LobbyChooseStrategy getLobbyChooseStrategy() {
        LobbyChooseStrategy lobbyChooseStrategy = LobbyChooseStrategy.valueOf(TimoCloudBungee.getInstance().getFileManager().getConfig().getString("LobbyChooseStrategy"));
        return lobbyChooseStrategy == null ? LobbyChooseStrategy.RANDOM : lobbyChooseStrategy;
    }

    private ServerInfo searchFreeLobby(UUID uuid, ServerInfo notThis) {
        ServerGroup group = TimoCloudBungee.getInstance().getServerManager().getGroupByName(TimoCloudBungee.getInstance().getFileManager().getConfig().getString("fallbackGroup"));
        List<Server> servers = new ArrayList<>(notThis == null ? group.getServers() : group.getServers().stream().filter(server -> !server.getName().equals(notThis.getName())).collect(Collectors.toList()));
        List<Server> removeServers = new ArrayList<>();
        Server notThisServer = notThis == null ? null : TimoCloudBungee.getInstance().getServerManager().getServerByName(notThis.getName());
        if (notThisServer != null) removeServers.add(notThisServer);
        List<ServerInfo> history = getVisitedLobbies(uuid);

        for (Server server : servers) {
            if (history.contains(server.getServerInfo()) && ! removeServers.contains(server)) removeServers.add(server);
        }
        servers.removeAll(removeServers);

        if (servers.size() == 0) {
            return null;
        }

        servers.sort(Comparator.comparingInt(Server::getCurrentPlayers));
        switch (getLobbyChooseStrategy()) {
            case RANDOM:
                return servers.get(new Random().nextInt(servers.size())).getServerInfo();
            case FILL:
                for (int i = servers.size()-1; i>= 0; i--) {
                    Server server = servers.get(i);
                    if (server.getCurrentPlayers() < server.getMaxPlayers()) return server.getServerInfo();
                }
                break;
            case BALANCE:
                return servers.get(0).getServerInfo();
        }
        return servers.get(new Random().nextInt(servers.size())).getServerInfo();
    }

    public ServerInfo getFreeLobby(UUID uuid, boolean kicked) {
        ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(uuid);
        ServerInfo notThis = null;
        if (proxiedPlayer != null && proxiedPlayer.getServer() != null) notThis = proxiedPlayer.getServer().getInfo();

        ServerInfo serverInfo = searchFreeLobby(uuid, notThis);
        if (serverInfo == null) {
            return TimoCloudBungee.getInstance().getProxy().getServerInfo(TimoCloudBungee.getInstance().getFileManager().getConfig().getString("emergencyFallback"));
        }

        if (kicked) addToHistory(uuid, serverInfo);

        return serverInfo;
    }

    public ServerInfo getFreeLobby(UUID uuid) {
        return getFreeLobby(uuid, false);
    }

}
