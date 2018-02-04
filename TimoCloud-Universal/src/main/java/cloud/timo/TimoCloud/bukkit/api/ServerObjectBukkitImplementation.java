package cloud.timo.TimoCloud.bukkit.api;

import cloud.timo.TimoCloud.api.objects.ServerGroupObject;
import cloud.timo.TimoCloud.api.objects.ServerObject;
import cloud.timo.TimoCloud.api.implementations.ServerObjectBasicImplementation;
import cloud.timo.TimoCloud.bukkit.TimoCloudBukkit;

import java.net.InetSocketAddress;

public class ServerObjectBukkitImplementation extends ServerObjectBasicImplementation implements ServerObject {

    public ServerObjectBukkitImplementation() {}

    public ServerObjectBukkitImplementation(String name, String group, String token, String state, String extra, String map, String motd, int currentPlayers, int maxPlayers, String base, InetSocketAddress socketAddress) {
        super(name, group, token, state, extra, map, motd, currentPlayers, maxPlayers, base, socketAddress);
    }

    public ServerObjectBukkitImplementation(ServerObjectBasicImplementation serverObjectBasicImplementation) {
        this(
                serverObjectBasicImplementation.getName(),
                serverObjectBasicImplementation.getGroup().getName(),
                serverObjectBasicImplementation.getToken(),
                serverObjectBasicImplementation.getState(),
                serverObjectBasicImplementation.getExtra(),
                serverObjectBasicImplementation.getMap(),
                serverObjectBasicImplementation.getMotd(),
                serverObjectBasicImplementation.getOnlinePlayerCount(),
                serverObjectBasicImplementation.getMaxPlayerCount(),
                serverObjectBasicImplementation.getBase(),
                serverObjectBasicImplementation.getSocketAddress()
        );
    }

    @Override
    public void setState(String state) {
        this.state = state;
        TimoCloudBukkit.getInstance().getSocketMessageManager().sendMessage("SET_STATE", getName(), state);
    }

    @Override
    public void setExtra(String extra) {
        this.extra = extra;
        TimoCloudBukkit.getInstance().getSocketMessageManager().sendMessage("SET_EXTRA", getName(), extra);
    }

    @Override
    public void executeCommand(String command) {
        TimoCloudBukkit.getInstance().getSocketMessageManager().sendMessage("REDIRECT_COMMAND", getName(), command);
    }

    @Override
    public void stop() {
        TimoCloudBukkit.getInstance().getSocketMessageManager().sendMessage("STOP_SERVER", getName(), "");
    }
}
