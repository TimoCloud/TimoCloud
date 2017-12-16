package at.TimoCraft.TimoCloud.bungeecord.api;

import at.TimoCraft.TimoCloud.api.objects.ServerObject;
import at.TimoCraft.TimoCloud.api.implementations.ServerObjectBasicImplementation;
import at.TimoCraft.TimoCloud.bungeecord.TimoCloud;
import at.TimoCraft.TimoCloud.bungeecord.objects.Server;

import java.net.InetSocketAddress;

public class ServerObjectBungeeImplementation extends ServerObjectBasicImplementation implements ServerObject {

    public ServerObjectBungeeImplementation(String name, String group, String token, String state, String extra, String map, String motd, int currentPlayers, int maxPlayers, InetSocketAddress socketAddress) {
        super(name, group, token, state, extra, map, motd, currentPlayers, maxPlayers, socketAddress);
    }

    private Server getServer() {
        return TimoCloud.getInstance().getServerManager().getServerByToken(getToken());
    }

    @Override
    public void setState(String state) {
        Server server = getServer();
        if (server == null) {
            TimoCloud.severe("&cCould not set state per API access for server " + getName() + ": Server does not exist anymore.");
            return;
        }
        this.state = state;
        server.setState(state);
    }

    @Override
    public void setExtra(String extra) {
        Server server = getServer();
        if (server == null) {
            TimoCloud.severe("&cCould not set extra per API access for server " + getName() + ": Server does not exist anymore.");
            return;
        }
        this.extra = extra;
        server.setState(extra);
    }

    @Override
    public void executeCommand(String command) {
        getServer().executeCommand(command);
    }

    @Override
    public void stop() {
        getServer().stop();
    }

}
