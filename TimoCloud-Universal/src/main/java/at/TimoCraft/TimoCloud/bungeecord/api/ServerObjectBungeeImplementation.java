package at.TimoCraft.TimoCloud.bungeecord.api;

import at.TimoCraft.TimoCloud.api.objects.GroupObject;
import at.TimoCraft.TimoCloud.api.objects.ServerObject;
import at.TimoCraft.TimoCloud.api.objects.ServerObjectBasicImplementation;
import at.TimoCraft.TimoCloud.bungeecord.TimoCloud;
import at.TimoCraft.TimoCloud.bungeecord.objects.Server;

import java.net.InetSocketAddress;

public class ServerObjectBungeeImplementation extends ServerObjectBasicImplementation implements ServerObject {

    public ServerObjectBungeeImplementation(String name, String group, String token, String state, String extra, String map, String motd, int currentPlayers, int maxPlayers, InetSocketAddress socketAddress) {
        super(name, group, token, state, extra, map, motd, currentPlayers, maxPlayers, socketAddress);
    }

    @Override
    public void setState(String state) {
        Server server = TimoCloud.getInstance().getServerManager().getServerByToken(getToken());
        if (server == null) {
            TimoCloud.severe("&cCould not set state per API access for server " + getName() + ": Server does not exist anymore.");
            return;
        }
        server.setState(state);
    }

    @Override
    public void setExtra(String extra) {
        Server server = TimoCloud.getInstance().getServerManager().getServerByToken(getToken());
        if (server == null) {
            TimoCloud.severe("&cCould not set extra per API access for server " + getName() + ": Server does not exist anymore.");
            return;
        }
        server.setState(extra);
    }

}
