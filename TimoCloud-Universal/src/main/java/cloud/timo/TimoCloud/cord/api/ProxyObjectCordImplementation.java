package cloud.timo.TimoCloud.cord.api;

import cloud.timo.TimoCloud.api.implementations.ProxyObjectBasicImplementation;
import cloud.timo.TimoCloud.api.objects.PlayerObject;
import cloud.timo.TimoCloud.api.objects.ProxyObject;
import cloud.timo.TimoCloud.cord.TimoCloudCord;
import lombok.NoArgsConstructor;

import java.net.InetSocketAddress;
import java.util.List;

@NoArgsConstructor
public class ProxyObjectCordImplementation extends ProxyObjectBasicImplementation implements ProxyObject {

    public ProxyObjectCordImplementation(String name, String group, String token, List<PlayerObject> onlinePlayers, int onlinePlayerCount, String base, InetSocketAddress inetSocketAddress) {
        super(name, group, token, onlinePlayers, onlinePlayerCount, base, inetSocketAddress);
    }

    @Override
    public void executeCommand(String command) {
        TimoCloudCord.getInstance().getSocketMessageManager().sendMessage("EXECUTE_COMMAND", getToken(), command);
    }

    @Override
    public void stop() {
        TimoCloudCord.getInstance().getSocketMessageManager().sendMessage("STOP_PROXY", getToken(), null);
    }
}
