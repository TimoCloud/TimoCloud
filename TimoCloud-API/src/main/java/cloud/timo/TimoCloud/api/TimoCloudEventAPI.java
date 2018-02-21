package cloud.timo.TimoCloud.api;

import cloud.timo.TimoCloud.api.events.Listener;

public interface TimoCloudEventAPI {

    void registerListener(Listener listener);
    void unregisterListener(Listener listener);

}
