package cloud.timo.TimoCloud.bungeecord.api;

import cloud.timo.TimoCloud.api.implementations.ProxyObjectBasicImplementation;
import cloud.timo.TimoCloud.api.objects.ProxyObject;
import cloud.timo.TimoCloud.bungeecord.TimoCloudBungee;

import java.net.InetSocketAddress;

public class ProxyObjectBungeeImplementation extends ProxyObjectBasicImplementation implements ProxyObject {

    public ProxyObjectBungeeImplementation() {}

    public ProxyObjectBungeeImplementation(String name, String group, String token, int onlinePlayerCount, InetSocketAddress inetSocketAddress) {
        super(name, group, token, onlinePlayerCount, inetSocketAddress);
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
