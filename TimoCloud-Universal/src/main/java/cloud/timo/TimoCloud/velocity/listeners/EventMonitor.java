package cloud.timo.TimoCloud.velocity.listeners;

import cloud.timo.TimoCloud.api.events.player.PlayerConnectEventBasicImplementation;
import cloud.timo.TimoCloud.api.events.player.PlayerDisconnectEventBasicImplementation;
import cloud.timo.TimoCloud.api.events.player.PlayerServerChangeEventBasicImplementation;
import cloud.timo.TimoCloud.api.objects.PlayerObject;
import cloud.timo.TimoCloud.common.events.EventTransmitter;
import cloud.timo.TimoCloud.velocity.TimoCloudVelocity;
import cloud.timo.TimoCloud.velocity.utils.PlayerUtil;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.proxy.Player;

import java.util.*;

public class EventMonitor {

    @Subscribe
    public void onPlayerConnect(PostLoginEvent event) {
        TimoCloudVelocity.getInstance().sendPlayerCount();
    }

    @Subscribe
    public void onServerSwitchEvent(ServerConnectedEvent event) {
        if (!event.getPreviousServer().isPresent()) { // Join
            EventTransmitter.sendEvent(new PlayerConnectEventBasicImplementation(PlayerUtil.playerToObject(event.getPlayer(), event.getServer())));
        } else { // Server change
            EventTransmitter.sendEvent(new PlayerServerChangeEventBasicImplementation(
                    PlayerUtil.playerToObject(event.getPlayer(), event.getServer()),
                    event.getPreviousServer().get().getServerInfo().getName(),
                    event.getServer().getServerInfo().getName()));
        }
    }

    @Subscribe
    public void onPlayerQuitEvent(DisconnectEvent event) {
        TimoCloudVelocity.getInstance().sendPlayerCount();
        if (!event.getLoginStatus().equals(DisconnectEvent.LoginStatus.SUCCESSFUL_LOGIN)) return;
        EventTransmitter.sendEvent(new PlayerDisconnectEventBasicImplementation(getPlayer(event.getPlayer())));
    }

    private PlayerObject getPlayer(Player player) {
        return PlayerUtil.playerToObject(player);
    }

}
