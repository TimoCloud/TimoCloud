package cloud.timo.TimoCloud.api.implementations;

import cloud.timo.TimoCloud.api.TimoCloudMessageAPI;
import cloud.timo.TimoCloud.api.internal.TimoCloudInternalAPI;
import cloud.timo.TimoCloud.api.messages.internal.TypeSpecificMessageListener;
import cloud.timo.TimoCloud.api.messages.listeners.MessageListener;
import cloud.timo.TimoCloud.api.messages.objects.*;
import cloud.timo.TimoCloud.lib.objects.JSONBuilder;
import cloud.timo.TimoCloud.lib.utils.PluginMessageSerializer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class TimoCloudMessageAPIBasicImplementation implements TimoCloudMessageAPI {

    private List<TypeSpecificMessageListener> listeners;

    public TimoCloudMessageAPIBasicImplementation() {
        listeners = new ArrayList<>();
    }

    @Override
    public void sendMessage(AddressedPluginMessage message) {
        TimoCloudInternalAPI.getInternalMessageAPI().sendMessageToCore(JSONBuilder.create()
                .setType("PLUGIN_MESSAGE")
                .setData(PluginMessageSerializer.serialize(message))
                .toString());
    }

    @Override
    public void sendMessageToCore(PluginMessage message) {
        sendMessage(new AddressedPluginMessage(MessageClientAddress.CORE, message));
    }

    @Override
    public void sendMessageToServer(PluginMessage message, String serverName) {
        sendMessage(new AddressedPluginMessage(new MessageClientAddress(serverName, MessageClientAddressType.SERVER), message));
    }

    @Override
    public void sendMessageToProxy(PluginMessage message, String proxyName) {
        sendMessage(new AddressedPluginMessage(new MessageClientAddress(proxyName, MessageClientAddressType.PROXY), message));
    }

    @Override
    public void sendMessageToCord(PluginMessage message, String cordName) {
        sendMessage(new AddressedPluginMessage(new MessageClientAddress(cordName, MessageClientAddressType.CORD), message));
    }

    @Override
    public void registerMessageListener(MessageListener listener, String... supportedMessageTypes) {
        listeners.add(new TypeSpecificMessageListener(listener, supportedMessageTypes));
    }

    public List<TypeSpecificMessageListener> getListeners() {
        return listeners;
    }

    public List<TypeSpecificMessageListener> getListeners(String type) {
        return getListeners().stream().filter(listener -> listener.isTypeSupported(type)).collect(Collectors.toList());
    }

    public void onMessage(AddressedPluginMessage message) {
        for (TypeSpecificMessageListener listener : getListeners(message.getMessage().getType())) {
            try {
                listener.getListener().onPluginMessage(message);
            } catch (Exception e) {
                System.err.println("Uncaught exception while calling method onPluginMessage: ");
                e.printStackTrace();
            }
        }
    }

}
