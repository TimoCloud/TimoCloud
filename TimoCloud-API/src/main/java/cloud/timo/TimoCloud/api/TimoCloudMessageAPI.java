package cloud.timo.TimoCloud.api;

import cloud.timo.TimoCloud.api.messages.listeners.MessageListener;
import cloud.timo.TimoCloud.api.messages.objects.AddressedPluginMessage;
import cloud.timo.TimoCloud.api.messages.objects.MessageClientAddress;
import cloud.timo.TimoCloud.api.messages.objects.PluginMessage;

/**
 * Use {@link TimoCloudAPI#getMessageAPI()} to get an instance of this API
 */
public interface TimoCloudMessageAPI {

    /**
     * Sends an addressed message to the given recipient
     * @param message The message which shall be sent
     */
    void sendMessage(AddressedPluginMessage message);

    /**
     * Sends a message to the core
     * @param message The message which shall be sent
     */
    void sendMessageToCore(PluginMessage message);

    /**
     * Sends a message to the server with the given name
     * @param message The message which shall be sent
     * @param serverName The server the message shall be sent to
     */
    void sendMessageToServer(PluginMessage message, String serverName);

    /**
     * Sends a message to the proxy with the given name
     * @param message The message which shall be sent
     * @param proxyName The proxy the message shall be sent to
     */
    void sendMessageToProxy(PluginMessage message, String proxyName);

    /**
     * Sends a message to the cord with the given name
     * @param message The message which shall be sent
     * @param cordName The cord the message shall be sent to
     */
    void sendMessageToCord(PluginMessage message, String cordName);

    /**
     * Registers a message listener
     * @param listener The listener which shall be registered
     * @param supportedMessageTypes The message types the listener accepts. Leave empty for all types.
     */
    void registerMessageListener(MessageListener listener, String ... supportedMessageTypes);

    /**
     * Unregisters a message listener so that it no longer gets notified of incoming protocol.
     * This might be useful when your plugin gets reloaded, so that your old listener's instance does not receive protocol anymore
     * @param listener The listener which shall be unregistered
     */
    void unregisterMessageListener(MessageListener listener);

    /**
     * Returns the message client address of the TimoCloud application you are on.
     * You usually don't need this, as the sender's address will be set automatically by TimoCloud
     * @return The message client address to which protocol you shall receive can be sent
     */
    MessageClientAddress getOwnAddress();
}
