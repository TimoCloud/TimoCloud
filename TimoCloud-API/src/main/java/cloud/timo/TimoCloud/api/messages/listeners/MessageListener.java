package cloud.timo.TimoCloud.api.messages.listeners;

import cloud.timo.TimoCloud.api.messages.objects.AddressedPluginMessage;

public interface MessageListener {

    /**
     * This method will be called when a message is received
     *
     * @param message The message we got
     */
    void onPluginMessage(AddressedPluginMessage message);

}
