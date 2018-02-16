package cloud.timo.TimoCloud.api;

import cloud.timo.TimoCloud.api.events.Listener;

import java.util.List;

public interface TimoCloudEventAPI {

    void registerListener(Listener listener);
    void unregisterListener(Listener listener);

}
