package cloud.timo.TimoCloud.bungeecord.listeners;

import cloud.timo.TimoCloud.api.events.EventHandler;
import cloud.timo.TimoCloud.api.events.Listener;
import cloud.timo.TimoCloud.api.events.serverGroup.ServerGroupDeletedEvent;
import cloud.timo.TimoCloud.bungeecord.TimoCloudBungee;

public class ServerGroupDeletedListener implements Listener {

    @EventHandler
    public void onServerGroupDeleted(ServerGroupDeletedEvent event) {
        TimoCloudBungee.getInstance().getTimoCloudCommand().removeServerGroupName(event.getServerGroup().getName());
    }
}
