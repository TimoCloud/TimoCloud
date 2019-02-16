package cloud.timo.TimoCloud.api.implementations.listeners;

import cloud.timo.TimoCloud.api.events.EventHandler;
import cloud.timo.TimoCloud.api.events.Listener;
import cloud.timo.TimoCloud.api.events.base.BaseAddressChangeEvent;
import cloud.timo.TimoCloud.api.events.base.BaseAddressChangeEventBasicImplementation;
import cloud.timo.TimoCloud.api.implementations.TimoCloudUniversalAPIBasicImplementation;
import cloud.timo.TimoCloud.api.implementations.objects.BaseObjectBasicImplementation;

public class TimoCloudUniversalAPIStorageUpdateListener implements Listener {

    private final TimoCloudUniversalAPIBasicImplementation api;

    public TimoCloudUniversalAPIStorageUpdateListener(TimoCloudUniversalAPIBasicImplementation api) {
        this.api = api;
    }

    @EventHandler
    public void onBaseAddressChangeEvent(BaseAddressChangeEvent event) {
        ((BaseObjectBasicImplementation) ((BaseAddressChangeEventBasicImplementation) event).getInstance()).setIpAddress(event.getNewValue());
    }

}
