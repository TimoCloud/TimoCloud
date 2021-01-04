package cloud.timo.TimoCloud.bungeecord.listeners;

import cloud.timo.TimoCloud.api.events.EventHandler;
import cloud.timo.TimoCloud.api.events.Listener;
import cloud.timo.TimoCloud.api.events.serverGroup.ServerGroupCreatedEvent;
import cloud.timo.TimoCloud.bungeecord.TimoCloudBungee;

public class ServerGroupCreatedListener implements Listener {

    @EventHandler
    public void onServerGroupCreated(ServerGroupCreatedEvent event) {
        TimoCloudBungee.getInstance().getTimoCloudCommand().addServerGroupName(event.getServerGroup().getName());
    }
}
