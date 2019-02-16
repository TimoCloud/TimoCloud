package cloud.timo.TimoCloud.api.events.player;

import cloud.timo.TimoCloud.api.events.Event;
import cloud.timo.TimoCloud.api.objects.PlayerObject;
import cloud.timo.TimoCloud.api.objects.ServerObject;

public interface PlayerServerChangeEvent extends Event {

    PlayerObject getPlayer();

    ServerObject getServerFrom();

    ServerObject getServerTo();
}
