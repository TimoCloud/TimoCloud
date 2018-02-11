package cloud.timo.TimoCloud.bukkit.api;

import cloud.timo.TimoCloud.api.implementations.ProxyObjectBasicImplementation;
import cloud.timo.TimoCloud.api.objects.ProxyObject;
import cloud.timo.TimoCloud.bukkit.TimoCloudBukkit;

import java.net.InetSocketAddress;

public class ProxyObjectBukkitImplementation extends ProxyObjectBasicImplementation implements ProxyObject {

    public ProxyObjectBukkitImplementation() { }

    public ProxyObjectBukkitImplementation(String name, String group, String token, int onlinePlayerCount, InetSocketAddress inetSocketAddress) {
        super(name, group, token, onlinePlayerCount, inetSocketAddress);
    }

    @Override
    public void executeCommand(String command) {
        TimoCloudBukkit.getInstance().getSocketMessageManager().sendMessage("EXECUTE_COMMAND", getToken(), command);
    }

    @Override
    public void stop() {
        TimoCloudBukkit.getInstance().getSocketMessageManager().sendMessage("STOP_PROXY", getToken(), null);
    }
}
