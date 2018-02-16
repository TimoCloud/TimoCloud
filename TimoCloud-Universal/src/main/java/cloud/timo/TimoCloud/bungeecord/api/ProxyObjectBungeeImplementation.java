package cloud.timo.TimoCloud.bungeecord.api;

import cloud.timo.TimoCloud.api.implementations.ProxyObjectBasicImplementation;
import cloud.timo.TimoCloud.api.objects.PlayerObject;
import cloud.timo.TimoCloud.api.objects.ProxyObject;
import cloud.timo.TimoCloud.bungeecord.TimoCloudBungee;

import java.net.InetSocketAddress;
import java.util.List;

public class ProxyObjectBungeeImplementation extends ProxyObjectBasicImplementation implements ProxyObject {

    public ProxyObjectBungeeImplementation() {}

    public ProxyObjectBungeeImplementation(String name, String group, String token, List<PlayerObject> onlinePlayers, int onlinePlayerCount, InetSocketAddress inetSocketAddress) {
        super(name, group, token, onlinePlayers, onlinePlayerCount, inetSocketAddress);
    }

    @Override
    public void executeCommand(String command) {
        TimoCloudBungee.getInstance().getSocketMessageManager().sendMessage("EXECUTE_COMMAND", getToken(), command);
    }

    @Override
    public void stop() {
        TimoCloudBungee.getInstance().getSocketMessageManager().sendMessage("STOP_PROXY", getToken(), null);
    }
}
