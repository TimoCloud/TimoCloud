package cloud.timo.TimoCloud.api.events.player;

import cloud.timo.TimoCloud.api.events.Event;
import cloud.timo.TimoCloud.api.objects.PlayerObject;

public interface PlayerConnectEvent extends Event {

    PlayerObject getPlayer();

}
