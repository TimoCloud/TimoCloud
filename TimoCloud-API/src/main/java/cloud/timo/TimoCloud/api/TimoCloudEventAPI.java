package cloud.timo.TimoCloud.api;

import cloud.timo.TimoCloud.api.events.Listener;

/**
 * Use {@link TimoCloudAPI#getEventAPI()} to get an instance of this API
 */
public interface TimoCloudEventAPI {

    /**
     * Registers an event listener which will be notified on events
     *
     * @param listener The event listener which shall be registered
     */
    void registerListener(Listener listener);

    /**
     * Unregisters a registered event listener which will no longer be notified on events
     *
     * @param listener The event listener which shall be unregistered
     */
    void unregisterListener(Listener listener);

}
