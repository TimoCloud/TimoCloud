package cloud.timo.TimoCloud.core.api;

import cloud.timo.TimoCloud.api.implementations.ServerObjectBasicImplementation;
import cloud.timo.TimoCloud.api.objects.ServerObject;
import cloud.timo.TimoCloud.core.TimoCloudCore;
import cloud.timo.TimoCloud.core.objects.Server;

import java.net.InetSocketAddress;

public class ServerObjectCoreImplementation extends ServerObjectBasicImplementation implements ServerObject {

    public ServerObjectCoreImplementation(String name, String group, String token, String state, String extra, String map, String motd, int currentPlayers, int maxPlayers, String base, InetSocketAddress socketAddress) {
        super(name, group, token, state, extra, map, motd, currentPlayers, maxPlayers, base, socketAddress);
    }

    private Server getServer() {
        return TimoCloudCore.getInstance().getServerManager().getServerByToken(getToken());
    }

    @Override
    public void setState(String state) {
        Server server = getServer();
        if (server == null) {
            TimoCloudCore.getInstance().severe("&cCould not set state per API access for server " + getName() + ": Server does not exist anymore.");
            return;
        }
        this.state = state;
        server.setState(state);
    }

    @Override
    public void setExtra(String extra) {
        Server server = getServer();
        if (server == null) {
            TimoCloudCore.getInstance().severe("&cCould not set extra per API access for server " + getName() + ": Server does not exist anymore.");
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
